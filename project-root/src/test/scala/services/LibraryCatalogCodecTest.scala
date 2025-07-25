package services

import org.scalatest.funsuite.AnyFunSuite
import io.circe.syntax._
import io.circe.parser._
import models._
import java.time.LocalDateTime
import services.{given_Encoder_Book, given_Decoder_Book, given_Encoder_User, given_Decoder_User, given_Encoder_Transaction, given_Decoder_Transaction}
import api._
import io.circe.generic.auto._
import services.given
import services.{manualUserDecoder}
import services.{manualReturnDecoder, manualTransactionEncoder, manualTransactionDecoder}
import services.{manualLoanDecoder, manualReturnEncoder}
import services.{manualBookEncoder, manualBookDecoder, manualUserEncoder, manualLoanEncoder, manualLibraryCatalogEncoder, manualLibraryCatalogDecoder}

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

  test("User decode: Student explicit") {
    val json = """{"Student":{"id":"s1","name":"Alice","level":"Bachelor"}}"""
    val decoded = io.circe.parser.decode[User](json)
    assert(decoded.isRight)
    assert(decoded.toOption.get == Student("s1", "Alice", "Bachelor"))
  }

  test("User decode: Faculty explicit") {
    val json = """{"Faculty":{"id":"f1","name":"Bob","department":"Math"}}"""
    val decoded = io.circe.parser.decode[User](json)
    assert(decoded.isRight)
    assert(decoded.toOption.get == Faculty("f1", "Bob", "Math"))
  }

  test("User decode: Librarian explicit") {
    val json = """{"Librarian":{"id":"l1","name":"Eve","position":"Chief"}}"""
    val decoded = io.circe.parser.decode[User](json)
    assert(decoded.isRight)
    assert(decoded.toOption.get == Librarian("l1", "Eve", "Chief"))
  }

  test("User decode: error on malformed JSON") {
    val json = """{"Student":{"id":123}}"""
    val decoded = io.circe.parser.decode[User](json)
    assert(decoded.isLeft)
  }

  test("Transaction decode: Loan explicit") {
    val book = Book("isbn", "title", List("A"), 2020, "Test", true)
    val user: User = Student("id1", "Alice", "Bachelor")
    val ts = LocalDateTime.now()
    val json = s"""{"Loan":{"book":${book.asJson.noSpaces},"user":${user.asJson.noSpaces},"timestamp":"${ts}"}}"""
    val decoded = io.circe.parser.decode[Transaction](json)
    assert(decoded.isRight)
    assert(decoded.toOption.get.isInstanceOf[Loan])
  }

  test("Transaction decode: Return explicit") {
    val book = Book("isbn", "title", List("A"), 2020, "Test", true)
    val user: User = Student("id1", "Alice", "Bachelor")
    val ts = LocalDateTime.now()
    val json = s"""{"Return":{"book":${book.asJson.noSpaces},"user":${user.asJson.noSpaces},"timestamp":"${ts}"}}"""
    val decoded = io.circe.parser.decode[Transaction](json)
    assert(decoded.isRight)
    assert(decoded.toOption.get.isInstanceOf[Return])
  }

  test("Transaction decode: error on malformed JSON") {
    val json = """{"Loan":{"book":null,"user":null,"timestamp":null}}"""
    val decoded = io.circe.parser.decode[Transaction](json)
    assert(decoded.isLeft)
  }

  test("LibraryCatalog encode/decode roundtrip") {
    val catalog = LibraryCatalog(
      books = List(Book("isbn", "title", List("A"), 2020, "Test", true)),
      users = List(Student("id1", "Alice", "Bachelor")),
      transactions = List(Loan(Book("isbn", "title", List("A"), 2020, "Test", true), Student("id1", "Alice", "Bachelor"), LocalDateTime.now()))
    )
    val json = catalog.asJson.noSpaces
    val decoded = io.circe.parser.decode[LibraryCatalog](json)
    assert(decoded.isRight)
    assert(decoded.toOption.get.books.nonEmpty)
    assert(decoded.toOption.get.users.nonEmpty)
    assert(decoded.toOption.get.transactions.nonEmpty)
  }

  test("LibraryCatalog encode/decode with empty lists") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val json = catalog.asJson.noSpaces
    val decoded = io.circe.parser.decode[LibraryCatalog](json)
    assert(decoded.isRight)
    assert(decoded.toOption.get.books.isEmpty)
    assert(decoded.toOption.get.users.isEmpty)
    assert(decoded.toOption.get.transactions.isEmpty)
  }

  test("LocalDateTime decode: invalid format") {
    val json = "\"not-a-date\""
    val decoded = io.circe.parser.decode[java.time.LocalDateTime](json)
    assert(decoded.isLeft)
  }

  test("manualUserDecoder decode Student") {
    val json = io.circe.Json.obj(
      ("type", io.circe.Json.fromString("Student")),
      ("id", io.circe.Json.fromString("stu1")),
      ("name", io.circe.Json.fromString("Alice")),
      ("level", io.circe.Json.fromString("Bachelor"))
    )
    val result = manualUserDecoder(json.hcursor)
    assert(result.contains(Student("stu1", "Alice", "Bachelor")))
  }

  test("manualUserDecoder decode Faculty") {
    val json = io.circe.Json.obj(
      ("type", io.circe.Json.fromString("Faculty")),
      ("id", io.circe.Json.fromString("fac1")),
      ("name", io.circe.Json.fromString("Bob")),
      ("department", io.circe.Json.fromString("Math"))
    )
    val result = manualUserDecoder(json.hcursor)
    assert(result.contains(Faculty("fac1", "Bob", "Math")))
  }

  test("manualUserDecoder decode Librarian") {
    val json = io.circe.Json.obj(
      ("type", io.circe.Json.fromString("Librarian")),
      ("id", io.circe.Json.fromString("lib1")),
      ("name", io.circe.Json.fromString("Eve")),
      ("position", io.circe.Json.fromString("Chief"))
    )
    val result = manualUserDecoder(json.hcursor)
    assert(result.contains(Librarian("lib1", "Eve", "Chief")))
  }

  test("manualUserDecoder decode unknown type") {
    val json = io.circe.Json.obj(
      ("type", io.circe.Json.fromString("Alien")),
      ("id", io.circe.Json.fromString("999")),
      ("name", io.circe.Json.fromString("E.T."))
    )
    val result = manualUserDecoder(json.hcursor)
    assert(result.isLeft)
  }

  test("manualReturnDecoder decode Return") {
    val book = Book("isbnX", "Titre", List("Auteur"), 2022, "Genre", true)
    val user: User = Student("idX", "Nom", "Licence")
    val timestamp = java.time.LocalDateTime.now()
    val json = io.circe.Json.obj(
      "book" -> book.asJson,
      "user" -> user.asJson,
      "timestamp" -> io.circe.Json.fromString(timestamp.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    )
    val result = manualReturnDecoder(json.hcursor)
    assert(result.contains(Return(book, user, timestamp)))
  }

  test("manualTransactionEncoder encodes Loan, Return, Reservation") {
    val book = Book("isbnY", "Scala", List("Paul"), 2023, "Informatique", true)
    val user: User = Faculty("idY", "Prof", "Maths")
    val timestamp = java.time.LocalDateTime.now()
    val loan: Transaction = Loan(book, user, timestamp)
    val returnTx: Transaction = Return(book, user, timestamp)
    val reservation: Transaction = Reservation(book, user, timestamp)

    val loanJson = manualTransactionEncoder(loan)
    assert(loanJson.hcursor.downField("Loan").succeeded)

    val returnJson = manualTransactionEncoder(returnTx)
    assert(returnJson.hcursor.downField("Return").succeeded)

    val reservationJson = manualTransactionEncoder(reservation)
    assert(reservationJson.hcursor.downField("Reservation").succeeded)
  }

  test("manualTransactionDecoder decode Loan, Return, Reservation") {
    val book = Book("isbnZ", "Livre", List("AuteurZ"), 2024, "Roman", false)
    val user: User = Librarian("idZ", "Bib", "Chef")
    val timestamp = java.time.LocalDateTime.now()

    // Loan
    val loanJson = io.circe.Json.obj(
      "Loan" -> io.circe.Json.obj(
        "book" -> book.asJson,
        "user" -> user.asJson,
        "timestamp" -> io.circe.Json.fromString(timestamp.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
      )
    )
    val loanResult = manualTransactionDecoder(loanJson.hcursor)
    assert(loanResult.exists(_.isInstanceOf[Loan]))

    // Return
    val returnJson = io.circe.Json.obj(
      "Return" -> io.circe.Json.obj(
        "book" -> book.asJson,
        "user" -> user.asJson,
        "timestamp" -> io.circe.Json.fromString(timestamp.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
      )
    )
    val returnResult = manualTransactionDecoder(returnJson.hcursor)
    assert(returnResult.exists(_.isInstanceOf[Return]))

    // Reservation
    val reservationJson = io.circe.Json.obj(
      "Reservation" -> io.circe.Json.obj(
        "book" -> book.asJson,
        "user" -> user.asJson,
        "timestamp" -> io.circe.Json.fromString(timestamp.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
      )
    )
    val reservationResult = manualTransactionDecoder(reservationJson.hcursor)
    assert(reservationResult.exists(_.isInstanceOf[Reservation]))
  }

  test("manualLoanDecoder decode Loan") {
    val book = Book("isbnL", "LoanBook", List("AuteurL"), 2025, "GenreL", true)
    val user: User = Faculty("idL", "ProfL", "Physique")
    val timestamp = java.time.LocalDateTime.now()
    val json = io.circe.Json.obj(
      "book" -> book.asJson,
      "user" -> user.asJson,
      "timestamp" -> io.circe.Json.fromString(timestamp.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    )
    val result = manualLoanDecoder(json.hcursor)
    assert(result.contains(Loan(book, user, timestamp)))
  }

  test("manualReturnEncoder encodes Return") {
    val book = Book("isbnR", "ReturnBook", List("AuteurR"), 2026, "GenreR", false)
    val user: User = Librarian("idR", "BibR", "Adjoint")
    val timestamp = java.time.LocalDateTime.now()
    val returnTx = Return(book, user, timestamp)
    val json = manualReturnEncoder(returnTx)
    val cursor = json.hcursor
    assert(cursor.downField("book").succeeded)
    assert(cursor.downField("user").succeeded)
    assert(cursor.downField("timestamp").succeeded)
  }

  test("deriveEncoder/deriveDecoder Book roundtrip explicit") {
    val book = Book("isbn2", "Scala", List("Martin"), 2021, "Test", true)
    val json = book.asJson.noSpaces
    val decoded = decode[Book](json)
    assert(decoded.contains(book))
  }

  test("deriveEncoder/deriveDecoder Transaction roundtrip explicit") {
    val t: Transaction = Loan(Book("isbn3", "FP", List("Paul"), 2022, "Test", true), Student("id2", "Bob", "Master"), java.time.LocalDateTime.now())
    val json = t.asJson.noSpaces
    println(json) // Pour debug, tu peux l'enlever après
    val decoded = decode[Transaction](json)
    assert(decoded.isRight)
  }

  test("deriveEncoder/deriveDecoder LibraryCatalog roundtrip explicit") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val json = catalog.asJson.noSpaces
    val decoded = decode[LibraryCatalog](json)
    assert(decoded.isRight)
  }

  test("manualTransactionDecoder decode Reservation branch") {
    val book = Book("isbnRes", "ResBook", List("AuteurRes"), 2027, "GenreRes", true)
    val user: User = Student("idRes", "NomRes", "LicenceRes")
    val timestamp = java.time.LocalDateTime.now()

    val reservationJson = io.circe.Json.obj(
      "Reservation" -> io.circe.Json.obj(
        "book" -> book.asJson,
        "user" -> user.asJson,
        "timestamp" -> io.circe.Json.fromString(timestamp.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
      )
    )

    val result = manualTransactionDecoder(reservationJson.hcursor)
    assert(result.exists {
      case Reservation(b, u, t) => b == book && u == user && t == timestamp
      case _ => false
    })
  }
  test("manualBookDecoder decode Book with correct types") {
  val json = io.circe.Json.obj(
    "isbn" -> io.circe.Json.fromString("9780000000001"),
    "title" -> io.circe.Json.fromString("Test Book"),
    "authors" -> io.circe.Json.arr(io.circe.Json.fromString("Author1"), io.circe.Json.fromString("Author2")),
    "publicationYear" -> io.circe.Json.fromInt(2020),
    "genre" -> io.circe.Json.fromString("Fiction"),
    "available" -> io.circe.Json.fromBoolean(true)
  )

  val result = manualBookDecoder(json.hcursor)
  assert(result.isRight)

  result.foreach { book =>
    assert(book.isbn.isInstanceOf[String])
    assert(book.title.isInstanceOf[String])
    assert(book.authors.forall(_.isInstanceOf[String]))
    assert(book.publicationYear.isInstanceOf[Int])
    assert(book.genre.isInstanceOf[String])
    assert(book.available.isInstanceOf[Boolean])
  }
 }
 

}
