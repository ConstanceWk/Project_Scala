import org.scalatest.funsuite.AnyFunSuite
import api._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser.decode

class ApiResponseFullCoverageTest extends AnyFunSuite {
  test("ApiResponse with success and data") {
    val resp = ApiResponse(success = true, data = Some(Map("x" -> 42)), message = Some("ok"))
    val json = resp.asJson.noSpaces
    val decoded = decode[ApiResponse[Map[String, Int]]](json)
    assert(decoded.isRight)
    assert(decoded.toOption.get == resp)
    assert(resp.success)
    assert(resp.data.get("x") == 42)
    assert(resp.message.contains("ok"))
  }

  test("ApiResponse with error and no data") {
    val resp = ApiResponse[Map[String, Int]](success = false, data = None, message = Some("fail"))
    val json = resp.asJson.noSpaces
    val decoded = decode[ApiResponse[Map[String, Int]]](json)
    assert(decoded.isRight)
    assert(decoded.toOption.get == resp)
    assert(!resp.success)
    assert(resp.data.isEmpty)
    assert(resp.message.contains("fail"))
  }

  test("ApiResponse serializes and deserializes with and without data") {
    val resp1 = ApiResponse(success = true, data = Some(Map("a" -> 1)), message = Some("ok"))
    val json1 = resp1.asJson.noSpaces
    val decoded1 = decode[ApiResponse[Map[String, Int]]](json1)
    assert(decoded1.isRight)
    assert(decoded1.toOption.get == resp1)

    val resp2 = ApiResponse[Map[String, Int]](success = false, data = None, message = Some("fail"))
    val json2 = resp2.asJson.noSpaces
    val decoded2 = decode[ApiResponse[Map[String, Int]]](json2)
    assert(decoded2.isRight)
    assert(decoded2.toOption.get == resp2)
  }

  test("ApiResponse equality and toString") {
    val r1 = ApiResponse(success = true, data = Some(1), message = Some("ok"))
    val r2 = ApiResponse(success = true, data = Some(1), message = Some("ok"))
    val r3 = ApiResponse(success = false, data = None, message = Some("fail"))
    assert(r1 == r2)
    assert(r1.toString.nonEmpty)
    assert(r3 != r1)
  }

  test("ApiResponse decode fails on malformed JSON") {
    val badJson = """{"status":true,"message":42}"""
    val decoded = decode[ApiResponse[Int]](badJson)
    assert(decoded.isLeft)
  }

  test("ApiResponse with None message and None data") {
    val resp = ApiResponse[Int](success = true, data = None, message = None)
    val json = resp.asJson.noSpaces
    val decoded = decode[ApiResponse[Int]](json)
    assert(decoded.isRight)
    assert(decoded.toOption.get == resp)
    assert(resp.success)
    assert(resp.data.isEmpty)
    assert(resp.message.isEmpty)
  }
}
