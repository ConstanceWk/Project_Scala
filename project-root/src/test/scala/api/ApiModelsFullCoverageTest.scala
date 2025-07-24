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

  test("RecommendationRequest encode/decode roundtrip") {
    val rec = RecommendationRequest("user42")
    val json = rec.asJson.noSpaces
    val decoded = decode[RecommendationRequest](json)
    assert(decoded.contains(rec))
  }

  test("ApiResponse equality and toString") {
    val a = ApiResponse(success = true, data = Some(42), message = Some("ok"))
    val b = ApiResponse(success = true, data = Some(42), message = Some("ok"))
    val c = ApiResponse(success = false, data = None, message = Some("fail"))
    assert(a == b)
    assert(a != c)
    assert(a.toString.contains("ApiResponse"))
  }

  test("DTO equality and toString") {
    val l1 = LoanRequest("u", "b"); val l2 = LoanRequest("u", "b")
    val r1 = ReturnRequest("u", "b"); val r2 = ReturnRequest("u", "b")
    val s1 = SearchRequest("t"); val s2 = SearchRequest("t")
    val rec1 = RecommendationRequest("u"); val rec2 = RecommendationRequest("u")
    assert(l1 == l2 && r1 == r2 && s1 == s2 && rec1 == rec2)
    assert(l1.toString.contains("LoanRequest"))
    assert(r1.toString.contains("ReturnRequest"))
    assert(s1.toString.contains("SearchRequest"))
    assert(rec1.toString.contains("RecommendationRequest"))
  }

  test("ApiResponse with no message and different data types") {
    val respInt = ApiResponse(success = true, data = Some(123), message = None)
    val respList = ApiResponse(success = true, data = Some(List(1,2,3)), message = None)
    val jsonInt = respInt.asJson.noSpaces
    val jsonList = respList.asJson.noSpaces
    assert(decode[ApiResponse[Int]](jsonInt).contains(respInt))
    assert(decode[ApiResponse[List[Int]]](jsonList).contains(respList))
  }

  test("ApiResponse decode fails on malformed JSON") {
    val badJson = "{" // invalid JSON
    val decoded = decode[ApiResponse[String]](badJson)
    assert(decoded.isLeft)
  }

  test("LoanRequest decode fails on wrong structure") {
    val badJson = "{\"foo\":\"bar\"}"
    val decoded = decode[LoanRequest](badJson)
    assert(decoded.isLeft)
  }
}
