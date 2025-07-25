import org.scalatest.funsuite.AnyFunSuite
import io.circe.syntax._
import io.circe.parser.decode
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._
import services._
import services.given
import models._
import java.time.LocalDateTime

// Codecs explicites pour les sous-types de User
given Encoder[Student] = deriveEncoder
given Encoder[Faculty] = deriveEncoder
given Encoder[Librarian] = deriveEncoder

given Decoder[Student] = deriveDecoder
given Decoder[Faculty] = deriveDecoder
given Decoder[Librarian] = deriveDecoder

class LibraryCatalogCodecFullCoverageTest extends AnyFunSuite {
  test("LocalDateTime codec roundtrip") {
    val now = LocalDateTime.now.withNano(0)
    val json = now.asJson.noSpaces
    val decoded = decode[LocalDateTime](json)
    assert(decoded.contains(now))
  }

  test("Book codec roundtrip") {
    val book = Book("isbn", "title", List("author1", "author2"), 2020, "genre", true)
    val json = book.asJson.noSpaces
    val decoded = decode[Book](json)
    assert(decoded.contains(book))
  }

  test("User codec roundtrip for Student, Faculty, Librarian") {
    val student = Student("id", "name", "level")
    val faculty = Faculty("id", "name", "dept")
    val librarian = Librarian("id", "name", "pos")
    for (u <- List(student, faculty, librarian)) {
      val json = u.asJson.noSpaces
      val decoded = decode[User](json)
      assert(decoded.contains(u))
    }
  }

  test("Transaction codec roundtrip for Loan and Return") {
    val now = LocalDateTime.now.withNano(0)
    val book = Book("isbn", "title", List("author"), 2020, "genre", true)
    val user = Student("id", "name", "level")
    val loan = Loan(book, user, now)
    val ret = Return(book, user, now)
    for (t <- List(loan, ret)) {
      val json = t.asJson.noSpaces
      val decoded = decode[Transaction](json)
      assert(decoded.contains(t))
    }
  }

  test("LibraryCatalog codec roundtrip") {
    val book = Book("isbn", "title", List("author"), 2020, "genre", true)
    val user = Student("id", "name", "level")
    val now = LocalDateTime.now.withNano(0)
    val cat = LibraryCatalog(
      books = List(book),
      users = List(user),
      transactions = List(Loan(book, user, now))
    )
    val json = cat.asJson.noSpaces
    val decoded = decode[LibraryCatalog](json)
    assert(decoded.contains(cat))
  }

  test("LocalDateTime decoder returns Left on invalid format") {
    val json = "\"not-a-date\""
    val decoded = decode[LocalDateTime](json)
    assert(decoded.isLeft)
  }

  test("User decoder fails on completely invalid JSON") {
    val json = "{}"
    val decoded = decode[User](json)
    assert(decoded.isLeft)
  }

  test("Transaction decoder fails on completely invalid JSON") {
    val json = "{}"
    val decoded = decode[Transaction](json)
    assert(decoded.isLeft)
  }

  test("User decoder fails on wrong structure") {
    val json = "{\"Faculty\":{\"id\":1}}" // id not a string
    val decoded = decode[User](json)
    assert(decoded.isLeft)
  }

  test("Transaction decoder fails on wrong structure") {
    val json = "{\"Loan\":{\"book\":{},\"user\":{},\"timestamp\":\"not-a-date\"}}"
    val decoded = decode[Transaction](json)
    assert(decoded.isLeft)
  }
}
