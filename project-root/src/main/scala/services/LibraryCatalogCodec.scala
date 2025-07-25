package services

import io.circe.generic.semiauto.{deriveEncoder, deriveDecoder}
import io.circe.{Encoder, Decoder, Json, HCursor, DecodingFailure}
import io.circe.syntax._
import models._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import api._

given Encoder[LocalDateTime] = Encoder.encodeString.contramap[LocalDateTime](_.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
given Decoder[LocalDateTime] = Decoder.decodeString.emap { str =>
  try Right(LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
  catch case _: Exception => Left(s"Unable to parse LocalDateTime: $str")
}

// Automatic derivation of encoders/decoders for all models
given Encoder[Book] = deriveEncoder
given Decoder[Book] = deriveDecoder
given Encoder[User] = deriveEncoder
given Decoder[User] = deriveDecoder
given Encoder[Loan] = deriveEncoder
given Decoder[Loan] = deriveDecoder
given Encoder[Return] = deriveEncoder
given Decoder[Return] = deriveDecoder
given Encoder[Transaction] = deriveEncoder
given Decoder[Transaction] = deriveDecoder
given Encoder[LibraryCatalog] = deriveEncoder
given Decoder[LibraryCatalog] = deriveDecoder

// Automatic derivation for API models
given Encoder[SearchRequest] = deriveEncoder
given Decoder[SearchRequest] = deriveDecoder
given Encoder[LoanRequest] = deriveEncoder
given Decoder[LoanRequest] = deriveDecoder
given Encoder[ReturnRequest] = deriveEncoder
given Decoder[ReturnRequest] = deriveDecoder
given Encoder[ApiResponse[String]] = deriveEncoder
given Decoder[ApiResponse[String]] = deriveDecoder
given [T: Encoder: Decoder]: Encoder[ApiResponse[T]] = deriveEncoder
given [T: Encoder: Decoder]: Decoder[ApiResponse[T]] = deriveDecoder

// Manual encoders/decoders for test compatibility and maximum coverage
def manualBookEncoder(book: Book): Json = Json.obj(
  "isbn" -> book.isbn.asJson,
  "title" -> book.title.asJson,
  "authors" -> book.authors.asJson,
  "publicationYear" -> book.publicationYear.asJson,
  "genre" -> book.genre.asJson,
  "available" -> book.available.asJson
)

def manualBookDecoder(cursor: HCursor): Decoder.Result[Book] = 
  for {
    isbn <- cursor.downField("isbn").as[String]
    title <- cursor.downField("title").as[String]
    authors <- cursor.downField("authors").as[List[String]]
    publicationYear <- cursor.downField("publicationYear").as[Int]
    genre <- cursor.downField("genre").as[String]
    available <- cursor.downField("available").as[Boolean]
  } yield Book(isbn, title, authors, publicationYear, genre, available)

def manualUserEncoder(user: User): Json = user match {
  case Student(id, name, level) => Json.obj(
    "id" -> id.asJson,
    "name" -> name.asJson,
    "level" -> level.asJson,
    "type" -> "Student".asJson
  )
  case Faculty(id, name, department) => Json.obj(
    "id" -> id.asJson,
    "name" -> name.asJson,
    "department" -> department.asJson,
    "type" -> "Faculty".asJson
  )
  case Librarian(id, name, position) => Json.obj(
    "id" -> id.asJson,
    "name" -> name.asJson,
    "position" -> position.asJson,
    "type" -> "Librarian".asJson
  )
}

def manualUserDecoder(cursor: HCursor): Decoder.Result[User] = {
  cursor.downField("type").as[String].flatMap {
    case "Student" =>
      for {
        id <- cursor.downField("id").as[String]
        name <- cursor.downField("name").as[String]
        level <- cursor.downField("level").as[String]
      } yield Student(id, name, level)
    case "Faculty" =>
      for {
        id <- cursor.downField("id").as[String]
        name <- cursor.downField("name").as[String]
        department <- cursor.downField("department").as[String]
      } yield Faculty(id, name, department)
    case "Librarian" =>
      for {
        id <- cursor.downField("id").as[String]
        name <- cursor.downField("name").as[String]
        position <- cursor.downField("position").as[String]
      } yield Librarian(id, name, position)
    case other => Left(DecodingFailure(s"Unknown user type: $other", cursor.history))
  }
}

def manualLoanEncoder(loan: Loan): Json = Json.obj(
  "book" -> manualBookEncoder(loan.book),
  "user" -> manualUserEncoder(loan.user),
  "timestamp" -> loan.timestamp.asJson
)

def manualLoanDecoder(cursor: HCursor): Decoder.Result[Loan] =
  for {
    book <- cursor.downField("book").as[Book](using summon[Decoder[Book]])
    user <- cursor.downField("user").as[User](using summon[Decoder[User]])
    timestamp <- cursor.downField("timestamp").as[LocalDateTime]
  } yield Loan(book, user, timestamp)

def manualReturnEncoder(returnTx: Return): Json = Json.obj(
  "book" -> manualBookEncoder(returnTx.book),
  "user" -> manualUserEncoder(returnTx.user),
  "timestamp" -> returnTx.timestamp.asJson
)

def manualReturnDecoder(cursor: HCursor): Decoder.Result[Return] =
  for {
    book <- cursor.downField("book").as[Book](using summon[Decoder[Book]])
    user <- cursor.downField("user").as[User](using summon[Decoder[User]])
    timestamp <- cursor.downField("timestamp").as[LocalDateTime]
  } yield Return(book, user, timestamp)

def manualTransactionEncoder(transaction: Transaction): Json = transaction match {
  case loan: Loan => Json.obj("Loan" -> manualLoanEncoder(loan))
  case returnTx: Return => Json.obj("Return" -> manualReturnEncoder(returnTx))
  case reservation: Reservation => Json.obj("Reservation" -> Json.obj(
    "book" -> manualBookEncoder(reservation.book),
    "user" -> manualUserEncoder(reservation.user),
    "timestamp" -> reservation.timestamp.asJson
  ))
}

def manualTransactionDecoder(cursor: HCursor): Decoder.Result[Transaction] = {
  cursor.downField("Loan").as[Loan](using summon[Decoder[Loan]]).orElse(
    cursor.downField("Return").as[Return](using summon[Decoder[Return]])
  ).orElse(
    cursor.downField("Reservation").downField("book").as[Book].flatMap { book =>
      cursor.downField("Reservation").downField("user").as[User].flatMap { user =>
        cursor.downField("Reservation").downField("timestamp").as[LocalDateTime].map { timestamp =>
          Reservation(book, user, timestamp)
        }
      }
    }
  )
}

def manualLibraryCatalogEncoder(catalog: LibraryCatalog): Json = Json.obj(
  "books" -> catalog.books.map(manualBookEncoder).asJson,
  "users" -> catalog.users.map(manualUserEncoder).asJson,
  "transactions" -> catalog.transactions.map(manualTransactionEncoder).asJson
)

def manualLibraryCatalogDecoder(cursor: HCursor): Decoder.Result[LibraryCatalog] =
  for {
    books <- cursor.downField("books").as[List[Book]](using summon[Decoder[List[Book]]])
    users <- cursor.downField("users").as[List[User]](using summon[Decoder[List[User]]])
    transactions <- cursor.downField("transactions").as[List[Transaction]](using summon[Decoder[List[Transaction]]])
  } yield LibraryCatalog(books, users, transactions)
