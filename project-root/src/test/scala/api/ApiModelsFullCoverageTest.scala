import org.scalatest.funsuite.AnyFunSuite
import io.circe.syntax._
import io.circe.parser.decode
import io.circe.generic.auto._
import api._

class ApiModelsFullCoverageTest extends AnyFunSuite {
  test("ApiResponse serializes and deserializes with and without data") {
    val respWithData = ApiResponse(success = true, data = Some(Map("foo" -> "bar")), message = Some("ok"))
    val json = respWithData.asJson.noSpaces
    val decoded = decode[ApiResponse[Map[String, String]]](json)
    assert(decoded.contains(respWithData))

    val respNoData = ApiResponse[String](success = false, data = None, message = Some("error"))
    val json2 = respNoData.asJson.noSpaces
    val decoded2 = decode[ApiResponse[String]](json2)
    assert(decoded2.contains(respNoData))
  }

  test("LoanRequest, ReturnRequest, SearchRequest encode/decode roundtrip") {
    val loan = LoanRequest("user", "book")
    val returnReq = ReturnRequest("user", "book")
    val search = SearchRequest("title")
    assert(decode[LoanRequest](loan.asJson.noSpaces).contains(loan))
    assert(decode[ReturnRequest](returnReq.asJson.noSpaces).contains(returnReq))
    assert(decode[SearchRequest](search.asJson.noSpaces).contains(search))
  }

  test("ApiResponse equality and toString") {
    val a = ApiResponse(success = true, data = Some(42), message = Some("ok"))
    val b = ApiResponse(success = true, data = Some(42), message = Some("ok"))
    val c = ApiResponse(success = false, data = None, message = Some("fail"))
    assert(a == b)
    assert(a != c)
    assert(a.toString.contains("ApiResponse"))
  }
}
