import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.circe.syntax._
import io.circe.parser._
import io.circe.{Encoder, Decoder}
import io.circe.generic.auto._

import models._
import services.{LibraryCatalog, given_Encoder_LibraryCatalog, given_Decoder_LibraryCatalog}
import utils.JsonIO

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class LoanRequest(userId: String, bookId: String)
case class ReturnRequest(userId: String, bookId: String)
case class SearchRequest(title: String)
case class RecommendationRequest(userId: String)
case class ApiResponse[T](success: Boolean, data: Option[T] = None, message: Option[String] = None)

object LibraryServer {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "library-system")
  implicit val executionContext: ExecutionContext = system.executionContext

  private val jsonPath = "Data/Library.json"
  private var catalog = loadCatalog()

  private def loadCatalog(): LibraryCatalog = {
    JsonIO.loadFromFile[LibraryCatalog](jsonPath)
      .getOrElse(LibraryCatalog(Nil, Nil, Nil))
      .synchronizeBookAvailability
  }

  private def saveCatalog(newCatalog: LibraryCatalog): Unit = {
    JsonIO.saveToFile(newCatalog, jsonPath)
    catalog = newCatalog
  }

  private def jsonResponse[T: Encoder](data: T, status: StatusCode = StatusCodes.OK): HttpResponse = {
    HttpResponse(
      status = status,
      entity = HttpEntity(ContentTypes.`application/json`, data.asJson.noSpaces)
    )
  }

  val routes: Route =
    
    pathSingleSlash {
      getFromFile("public/index.html")
    } ~
    pathPrefix("static") {
      getFromDirectory("public")
    } ~
    pathPrefix("api") {
      concat(
        path("books") {
          get {
            complete(jsonResponse(ApiResponse[List[Book]](success = true, data = Some(catalog.books))))
          }
        },
        path("books" / "available") {
          get {
            complete(jsonResponse(ApiResponse[List[Book]](success = true, data = Some(catalog.availableBooks))))
          }
        },
        
        path("books" / "search") {
          post {
            entity(as[String]) { jsonString =>
              decode[SearchRequest](jsonString) match {
                case Right(request) =>
                  val results = catalog.findByTitle(request.title)
                  complete(jsonResponse(ApiResponse[List[Book]](success = true, data = Some(results))))
                case Left(_) =>
                  complete(jsonResponse(
                    ApiResponse[List[Book]](success = false, message = Some("Requête JSON invalide")),
                    StatusCodes.BadRequest
                  ))
              }
            }
          }
        },
        // POST /api/books/loan - Emprunter un livre
        path("books" / "loan") {
          post {
            entity(as[String]) { jsonString =>
              decode[LoanRequest](jsonString) match {
                case Right(request) =>
                  catalog.loanBook(request.bookId, request.userId) match {
                    case Right(updatedCatalog) =>
                      saveCatalog(updatedCatalog)
                      complete(jsonResponse(ApiResponse[String](success = true, message = Some("Livre emprunté avec succès"))))
                    case Left(error) =>
                      complete(jsonResponse(
                        ApiResponse[String](success = false, message = Some(error)),
                        StatusCodes.BadRequest
                      ))
                  }
                case Left(_) =>
                  complete(jsonResponse(
                    ApiResponse[String](success = false, message = Some("Requête JSON invalide")),
                    StatusCodes.BadRequest
                  ))
              }
            }
          }
        },
        path("books" / "return") {
          post {
            entity(as[String]) { jsonString =>
              decode[ReturnRequest](jsonString) match {
                case Right(request) =>
                  catalog.returnBook(request.bookId, request.userId) match {
                    case Right(updatedCatalog) =>
                      saveCatalog(updatedCatalog)
                      complete(jsonResponse(ApiResponse[String](success = true, message = Some("Livre retourné avec succès"))))
                    case Left(error) =>
                      complete(jsonResponse(
                        ApiResponse[String](success = false, message = Some(error)),
                        StatusCodes.BadRequest
                      ))
                  }
                case Left(_) =>
                  complete(jsonResponse(
                    ApiResponse[String](success = false, message = Some("Requête JSON invalide")),
                    StatusCodes.BadRequest
                  ))
              }
            }
          }
        },
        path("users" / Segment / "recommendations") { userId =>
          get {
            val recommendations = catalog.recommendBooks(userId)
            complete(jsonResponse(ApiResponse[List[Book]](success = true, data = Some(recommendations))))
          }
        },
        path("users") {
          get {
            complete(jsonResponse(ApiResponse[List[User]](success = true, data = Some(catalog.users))))
          }
        },
        path("transactions") {
          get {
            complete(jsonResponse(ApiResponse[List[Transaction]](success = true, data = Some(catalog.transactions))))
          }
        }
      )
    }

  def main(args: Array[String]): Unit = {
    val host = "localhost"
    val port = 8080

    val bindingFuture = Http().newServerAt(host, port).bind(routes)

    bindingFuture.onComplete {
      case Success(binding) =>
        println(s"🚀 Server started at http://$host:$port")
        println("📚 Web interface available at the above address")
        println("🔧 REST API available at /api")
        println("Press ENTER to stop the server...")
      case Failure(exception) =>
        println(s"❌ Error starting server: ${exception.getMessage}")
        system.terminate()
    }

    scala.io.StdIn.readLine()
    
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
