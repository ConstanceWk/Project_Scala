package utils

import org.scalatest.funsuite.AnyFunSuite
import models.Book
import services.given_Encoder_Book

class JsonIOErrorCoverageTest extends AnyFunSuite {
  test("saveToFile should throw for invalid path") {
    val book = Book("9780000000001", "Test", List("A"), 2020, "Test", true)
    intercept[Exception] {
      JsonIO.saveToFile(book, "/invalid_path/should_fail.json")
    }
  }
}
