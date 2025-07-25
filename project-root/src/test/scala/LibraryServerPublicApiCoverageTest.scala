import org.scalatest.funsuite.AnyFunSuite
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import io.circe.syntax._
import io.circe.parser._
import io.circe.generic.auto._
import models._
import api._
import services.LibraryCatalog
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Success, Failure}

class LibraryServerPublicApiCoverageTest extends AnyFunSuite with ScalatestRouteTest {

  val routes: Route = LibraryServer.routes

  test("GET /api/books should return all books") {
    Get("/api/books") ~> routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      assert(responseStr.contains("success"))
      assert(responseStr.contains("data"))
      
      val apiResponse = decode[ApiResponse[List[Book]]](responseStr)
      assert(apiResponse.isRight)
      assert(apiResponse.getOrElse(ApiResponse(false, None, None)).success)
    }
  }

  test("GET /api/books/available should return available books") {
    Get("/api/books/available") ~> routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      val apiResponse = decode[ApiResponse[List[Book]]](responseStr)
      assert(apiResponse.isRight)
      assert(apiResponse.getOrElse(ApiResponse(false, None, None)).success)
    }
  }

  test("POST /api/books/search should handle valid search requests") {
    val searchRequest = SearchRequest("Scala")
    val jsonEntity = HttpEntity(ContentTypes.`application/json`, searchRequest.asJson.noSpaces)
    
    Post("/api/books/search", jsonEntity) ~> routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      val apiResponse = decode[ApiResponse[List[Book]]](responseStr)
      assert(apiResponse.isRight)
    }
  }

  test("POST /api/books/search should handle invalid JSON") {
    val invalidJson = HttpEntity(ContentTypes.`application/json`, "{invalid json}")
    
    Post("/api/books/search", invalidJson) ~> routes ~> check {
      assert(status == StatusCodes.BadRequest)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      assert(responseStr.contains("Requête JSON invalide"))
      assert(responseStr.contains("false"))
    }
  }

  test("POST /api/books/loan should handle valid loan requests") {
    val loanRequest = LoanRequest("student1", "book1")
    val jsonEntity = HttpEntity(ContentTypes.`application/json`, loanRequest.asJson.noSpaces)
    
    Post("/api/books/loan", jsonEntity) ~> routes ~> check {
      // Status can be OK (success) or BadRequest (book not available/user not found)
      assert(status == StatusCodes.OK || status == StatusCodes.BadRequest)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      val apiResponse = decode[ApiResponse[String]](responseStr)
      assert(apiResponse.isRight)
    }
  }

  test("POST /api/books/loan should handle invalid JSON") {
    val invalidJson = HttpEntity(ContentTypes.`application/json`, "{not valid json}")
    
    Post("/api/books/loan", invalidJson) ~> routes ~> check {
      assert(status == StatusCodes.BadRequest)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      assert(responseStr.contains("Requête JSON invalide"))
    }
  }

  test("POST /api/books/return should handle valid return requests") {
    val returnRequest = ReturnRequest("student1", "book1")
    val jsonEntity = HttpEntity(ContentTypes.`application/json`, returnRequest.asJson.noSpaces)
    
    Post("/api/books/return", jsonEntity) ~> routes ~> check {
      // Status can be OK (success) or BadRequest (book not loaned/user not found)
      assert(status == StatusCodes.OK || status == StatusCodes.BadRequest)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      val apiResponse = decode[ApiResponse[String]](responseStr)
      assert(apiResponse.isRight)
    }
  }

  test("POST /api/books/return should handle invalid JSON") {
    val invalidJson = HttpEntity(ContentTypes.`application/json`, "invalid")
    
    Post("/api/books/return", invalidJson) ~> routes ~> check {
      assert(status == StatusCodes.BadRequest)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      assert(responseStr.contains("Requête JSON invalide"))
    }
  }

  test("POST /api/books/reserve should handle valid reservation requests") {
    val reserveRequest = LoanRequest("student1", "book1") // Uses same model as loan
    val jsonEntity = HttpEntity(ContentTypes.`application/json`, reserveRequest.asJson.noSpaces)
    
    Post("/api/books/reserve", jsonEntity) ~> routes ~> check {
      // Status can be OK (success) or BadRequest (book not available/user not found)
      assert(status == StatusCodes.OK || status == StatusCodes.BadRequest)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      val apiResponse = decode[ApiResponse[String]](responseStr)
      assert(apiResponse.isRight)
    }
  }

  test("POST /api/books/reserve should handle invalid JSON") {
    val invalidJson = HttpEntity(ContentTypes.`application/json`, "not json")
    
    Post("/api/books/reserve", invalidJson) ~> routes ~> check {
      assert(status == StatusCodes.BadRequest)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      assert(responseStr.contains("Requête JSON invalide"))
    }
  }

  test("GET /api/users/{userId}/reservations should return user reservations") {
    Get("/api/users/student1/reservations") ~> routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      val apiResponse = decode[ApiResponse[List[Reservation]]](responseStr)
      assert(apiResponse.isRight)
      assert(apiResponse.getOrElse(ApiResponse(false, None, None)).success)
    }
  }

  test("GET /api/statistics/genres should return top genres") {
    Get("/api/statistics/genres") ~> routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      val apiResponse = decode[ApiResponse[List[(String, Int)]]](responseStr)
      assert(apiResponse.isRight)
      assert(apiResponse.getOrElse(ApiResponse(false, None, None)).success)
    }
  }

  test("GET /api/statistics/authors should return top authors") {
    Get("/api/statistics/authors") ~> routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      val apiResponse = decode[ApiResponse[List[(String, Int)]]](responseStr)
      assert(apiResponse.isRight)
      assert(apiResponse.getOrElse(ApiResponse(false, None, None)).success)
    }
  }

  test("GET /api/users/{userId}/recommendations should return recommendations") {
    Get("/api/users/student1/recommendations") ~> routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      val apiResponse = decode[ApiResponse[List[Book]]](responseStr)
      assert(apiResponse.isRight)
      assert(apiResponse.getOrElse(ApiResponse(false, None, None)).success)
    }
  }

  test("GET /api/users should return all users") {
    Get("/api/users") ~> routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      val apiResponse = decode[ApiResponse[List[User]]](responseStr)
      assert(apiResponse.isRight)
      assert(apiResponse.getOrElse(ApiResponse(false, None, None)).success)
    }
  }

  test("GET /api/transactions should return all transactions") {
    Get("/api/transactions") ~> routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val responseStr = responseAs[String]
      val apiResponse = decode[ApiResponse[List[Transaction]]](responseStr)
      assert(apiResponse.isRight)
      assert(apiResponse.getOrElse(ApiResponse(false, None, None)).success)
    }
  }

  test("Static file routes should serve index.html at root") {
    Get("/") ~> routes ~> check {
      // Should attempt to serve index.html (may succeed or fail depending on file existence)
      assert(status == StatusCodes.OK || status == StatusCodes.NotFound)
    }
  }

  test("Static file routes should handle static files") {
    Get("/static/app.js") ~> routes ~> check {
      // Should attempt to serve static files (may succeed or fail depending on file existence)
      assert(status == StatusCodes.OK || status == StatusCodes.NotFound)
    }
  }

  test("Invalid routes should be rejected") {
    Get("/api/invalid/route") ~> routes ~> check {
      assert(handled == false) // Route not handled
    }
    
    Post("/api/invalid/route") ~> routes ~> check {
      assert(handled == false) // Route not handled
    }
  }

  test("Data model serialization coverage") {
    // Test Book serialization
    val book = Book("978-0134685991", "Test Book", List("Test Author"), 2020, "Fiction", available = true)
    val bookJson = book.asJson.noSpaces
    assert(bookJson.contains("Test Book"))
    assert(bookJson.contains("Test Author"))
    assert(bookJson.contains("978-0134685991"))
    
    // Test User serialization
    val student = Student("s1", "John Doe", "Computer Science")
    val faculty = Faculty("p1", "Jane Smith", "Software Engineering")
    val studentJson = student.asJson.noSpaces
    val facultyJson = faculty.asJson.noSpaces
    assert(studentJson.contains("John Doe"))
    assert(facultyJson.contains("Jane Smith"))
    
    // Test API models
    val searchReq = SearchRequest("Scala")
    val loanReq = LoanRequest("user1", "book1")
    val returnReq = ReturnRequest("user1", "book1")
    val apiResp = ApiResponse(success = true, data = Some("test"), message = Some("ok"))
    
    assert(searchReq.asJson.noSpaces.contains("Scala"))
    assert(loanReq.asJson.noSpaces.contains("user1"))
    assert(returnReq.asJson.noSpaces.contains("book1"))
    assert(apiResp.asJson.noSpaces.contains("true"))
  }

  test("Data model deserialization coverage") {
    // Test deserialization
    val bookJson = """{"isbn":"123","title":"Test","authors":["Author"],"publicationYear":2020,"genre":"Fiction","available":true}"""
    val userJson = """{"Student":{"id":"s1","name":"John","level":"CS"}}"""
    val searchJson = """{"title":"Scala"}"""
    val loanJson = """{"userId":"u1","bookId":"b1"}"""
    
    val bookResult = decode[Book](bookJson)
    val userResult = decode[User](userJson)
    val searchResult = decode[SearchRequest](searchJson)
    val loanResult = decode[LoanRequest](loanJson)
    
    assert(bookResult.isRight)
    assert(userResult.isRight)
    assert(searchResult.isRight)
    assert(loanResult.isRight)
  }

  test("LibraryServer public members accessibility") {
    // Test that public members are accessible
    assert(LibraryServer.system != null)
    assert(LibraryServer.executionContext != null)
    assert(LibraryServer.routes != null)
    
    // Test that main method exists
    val mainMethod = LibraryServer.getClass.getMethod("main", classOf[Array[String]])
    assert(mainMethod != null)
  }

  test("Error response format consistency") {
    // Test that all error responses follow the same format
    val invalidJsonRequests = List(
      Post("/api/books/search", HttpEntity(ContentTypes.`application/json`, "invalid")),
      Post("/api/books/loan", HttpEntity(ContentTypes.`application/json`, "invalid")),
      Post("/api/books/return", HttpEntity(ContentTypes.`application/json`, "invalid")),
      Post("/api/books/reserve", HttpEntity(ContentTypes.`application/json`, "invalid"))
    )
    
    invalidJsonRequests.foreach { request =>
      request ~> routes ~> check {
        assert(status == StatusCodes.BadRequest)
        assert(contentType == ContentTypes.`application/json`)
        
        val responseStr = responseAs[String]
        assert(responseStr.contains("success"))
        assert(responseStr.contains("false"))
        assert(responseStr.contains("Requête JSON invalide"))
      }
    }
  }
}
