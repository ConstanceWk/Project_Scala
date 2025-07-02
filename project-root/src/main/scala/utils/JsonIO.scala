package utils

import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.auto._
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import io.circe.generic.auto._
import io.circe.Decoder


object JsonIO {
  def saveToFile[T: io.circe.Encoder](data: T, path: String): Unit = {
    val json = data.asJson.spaces2
    Files.write(Paths.get(path), json.getBytes(StandardCharsets.UTF_8))
  }

  def loadFromFile[T: Decoder](path: String): Either[String, T] = {
    try {
        val content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8)
        decode[T](content).left.map(_.getMessage)
    } catch {
        case ex: Exception => Left(ex.getMessage)
    }
    }

}