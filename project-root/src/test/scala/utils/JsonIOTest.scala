package utils

import org.scalatest.funsuite.AnyFunSuite
import models._
import services._
import java.nio.file.{Files, Paths}
import io.circe.generic.auto._

class JsonIOTest extends AnyFunSuite {

  val testPath = "test_library.json"
  val book = Book("999", "Test Book", List("Test Author"), 2020, "Test", true)
  val user = Student("u1", "Test User", "Bachelor")
  val catalog = LibraryCatalog(List(book), List(user), Nil)

  test("Save and load JSON should be consistent") {
    JsonIO.saveToFile(catalog, testPath)
    val loaded = JsonIO.loadFromFile[LibraryCatalog](testPath)

    assert(loaded.isRight)
    assert(loaded.toOption.get.books.head.title == "Test Book")
  }

  test("Load should fail with non-existent file") {
    val result = JsonIO.loadFromFile[LibraryCatalog]("non_existent_file.json")
    result match {
      case Left(error) => assert(error.nonEmpty)
      case Right(_) => fail("Expected failure but got success")
    }
  }

  test("Cleanup temporary file") {
    Files.deleteIfExists(Paths.get(testPath))
    assert(!Files.exists(Paths.get(testPath)))
  }
}
