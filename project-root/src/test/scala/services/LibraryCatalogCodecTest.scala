package services

import org.scalatest.funsuite.AnyFunSuite
import io.circe.syntax._
import io.circe.parser._
import models._
import java.time.LocalDateTime
import services.{given_Encoder_Book, given_Decoder_Book, given_Encoder_User, given_Decoder_User, given_Encoder_Transaction, given_Decoder_Transaction}
import api._
import io.circe.generic.auto._

class LibraryCatalogCodecTest extends AnyFunSuite {
  test("Book encode/decode") {
    val book = Book("9780000000001", "Test", List("A"), 2020, "Test", true)
    val json = book.asJson.noSpaces
    val decoded = decode[Book](json)
    assert(decoded.isRight)
    assert(decoded.toOption.get == book)
  }

  test("User encode/decode") {
    val user: User = Student("id1", "Alice", "Bachelor")
    val json = user.asJson.noSpaces
    val decoded = decode[User](json)
    assert(decoded.isRight)
    assert(decoded.toOption.get == user)
  }

  test("Transaction encode/decode") {
    val book = Book("9780000000001", "Test", List("A"), 2020, "Test", true)
    val user: User = Student("id1", "Alice", "Bachelor")
    val loan = Loan(book, user, LocalDateTime.now())
    val json = loan.asJson.noSpaces
    val decoded = decode[Transaction](json)
    assert(decoded.isRight)
  }

  test("ApiResponse encode/decode with and without data") {
    val resp1 = ApiResponse[String](success = true, data = Some("ok"), message = None)
    val json = io.circe.syntax.EncoderOps(resp1).asJson.noSpaces
    val decoded = io.circe.parser.decode[ApiResponse[String]](json)
    assert(decoded.isRight)
    val resp2 = ApiResponse[String](success = false, data = None, message = Some("error"))
    val json2 = io.circe.syntax.EncoderOps(resp2).asJson.noSpaces
    val decoded2 = io.circe.parser.decode[ApiResponse[String]](json2)
    assert(decoded2.isRight)
  }

  test("LoanRequest, ReturnRequest, SearchRequest encode/decode") {
    val loan = LoanRequest("u", "b")
    val json = io.circe.syntax.EncoderOps(loan).asJson.noSpaces
    val decoded = io.circe.parser.decode[LoanRequest](json)
    assert(decoded.isRight)
    val ret = ReturnRequest("u", "b")
    val json2 = io.circe.syntax.EncoderOps(ret).asJson.noSpaces
    val decoded2 = io.circe.parser.decode[ReturnRequest](json2)
    assert(decoded2.isRight)
    val search = SearchRequest("title")
    val json3 = io.circe.syntax.EncoderOps(search).asJson.noSpaces
    val decoded3 = io.circe.parser.decode[SearchRequest](json3)
    assert(decoded3.isRight)
  }
}
