package services

import org.scalatest.funsuite.AnyFunSuite
import models._
import java.time.LocalDateTime

class LibraryCatalogTest extends AnyFunSuite {

  val book = Book("9780000000001", "Scala for the Brave", List("Martin Odersky"), 2022, "Programming", true)
  val user = Student("s1", "Alice", "Bachelor")
  val catalog = LibraryCatalog(List(book), List(user), Nil)

  test("Loan book should update availability and transactions") {
    val result = catalog.loanBook("9780000000001", "s1")
    assert(result.isRight)
    result match {
      case Right(updated) =>
        val bookOpt = updated.books.find(_.isbn == "9780000000001")
        assert(bookOpt.exists(!_.available))
        assert(updated.transactions.exists(_.isInstanceOf[Loan]))
      case Left(_) => fail("Loan should succeed for available book and valid user")
    }
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

  test("Loan should fail for unknown book") {
    val result = catalog.loanBook("9780000000999", "s1")
    assert(result.isLeft)
  }

  test("Return book should update availability and transactions") {
    val loaned = catalog.loanBook("9780000000001", "s1")
    assert(loaned.isRight)
    loaned match {
      case Right(loanedCatalog) =>
        val result = loanedCatalog.returnBook("9780000000001", "s1")
        assert(result.isRight)
        result match {
          case Right(updated) =>
            val bookOpt = updated.books.find(_.isbn == "9780000000001")
            assert(bookOpt.exists(_.available))
            assert(updated.transactions.exists(_.isInstanceOf[Return]))
          case Left(_) => fail("Return should succeed for loaned book and valid user")
        }
      case Left(_) => fail("Loan should succeed before return")
    }
  }

  test("Loan should fail with invalid ISBN format") {
    val invalidBook = book.copy(isbn = "123")
    val catalogWithInvalid = catalog.copy(books = List(invalidBook))
    val result = catalogWithInvalid.loanBook("123", "s1")
    assert(result == Left("Invalid ISBN format"))
  }

  test("Return book should fail if book not loaned or user not found") {
    val notLoaned = catalog.returnBook("9780000000001", "s1")
    assert(notLoaned.isLeft)
    val unknownUser = catalog.returnBook("9780000000001", "unknown")
    assert(unknownUser.isLeft)
  }

  test("Loan should fail if user does not exist") {
    val result = catalog.loanBook("9780000000001", "unknown")
    assert(result.isLeft)
  }

  test("recommendBooks returns empty for user with no history") {
    val recs = catalog.recommendBooks("unknown")
    assert(recs.isEmpty)
  }

  test("topGenres and topAuthors return empty if no transactions") {
    val emptyStats = LibraryCatalog(Nil, Nil, Nil)
    assert(emptyStats.topGenres().isEmpty)
    assert(emptyStats.topAuthors().isEmpty)
  }

  test("synchronizeBookAvailability on empty catalog does not fail") {
    val empty = LibraryCatalog(Nil, Nil, Nil)
    val sync = empty.synchronizeBookAvailability
    assert(sync.books.isEmpty)
  }

  test("findByTitle returns empty if no match") {
    val res = catalog.findByTitle("Inexistant")
    assert(res.isEmpty)
  }

  test("findByAuthor returns books by author") {
    val res = catalog.findByAuthor("Martin")
    assert(res.nonEmpty)
    val res2 = catalog.findByAuthor("Inexistant")
    assert(res2.isEmpty)
  }

  test("availableBooks returns only available books") {
    val unavailable = book.copy(available = false)
    val cat = LibraryCatalog(List(book, unavailable), List(user), Nil)
    val res = cat.availableBooks
    assert(res.size == 1 && res.head.available)
  }

  test("reserveBook succeeds and fails for unavailable book") {
    val reserved = catalog.reserveBook("9780000000001", "s1")
    assert(reserved.isRight)
    val unavailable = book.copy(available = false)
    val cat = LibraryCatalog(List(unavailable), List(user), Nil)
    val fail = cat.reserveBook("9780000000001", "s1")
    assert(fail.isLeft)
  }

  test("reservationsForUser returns empty if none") {
    val res = catalog.reservationsForUser("unknown")
    assert(res.isEmpty)
  }

  test("recommendBooks returns empty if no available books in preferred genre") {
    val loaned = book.copy(available = false)
    val cat = LibraryCatalog(List(loaned), List(user), List(Loan(loaned, user, LocalDateTime.now())))
    val recs = cat.recommendBooks("s1")
    assert(recs.isEmpty)
  }

  test("topGenres/topAuthors handle ties and empty") {
    val c1 = catalog.loanBook("9780000000001", "s1").toOption.get
    val c2 = c1.copy(transactions = c1.transactions ++ List(Loan(book, user, LocalDateTime.now())))
    val genres = c2.topGenres()
    val authors = c2.topAuthors()
    assert(genres.nonEmpty && authors.nonEmpty)
    val empty = LibraryCatalog(Nil, Nil, Nil)
    assert(empty.topGenres().isEmpty && empty.topAuthors().isEmpty)
  }

  test("synchronizeBookAvailability synchronizes after multiple loans/returns") {
    val c1 = catalog.loanBook("9780000000001", "s1").toOption.get
    val c2 = c1.returnBook("9780000000001", "s1").toOption.get
    val sync = c2.synchronizeBookAvailability
    assert(sync.books.forall(_.available))
  }
}