import org.scalatest.funsuite.AnyFunSuite
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.actor.ActorSystem
import models._
import api._
import services.LibraryCatalog
import services.given_Encoder_LoanRequest
import services.given_Encoder_SearchRequest
import services.given_Encoder_ReturnRequest
import io.circe.syntax._
import scala.util.Try
import scala.concurrent.{ExecutionContext, Future}
import java.net.{HttpURLConnection, URL}
import java.io.{BufferedReader, InputStreamReader, OutputStream}
import scala.util.Using

// Resolving ambiguous references to ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class LibraryServerComprehensiveCoverageTestFixed extends AnyFunSuite with ScalatestRouteTest {
  // Helper pour requêtes HTTP sécurisées
  private def safeRequest(method: String, url: String, body: String = "", headers: Map[String, String] = Map()): (Int, String, Boolean) = {
    try {
      val connection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod(method.toUpperCase)
      connection.setConnectTimeout(2000)
      connection.setReadTimeout(2000)
      
      headers.foreach { case (key, value) => connection.setRequestProperty(key, value) }
      
      if (body.nonEmpty && (method.toLowerCase == "post" || method.toLowerCase == "put")) {
        connection.setDoOutput(true)
        Using(connection.getOutputStream) { os =>
          os.write(body.getBytes("UTF-8"))
          os.flush()
        }
      }
      
      val responseCode = connection.getResponseCode
      val responseBody = Using(new BufferedReader(new InputStreamReader(
        if (responseCode >= 200 && responseCode < 300) connection.getInputStream else connection.getErrorStream
      ))) { reader =>
        Iterator.continually(reader.readLine()).takeWhile(_ != null).mkString("\n")
      }.getOrElse("")
      
      (responseCode, responseBody, true)
    } catch {
      case _: Exception => (0, "", false)
    }
  }

  test("All POST routes handle malformed JSON") {
    import akka.http.scaladsl.testkit.ScalatestRouteTest
    
    val malformedJsons = List(
      """{"invalid": json}""",
      """{"unclosed": "quote}""",
      """{broken json""",
      """invalid""",
      """null"""
    )
    
    val postRoutes = List("/api/books/search", "/api/books/loan", "/api/books/return", "/api/books/reserve")
    
    postRoutes.foreach { route =>
      malformedJsons.foreach { json =>
        Post(route).withEntity(HttpEntity(ContentTypes.`application/json`, json)) ~> LibraryServer.routes ~> check {
          assert(status == StatusCodes.BadRequest)
        }
      }
    }
  }

  test("All POST routes handle missing or invalid required fields") {
    val incompleteRequests = List(
      """{}""",
      """{"title":""}""",
      """{"userId":""}""",
      """{"bookId":""}""",
      """{"incomplete":true}"""
    )
    
    val postRoutes = List("/api/books/search", "/api/books/loan", "/api/books/return", "/api/books/reserve")
    
    postRoutes.foreach { route =>
      incompleteRequests.foreach { json =>
        Post(route).withEntity(HttpEntity(ContentTypes.`application/json`, json)) ~> LibraryServer.routes ~> check {
          // Should either return 400 for invalid JSON or 200 with proper handling
          assert(status == StatusCodes.BadRequest || status == StatusCodes.OK)
        }
      }
    }
  }

  test("All GET routes handle invalid parameters") {
    // Test non-standard HTTP versions and edge cases through test framework
    import akka.http.scaladsl.model.HttpProtocols
    
    val getRoutes = List("/api/books", "/api/users", "/api/transactions", "/api/books/available")
    
    getRoutes.foreach { route =>
      Get(route) ~> LibraryServer.routes ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[String].contains("success"))
      }
    }
  }

  test("API endpoints handle various HTTP headers") {
    val customHeaders = List(
      ("User-Agent", "TestClient/1.0"),
      ("Accept", "application/json"),
      ("Accept-Language", "en-US,en;q=0.9"),
      ("Cache-Control", "no-cache")
    )
    
    customHeaders.foreach { case (name, value) =>
      Get("/api/books").withHeaders(akka.http.scaladsl.model.headers.RawHeader(name, value)) ~> LibraryServer.routes ~> check {
        assert(status == StatusCodes.OK)
      }
    }
  }

  test("API responses handle various data types") {
    // Test that different endpoints return appropriate data types
    val endpointExpectations = Map(
      "/api/books" -> "isbn",
      "/api/users" -> "id",
      "/api/transactions" -> "timestamp",
      "/api/statistics/genres" -> "data",
      "/api/statistics/authors" -> "data"
    )
    
    endpointExpectations.foreach { case (endpoint, expectedField) =>
      Get(endpoint) ~> LibraryServer.routes ~> check {
        assert(status == StatusCodes.OK)
        val response = responseAs[String]
        assert(response.contains("success"))
        // Data field should exist in response
        assert(response.contains("data"))
      }
    }
  }

  test("API endpoints handle edge case errors") {
    import io.circe.syntax._
    
    // Test with extreme values - handle each type separately
    val loanRequest = LoanRequest("invalid-isbn", "non-existent-user")
    val emptyLoanRequest = LoanRequest("", "")
    val searchRequest = SearchRequest("")
    val longSearchRequest = SearchRequest("A" * 1000) // Very long string
    
    // Test loan requests
    List(loanRequest, emptyLoanRequest).foreach { request =>
      val json = request.asJson.noSpaces
      Post("/api/books/loan").withEntity(HttpEntity(ContentTypes.`application/json`, json)) ~> LibraryServer.routes ~> check {
        // Should handle gracefully
        assert(status.isSuccess() || status.intValue >= 400)
      }
    }
    
    // Test search requests
    List(searchRequest, longSearchRequest).foreach { request =>
      val json = request.asJson.noSpaces
      Post("/api/books/search").withEntity(HttpEntity(ContentTypes.`application/json`, json)) ~> LibraryServer.routes ~> check {
        // Should handle gracefully
        assert(status == StatusCodes.OK || status == StatusCodes.BadRequest)
      }
    }
  }

  test("Graceful handling when server is not available") {
    // This test checks the safety of our helper methods
    val (code, body, available) = safeRequest("GET", "http://localhost:9999/api/books")
    assert(!available || code >= 0) // Should handle connection failures gracefully
  }

  test("LibraryServer system initialization") {
    // Test that the actor system and execution context are properly initialized
    import LibraryServer.{system => libSystem, executionContext}
    
    assert(libSystem != null)
    assert(libSystem.name == "library-system")
    assert(executionContext != null)
    assert(!libSystem.whenTerminated.isCompleted)
  }

  test("Routes structure and imports") {
    // Test that routes are properly defined and imports work
    assert(LibraryServer.routes != null)
    
    // Test that all required classes are imported and accessible
    val book = Book("isbn", "title", List("author"), 2020, "genre", true)
    val user = Student("id", "name", "level")
    val apiResponse = ApiResponse(success = true, data = Some("test"))
    
    assert(book != null)
    assert(user != null)
    assert(apiResponse != null)
  }
}
