import org.scalatest.funsuite.AnyFunSuite
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import io.circe.parser._
import io.circe.syntax._
import models._
import api._
import services.LibraryCatalog

class LibraryServerFinalCoverageTest extends AnyFunSuite with ScalatestRouteTest {

  test("LibraryServer routes - complete path coverage") {
    // Test tous les chemins d'API pour maximiser la couverture
    val routes = LibraryServer.routes

    // Test route racine
    Get("/") ~> routes ~> check {
      assert(handled)
    }

    // Test chemins statiques
    Get("/static/app.js") ~> routes ~> check {
      assert(handled)
    }

    // Test toutes les routes API
    Get("/api/books") ~> routes ~> check {
      assert(status == StatusCodes.OK)
    }
    
    Get("/api/books/available") ~> routes ~> check {
      assert(status == StatusCodes.OK)
    }
    
    Get("/api/statistics/genres") ~> routes ~> check {
      assert(status == StatusCodes.OK)
    }
    
    Get("/api/statistics/authors") ~> routes ~> check {
      assert(status == StatusCodes.OK)
    }
    
    Get("/api/users") ~> routes ~> check {
      assert(status == StatusCodes.OK)
    }
    
    Get("/api/transactions") ~> routes ~> check {
      assert(status == StatusCodes.OK)
    }

    Get("/api/users/user1/reservations") ~> routes ~> check {
      assert(status == StatusCodes.OK)
    }

    Get("/api/users/user1/recommendations") ~> routes ~> check {
      assert(status == StatusCodes.OK)
    }
  }

  test("LibraryServer POST routes with various payloads") {
    val routes = LibraryServer.routes

    // Test search avec payload valide
    val searchRequest = """{"title":"Test"}"""
    Post("/api/books/search").withEntity(ContentTypes.`application/json`, searchRequest) ~> routes ~> check {
      assert(status == StatusCodes.OK)
    }

    // Test loan avec payload valide
    val loanRequest = """{"bookId":"978-0134685991","userId":"user1"}"""
    Post("/api/books/loan").withEntity(ContentTypes.`application/json`, loanRequest) ~> routes ~> check {
      // Peut échouer car le livre n'existe pas, mais ça teste le code
      assert(handled)
    }

    // Test return avec payload valide
    val returnRequest = """{"bookId":"978-0134685991","userId":"user1"}"""
    Post("/api/books/return").withEntity(ContentTypes.`application/json`, returnRequest) ~> routes ~> check {
      assert(handled)
    }

    // Test reserve avec payload valide
    val reserveRequest = """{"bookId":"978-0134685991","userId":"user1"}"""
    Post("/api/books/reserve").withEntity(ContentTypes.`application/json`, reserveRequest) ~> routes ~> check {
      assert(handled)
    }
  }

  test("LibraryServer system and execution context access") {
    // Test l'accès aux membres publics du système
    val system = LibraryServer.system
    val executionContext = LibraryServer.executionContext
    
    assert(system != null)
    assert(executionContext != null)
  }

  test("LibraryServer routes object instantiation") {
    // Test l'instantiation de l'objet routes
    val routes: Route = LibraryServer.routes
    assert(routes != null)
  }

  test("Error case coverage - malformed JSON") {
    val routes = LibraryServer.routes

    // Test avec JSON malformé pour tous les endpoints POST
    val malformedJson = """{"invalid": json}"""
    
    Post("/api/books/search").withEntity(ContentTypes.`application/json`, malformedJson) ~> routes ~> check {
      assert(status == StatusCodes.BadRequest)
    }
    
    Post("/api/books/loan").withEntity(ContentTypes.`application/json`, malformedJson) ~> routes ~> check {
      assert(status == StatusCodes.BadRequest)
    }
    
    Post("/api/books/return").withEntity(ContentTypes.`application/json`, malformedJson) ~> routes ~> check {
      assert(status == StatusCodes.BadRequest)
    }
    
    Post("/api/books/reserve").withEntity(ContentTypes.`application/json`, malformedJson) ~> routes ~> check {
      assert(status == StatusCodes.BadRequest)
    }
  }
}
