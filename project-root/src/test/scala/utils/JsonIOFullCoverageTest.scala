package utils

import org.scalatest.funsuite.AnyFunSuite
import utils.JsonIO
import models._
import services._
import java.nio.file.{Files, Paths}
import io.circe.generic.auto._

class JsonIOFullCoverageTest extends AnyFunSuite {
  val testPath = "test_fullcov.json"
  val book = Book("999", "Test Book", List("Test Author"), 2020, "Test", true)
  val user = Student("u1", "Test User", "Bachelor")
  val catalog = LibraryCatalog(List(book), List(user), Nil)

  test("loadFromFile returns Left for malformed JSON") {
    val path = "malformed.json"
    Files.write(Paths.get(path), "{".getBytes)
    val result = JsonIO.loadFromFile[LibraryCatalog](path)
    assert(result.isLeft)
    Files.deleteIfExists(Paths.get(path))
  }

  test("loadFromFile returns Left for invalid JSON structure") {
    val path = "invalidstruct.json"
    Files.write(Paths.get(path), "{\"foo\":123}".getBytes)
    val result = JsonIO.loadFromFile[LibraryCatalog](path)
    assert(result.isLeft)
    Files.deleteIfExists(Paths.get(path))
  }
}
