package services

import org.scalatest.funsuite.AnyFunSuite
import models._
import java.time.LocalDateTime
import utils.JsonIO
import java.nio.file.{Files, Paths}
import io.circe.generic.auto._

class LibraryIntegrationTest extends AnyFunSuite {
  val book1 = Book("9780000000001", "FP in Scala", List("Chiusano", "Bjarnason"), 2015, "Programming", true)
  val book2 = Book("9780000000002", "Scala Puzzlers", List("Suereth"), 2014, "Programming", true)
  val user = Student("stu1", "Bob", "Master")
  val catalog = LibraryCatalog(List(book1, book2), List(user), Nil)
  val testPath = "integration_test_library.json"

  test("Full scenario: loan, return, recommend, persist") {
    // Loan a book
    val loanResult = catalog.loanBook("9780000000001", "stu1")
    assert(loanResult.isRight, s"Loan failed: ${loanResult.left.toOption.getOrElse("")}")
    loanResult match {
      case Right(afterLoan) =>
        assert(!afterLoan.books.find(_.isbn == "9780000000001").get.available)
        assert(afterLoan.transactions.exists(_.isInstanceOf[Loan]))

        // Return the book
        val returnResult = afterLoan.returnBook("9780000000001", "stu1")
        assert(returnResult.isRight, s"Return failed: ${returnResult.left.toOption.getOrElse("")}")
        returnResult match {
          case Right(afterReturn) =>
            assert(afterReturn.books.find(_.isbn == "9780000000001").get.available)
            assert(afterReturn.transactions.exists(_.isInstanceOf[Return]))

            // Recommend books
            val recs = afterReturn.recommendBooks("stu1")
            assert(recs.exists(_.isbn == "9780000000002"))

            // Persist and reload
            JsonIO.saveToFile(afterReturn, testPath)
            val loaded = JsonIO.loadFromFile[LibraryCatalog](testPath)
            assert(loaded.isRight)
            assert(loaded.toOption.get.books.size == 2)
            Files.deleteIfExists(Paths.get(testPath))
          case Left(err) => fail(s"Return failed: $err")
        }
      case Left(err) => fail(s"Loan failed: $err")
    }
  }

  test("Loan should fail for unknown user") {
    val result = catalog.loanBook("9780000000001", "unknown")
    assert(result.isLeft)
  }

  test("Return should fail for book not loaned") {
    val result = catalog.returnBook("9780000000002", "stu1")
    assert(result.isLeft)
  }
}
