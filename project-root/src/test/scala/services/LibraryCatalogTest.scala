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

  // Test ciblé pour la branche non couverte : Left("Book not available or User not found")
  test("Loan should return 'Book not available or User not found' for unavailable book and unknown user") {
    val unavailableBook = book.copy(available = false)
    val catalogUnavailable = catalog.copy(books = List(unavailableBook))
    val result = catalogUnavailable.loanBook(unavailableBook.isbn, "unknown")
    assert(result == Left("Book not available or User not found"))
  }

  // Test ciblé pour la branche non couverte : Left("Book not found or User not found")
  test("Return should return 'Book not found or User not found' for unknown book and unknown user") {
    val result = catalog.returnBook("unknown_isbn", "unknown_user")
    assert(result == Left("Book not found or User not found"))
  }

  // Test pour vérifier que les autres livres ne sont pas modifiés lors d'un prêt
  test("Loan book should not modify other books in the catalog") {
    val book2 = Book("9780000000002", "Scala for the Wise", List("Martin Odersky"), 2022, "Programming", true)
    val catalogWithTwo = catalog.copy(books = List(book, book2))
    val result = catalogWithTwo.loanBook("9780000000001", "s1")
    assert(result.isRight)
    result match {
      case Right(updated) =>
        val untouchedBook = updated.books.find(_.isbn == "9780000000002").get
        assert(untouchedBook == book2)
      case _ => fail("Loan should succeed")
    }
  }
  test("LibraryCatalog topGenres and topAuthors") {
  val book1 = Book("isbn1", "Title1", List("Author1"), 2020, "Fiction", true)
  val book2 = Book("isbn2", "Title2", List("Author2"), 2021, "Science", true)
  val book3 = Book("isbn3", "Title3", List("Author1"), 2022, "Fiction", true)
  val user = Student("user1", "Alice", "Bachelor")
  val transactions = List(
    Loan(book1, user, LocalDateTime.now()),
    Loan(book2, user, LocalDateTime.now()),
    Loan(book3, user, LocalDateTime.now())
  )
  val catalog = LibraryCatalog(List(book1, book2, book3), List(user), transactions)

  val topGenres = catalog.topGenres()
  assert(topGenres == List(("Fiction", 2), ("Science", 1)))

  val topAuthors = catalog.topAuthors()
  assert(topAuthors == List(("Author1", 2), ("Author2", 1)))
 }
 test("recommendBooks returns books in preferred genres based on transaction history") {
  val book1 = Book("isbn1", "Title1", List("Author1"), 2020, "Fiction", true)
  val book2 = Book("isbn2", "Title2", List("Author2"), 2021, "Science", true)
  val book3 = Book("isbn3", "Title3", List("Author1"), 2022, "Fiction", true)
  val unavailableBook = Book("isbn4", "Title4", List("Author3"), 2023, "Fiction", false)
  val user = Student("user1", "Alice", "Bachelor")
  val transactions = List(
    Loan(book1, user, LocalDateTime.now()),
    Loan(book2, user, LocalDateTime.now()),
    Loan(book3, user, LocalDateTime.now())
  )
  val catalog = LibraryCatalog(List(book1, book2, book3, unavailableBook), List(user), transactions)

  val recommendations = catalog.recommendBooks("user1")
  assert(recommendations == List(book1, book3)) // Les livres disponibles dans le genre "Fiction"
 } 

}
