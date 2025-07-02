package services

import io.circe.{Encoder, Decoder, HCursor}
import io.circe.generic.semiauto._
import models._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Encoders/Decoders pour LocalDateTime
given Encoder[LocalDateTime] = Encoder.encodeString.contramap[LocalDateTime](_.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
given Decoder[LocalDateTime] = Decoder.decodeString.emap { str =>
  try Right(LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
  catch case _: Exception => Left(s"Unable to parse LocalDateTime: $str")
}

given Encoder[Book] = deriveEncoder
given Decoder[Book] = deriveDecoder

// Decoder personnalisé pour User qui traite les objets simples comme des Students
given Decoder[User] = (c: HCursor) => {
  for {
    id <- c.downField("id").as[String]
    name <- c.downField("name").as[String]
    // Essayer de lire les champs spécifiques, sinon créer un Student par défaut
    level = c.downField("level").as[String].getOrElse("Undergraduate")
  } yield Student(id, name, level)
}

given Encoder[User] = deriveEncoder

// Decoder personnalisé pour Transaction qui traite les objets simples comme des Loans
given Decoder[Transaction] = (c: HCursor) => {
  for {
    book <- c.downField("book").as[Book]
    user <- c.downField("user").as[User]
    timestamp <- c.downField("timestamp").as[LocalDateTime]
  } yield Loan(book, user, timestamp)
}

given Encoder[Transaction] = deriveEncoder

given Encoder[LibraryCatalog] = deriveEncoder
given Decoder[LibraryCatalog] = deriveDecoder
