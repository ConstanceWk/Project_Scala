package services

import io.circe.{Encoder, Decoder, HCursor}
import io.circe.generic.semiauto._
import models._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

given Encoder[LocalDateTime] = Encoder.encodeString.contramap[LocalDateTime](_.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
given Decoder[LocalDateTime] = Decoder.decodeString.emap { str =>
  try Right(LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
  catch case _: Exception => Left(s"Unable to parse LocalDateTime: $str")
}

given Encoder[Book] = deriveEncoder
given Decoder[Book] = deriveDecoder

given Decoder[User] = (c: HCursor) => {
  c.keys.flatMap(_.headOption) match {
    case Some("Student") =>
      val studentCursor = c.downField("Student")
      for {
        id <- studentCursor.downField("id").as[String]
        name <- studentCursor.downField("name").as[String]
        level <- studentCursor.downField("level").as[String]
      } yield Student(id, name, level)
    case Some("Faculty") =>
      val facultyCursor = c.downField("Faculty")
      for {
        id <- facultyCursor.downField("id").as[String]
        name <- facultyCursor.downField("name").as[String]
        department <- facultyCursor.downField("department").as[String]
      } yield Faculty(id, name, department)
    case Some("Librarian") =>
      val librarianCursor = c.downField("Librarian")
      for {
        id <- librarianCursor.downField("id").as[String]
        name <- librarianCursor.downField("name").as[String]
        position <- librarianCursor.downField("position").as[String]
      } yield Librarian(id, name, position)
    case _ =>
      
      for {
        id <- c.downField("id").as[String]
        name <- c.downField("name").as[String]
        level = c.downField("level").as[String].getOrElse("Undergraduate")
      } yield Student(id, name, level)
  }
}

given Encoder[User] = deriveEncoder

given Decoder[Transaction] = (c: HCursor) => {
  
  c.keys.flatMap(_.headOption) match {
    case Some("Loan") =>
      val loanCursor = c.downField("Loan")
      for {
        book <- loanCursor.downField("book").as[Book]
        user <- loanCursor.downField("user").as[User]
        timestamp <- loanCursor.downField("timestamp").as[LocalDateTime]
      } yield Loan(book, user, timestamp)
    case Some("Return") =>
      val returnCursor = c.downField("Return")
      for {
        book <- returnCursor.downField("book").as[Book]
        user <- returnCursor.downField("user").as[User]
        timestamp <- returnCursor.downField("timestamp").as[LocalDateTime]
      } yield Return(book, user, timestamp)
    case _ =>
      for {
        book <- c.downField("book").as[Book]
        user <- c.downField("user").as[User]
        timestamp <- c.downField("timestamp").as[LocalDateTime]
      } yield Loan(book, user, timestamp)
  }
}

given Encoder[Loan] = deriveEncoder
given Encoder[Return] = deriveEncoder
given Encoder[Transaction] = deriveEncoder

given Encoder[LibraryCatalog] = deriveEncoder
given Decoder[LibraryCatalog] = deriveDecoder
