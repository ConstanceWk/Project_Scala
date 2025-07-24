package models

import org.scalatest.funsuite.AnyFunSuite
import java.time.LocalDateTime

class TransactionCoverageTest extends AnyFunSuite {
  val book = Book("9780000000001", "Book1", List("A1"), 2020, "Genre1", true)
  val user = Student("u1", "User1", "Bachelor")
  val now = LocalDateTime.now()

  test("Loan, Return, Reservation case classes") {
    val loan = Loan(book, user, now)
    val ret = Return(book, user, now)
    val res = Reservation(book, user, now)
    assert(loan.book == book && loan.user == user)
    assert(ret.book == book && ret.user == user)
    assert(res.book == book && res.user == user)
  }

  test("Transaction equality and copy") {
    val book = Book("9780000000001", "Book1", List("A1"), 2020, "Genre1", true)
    val user = Student("u1", "User1", "Bachelor")
    val now = LocalDateTime.now()
    val loan1 = Loan(book, user, now)
    val loan2 = loan1.copy()
    assert(loan1 == loan2)
    val ret1 = Return(book, user, now)
    val ret2 = ret1.copy()
    assert(ret1 == ret2)
    val res1 = Reservation(book, user, now)
    val res2 = res1.copy()
    assert(res1 == res2)
  }

  test("Transaction toString and hashCode") {
    val book = Book("9780000000001", "Book1", List("A1"), 2020, "Genre1", true)
    val user = Student("u1", "User1", "Bachelor")
    val now = LocalDateTime.now()
    val loan = Loan(book, user, now)
    assert(loan.toString.contains("Loan"))
    assert(loan.hashCode != 0)
  }
}
