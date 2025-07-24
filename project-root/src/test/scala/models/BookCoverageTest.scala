package models

import org.scalatest.funsuite.AnyFunSuite

class BookCoverageTest extends AnyFunSuite {
  test("isValidISBN should return true for valid ISBN") {
    val book = Book("9780000000001", "Test", List("A"), 2020, "Test", true)
    assert(book.isValidISBN)
  }
  test("isValidISBN should return false for invalid ISBN") {
    val book = Book("123", "Test", List("A"), 2020, "Test", true)
    assert(!book.isValidISBN)
  }

  test("Book equality and copy") {
    val b1 = Book("9780000000001", "Test", List("A"), 2020, "Test", true)
    val b2 = b1.copy(title = "Other")
    assert(b1 != b2)
    assert(b1.copy() == b1)
  }

  test("Book toString and hashCode") {
    val b = Book("9780000000001", "Test", List("A"), 2020, "Test", true)
    assert(b.toString.contains("Book"))
    assert(b.hashCode != 0)
  }
}
