package services

import org.scalatest.propspec.AnyPropSpec
import org.scalacheck.Prop.forAll
import org.scalacheck.Gen
import models._
import java.time.LocalDateTime

class LibraryCatalogPropertyTest extends AnyPropSpec {

  val bookGen: Gen[Book] = for {
    isbn <- Gen.alphaNumStr.suchThat(_.nonEmpty)
    title <- Gen.alphaStr
    authors <- Gen.listOf(Gen.alphaStr)
    year <- Gen.choose(1900, 2025)
    genre <- Gen.oneOf("Fiction", "Programming", "Science", "Fantasy")
    available <- Gen.oneOf(true, false)
  } yield Book(isbn, title, authors, year, genre, available)

  val userGen: Gen[User] = for {
    id <- Gen.alphaNumStr.suchThat(_.nonEmpty)
    name <- Gen.alphaStr
    user <- Gen.oneOf(
      Gen.const(Student(id, name, "Bachelor")),
      Gen.const(Faculty(id, name, "CS")),
      Gen.const(Librarian(id, name, "Head"))
    )
  } yield user

  property("loanBook should either succeed or return error") {
    forAll(bookGen, userGen) { (book, user) =>
      val catalog = LibraryCatalog(List(book.copy(available = true)), List(user), Nil)
      val result = catalog.loanBook(book.isbn, user.id)
      result.isRight || result.isLeft
    }
  }

  property("loanBook should fail if user does not exist") {
    forAll(bookGen) { book =>
      val catalog = LibraryCatalog(List(book.copy(available = true)), Nil, Nil)
      val result = catalog.loanBook(book.isbn, "unknown")
      result.isLeft
    }
  }

  property("returnBook should succeed only if book was loaned") {
    forAll(bookGen, userGen) { (book, user) =>
      val catalog = LibraryCatalog(List(book.copy(available = true)), List(user), Nil)
      val afterLoan = catalog.loanBook(book.isbn, user.id)
      if (afterLoan.isRight) {
        val afterReturn = afterLoan.toOption.get.returnBook(book.isbn, user.id)
        afterReturn.isRight
      } else {
        val result = catalog.returnBook(book.isbn, user.id)
        result.isLeft
      }
    }
  }
}
