package services

import org.scalatest.funsuite.AnyFunSuite
import models._
import java.time.LocalDateTime

class LibraryCatalogTest extends AnyFunSuite {

  val book = Book("123", "Scala for the Brave", List("Martin Odersky"), 2022, "Programming", true)
  val user = Student("s1", "Alice", "Bachelor")
  val catalog = LibraryCatalog(List(book), List(user), Nil)

  test("Loan book should update availability and transactions") {
    val result = catalog.loanBook("123", "s1")
    assert(result.isRight)

    val updated = result.toOption.get
    assert(!updated.books.head.available)
    assert(updated.transactions.exists(_.isInstanceOf[Loan]))
  }

  test("Loan should fail if book not available") {
    val catalogUnavailable = catalog.copy(books = List(book.copy(available = false)))
    val result = catalogUnavailable.loanBook("123", "s1")
    assert(result.isLeft)
  }

  test("Recommendation should return available books in preferred genre") {
    val unavailableBook = book.copy(available = false)
    val loaned = Loan(unavailableBook, user, LocalDateTime.now())
    val updatedCatalog = LibraryCatalog(List(unavailableBook), List(user), List(loaned))
    val recs = updatedCatalog.recommendBooks("s1")
    assert(recs.isEmpty)

  }
}