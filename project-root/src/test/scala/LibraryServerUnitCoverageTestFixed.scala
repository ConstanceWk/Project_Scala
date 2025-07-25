import org.scalatest.funsuite.AnyFunSuite
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import models._
import api._
import services.LibraryCatalog
import utils.JsonIO
import java.io.File
import scala.concurrent.ExecutionContext
import services.given_Encoder_SearchRequest
import services.given_Encoder_LoanRequest
import services.given_Encoder_ReturnRequest

class LibraryServerUnitCoverageTestFixed extends AnyFunSuite with ScalatestRouteTest {
  
  test("LibraryServer routes handle GET /api/books") {
    Get("/api/books") ~> LibraryServer.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType.mediaType.isApplication)
      assert(contentType.mediaType.subType == "json")
      assert(responseAs[String].contains("success"))
    }
  }

  test("LibraryServer routes handle GET /api/users") {
    Get("/api/users") ~> LibraryServer.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(responseAs[String].contains("success"))
    }
  }
  
  test("LibraryServer routes handle GET /api/transactions") {
    Get("/api/transactions") ~> LibraryServer.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(responseAs[String].contains("success"))
    }
  }

  test("LibraryServer routes handle GET /api/books/available") {
    Get("/api/books/available") ~> LibraryServer.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(responseAs[String].contains("success"))
    }
  }

  test("LibraryServer routes handle POST /api/books/search with valid JSON") {
    import io.circe.syntax._
    val searchRequest = SearchRequest("Example")
    val requestJson = searchRequest.asJson.noSpaces
    
    Post("/api/books/search").withEntity(HttpEntity(ContentTypes.`application/json`, requestJson)) ~> LibraryServer.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(responseAs[String].contains("success"))
    }
  }

  test("LibraryServer routes handle POST /api/books/search with invalid JSON") {
    val invalidJson = """{"invalid": json}"""
    Post("/api/books/search").withEntity(HttpEntity(ContentTypes.`application/json`, invalidJson)) ~> LibraryServer.routes ~> check {
      assert(status == StatusCodes.BadRequest)
      assert(responseAs[String].contains("invalide"))
    }
  }

  test("LibraryServer routes handle POST /api/books/loan") {
    import io.circe.syntax._
    val loanRequest = LoanRequest("978-00000000001", "student1")
    val requestJson = loanRequest.asJson.noSpaces
    
    Post("/api/books/loan").withEntity(HttpEntity(ContentTypes.`application/json`, requestJson)) ~> LibraryServer.routes ~> check {
      // Should either succeed or fail gracefully
      assert(status == StatusCodes.OK || status == StatusCodes.BadRequest)
    }
  }

  test("LibraryServer routes handle POST /api/books/return") {
    import io.circe.syntax._
    val returnRequest = ReturnRequest("978-00000000001", "student1")
    val requestJson = returnRequest.asJson.noSpaces
    
    Post("/api/books/return").withEntity(HttpEntity(ContentTypes.`application/json`, requestJson)) ~> LibraryServer.routes ~> check {
      // Should either succeed or fail gracefully
      assert(status == StatusCodes.OK || status == StatusCodes.BadRequest)
    }
  }

  test("LibraryServer routes handle POST /api/books/reserve") {
    import io.circe.syntax._
    val reserveRequest = LoanRequest("978-00000000001", "student1")
    val requestJson = reserveRequest.asJson.noSpaces
    
    Post("/api/books/reserve").withEntity(HttpEntity(ContentTypes.`application/json`, requestJson)) ~> LibraryServer.routes ~> check {
      // Should either succeed or fail gracefully
      assert(status == StatusCodes.OK || status == StatusCodes.BadRequest)
    }
  }

  test("LibraryServer routes handle GET /api/statistics/genres") {
    Get("/api/statistics/genres") ~> LibraryServer.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(responseAs[String].contains("success"))
    }
  }

  test("LibraryServer routes handle GET /api/statistics/authors") {
    Get("/api/statistics/authors") ~> LibraryServer.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(responseAs[String].contains("success"))
    }
  }

  test("LibraryServer routes handle GET /api/users/{userId}/reservations") {
    Get("/api/users/student1/reservations") ~> LibraryServer.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(responseAs[String].contains("success"))
    }
  }

  test("LibraryServer routes handle GET /api/users/{userId}/recommendations") {
    Get("/api/users/student1/recommendations") ~> LibraryServer.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(responseAs[String].contains("success"))
    }
  }

  test("LibraryServer implicit system and executionContext are accessible") {
    import LibraryServer.{system => libSystem, executionContext}
    assert(libSystem != null)
    assert(executionContext != null)
    assert(executionContext.isInstanceOf[ExecutionContext])
  }

  test("LibraryServer handles static file requests") {
    Get("/") ~> LibraryServer.routes ~> check {
      // Should either return the file or handle gracefully
      assert(status == StatusCodes.OK || status == StatusCodes.NotFound)
    }
  }

  test("LibraryServer handles static directory requests") {
    Get("/static/app.js") ~> LibraryServer.routes ~> check {
      // Should either return the file or handle gracefully  
      assert(status == StatusCodes.OK || status == StatusCodes.NotFound)
    }
  }

  test("LibraryServer main method components") {
    // Test that the main method exists and is callable (without actually running it)
    val mainMethod = LibraryServer.getClass.getDeclaredMethod("main", classOf[Array[String]])
    assert(mainMethod != null)
    assert(mainMethod.getReturnType == Void.TYPE)
  }
}
