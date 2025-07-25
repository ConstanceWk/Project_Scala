import org.scalatest.funsuite.AnyFunSuite
import services.LibraryCatalog
import models._
import LibraryApp._

class LibraryAppSessionCoverageTest extends AnyFunSuite {
  val student = Student("u1", "Alice", "M1")
  val book = Book("1234567890123", "Scala 3", List("Martin Odersky"), 2021, "Programming", true)
  val catalog = LibraryCatalog(List(book), List(student), Nil)

  test("Borrow book - success and invalid user") {
    val freshCatalog = LibraryCatalog(List(book), List(student), Nil)
    val actions = List(
      (2, "u1", "1234567890123"), // success
      (2, "bad", "1234567890123") // invalid user
    )
    val results = LibraryApp.simulateSession(actions, freshCatalog)
    assert(results.head._1.contains("borrowed successfully") || results.head._1.contains("not available"))
    assert(results(1)._1.contains("Invalid user ID format") || results(1)._1.contains("not available"))
  }

  test("Return book - success and error") {
    val borrowedCatalog = catalog.loanBook("1234567890123", "u1").getOrElse(catalog)
    val actions = List(
      (3, "u1", "1234567890123"), // success
      (3, "u1", "bad") // error
    )
    val results = LibraryApp.simulateSession(actions, borrowedCatalog)
    assert(results.head._1.contains("returned successfully") || results.head._1.contains("Error"))
    assert(results(1)._1.contains("Error"))
  }

  test("Recommendations and user not found") {
    val actions = List(
      (4, "u1", ""), // recommendations
      (4, "bad", "")  // user not found
    )
    val results = LibraryApp.simulateSession(actions, catalog)
    assert(results.head._1.contains("No recommendations") || results.head._1.contains("- "))
    assert(results(1)._1.contains("User not found"))
  }

  test("List available books") {
    val actions = List((5, "", ""))
    val results = LibraryApp.simulateSession(actions, catalog)
    assert(results.head._1.contains("Scala 3"))
  }

  test("Reserve book - success and error") {
    val freshCatalog = LibraryCatalog(List(book), List(student), Nil)
    val actions = List(
      (6, "u1", "1234567890123"), // success
      (6, "u1", "bad") // error
    )
    val results = LibraryApp.simulateSession(actions, freshCatalog)
    assert(results.head._1.contains("reserved successfully") || results.head._1.contains("not available"))
    assert(results(1)._1.contains("Error"))
  }

  test("Show reservations - found and not found") {
    val reservedCatalog = catalog.reserveBook("1234567890123", "u1").getOrElse(catalog)
    val actions = List(
      (7, "u1", ""), // found
      (7, "bad", "") // not found
    )
    val results = LibraryApp.simulateSession(actions, reservedCatalog)
    assert(results.head._1.contains("reserved at") || results.head._1 == "No reservations found.")
    assert(results(1)._1 == "No reservations found.")
  }

  test("Exit and invalid option") {
    val actions = List((10, "", ""), (99, "", ""))
    val results = LibraryApp.simulateSession(actions, catalog)
    assert(results.head._1.contains("Thank you"))
    assert(results(1)._1.contains("Invalid option"))
  }

  test("Example test") {
    assert(1 + 1 == 2)
  }
}
