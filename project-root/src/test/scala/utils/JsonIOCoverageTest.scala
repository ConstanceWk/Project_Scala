package utils

import org.scalatest.funsuite.AnyFunSuite
import java.nio.file.{Files, Paths}
import models.Book
import services.{given_Encoder_Book, given_Decoder_Book}

class JsonIOCoverageTest extends AnyFunSuite {
  val testPath = "test_jsonio_coverage.json"
  val book = Book("9780000000001", "Test", List("A"), 2020, "Test", true)

  test("saveToFile and loadFromFile should persist and reload data") {
    JsonIO.saveToFile(book, testPath)
    val loaded = JsonIO.loadFromFile[Book](testPath)
    assert(loaded.isRight)
    assert(loaded.toOption.get == book)
    Files.deleteIfExists(Paths.get(testPath))
  }

  test("loadFromFile should return Left on missing file") {
    val result = JsonIO.loadFromFile[Book]("does_not_exist.json")
    assert(result.isLeft)
  }
}
