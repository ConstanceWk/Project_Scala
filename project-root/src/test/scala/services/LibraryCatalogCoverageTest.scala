package services

import org.scalatest.funsuite.AnyFunSuite
import models._
import java.time.LocalDateTime

class LibraryCatalogCoverageTest extends AnyFunSuite {
  val book1 = Book("9780000000001", "Book1", List("A1"), 2020, "Genre1", true)
  val book2 = Book("9780000000002", "Book2", List("A2"), 2021, "Genre2", true)
  val user1 = Student("u1", "User1", "Bachelor")
  val user2 = Faculty("u2", "User2", "CS")
  val catalog = LibraryCatalog(List(book1, book2), List(user1, user2), Nil)

  test("reserveBook should add a reservation") {
    val reserved = catalog.reserveBook("9780000000001", "u1")
    assert(reserved.isRight)
    val updated = reserved.toOption.get
    assert(updated.transactions.exists(_.isInstanceOf[Reservation]))
  }

  test("reservationsForUser returns correct reservations") {
    val reserved = catalog.reserveBook("9780000000001", "u1").toOption.get
    val res = reserved.reservationsForUser("u1")
    assert(res.nonEmpty)
    assert(res.head.book.isbn == "9780000000001")
  }

  test("topGenres and topAuthors return correct stats") {
    val c1 = catalog.loanBook("9780000000001", "u1").toOption.get
    val c2 = c1.loanBook("9780000000002", "u2").toOption.get
    val topGenres = c2.topGenres()
    val topAuthors = c2.topAuthors()
    assert(topGenres.nonEmpty)
    assert(topAuthors.nonEmpty)
  }

  test("synchronizeBookAvailability updates availability") {
    val c1 = catalog.loanBook("9780000000001", "u1").toOption.get
    val sync = c1.synchronizeBookAvailability
    assert(!sync.books.find(_.isbn == "9780000000001").get.available)
  }

  test("reserveBook fails for unknown user or unavailable book") {
    val res1 = catalog.reserveBook("9780000000001", "unknown")
    val res2 = catalog.reserveBook("9780000000999", "u1")
    assert(res1.isLeft)
    assert(res2.isLeft)
  }
}
