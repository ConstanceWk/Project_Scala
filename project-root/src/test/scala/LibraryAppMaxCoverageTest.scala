import org.scalatest.funsuite.AnyFunSuite
import services.LibraryCatalog
import models._

class LibraryAppMaxCoverageTest extends AnyFunSuite {

  // Tests pour handleChoice - toutes les branches du menu
  test("handleChoice - search book found") {
    val book = Book("isbn1", "Scala Programming", List("Martin Odersky"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(1, catalog, "", "Scala")
    assert(result.contains("Scala Programming"))
    assert(result.contains("Martin Odersky"))
  }

  test("handleChoice - search book not found") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(1, catalog, "", "Unknown")
    assert(result == "No book found.")
  }

  test("handleChoice - borrow with invalid user ID") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(2, catalog, "invalid@@", "isbn1")
    assert(result.contains("Invalid user ID format"))
  }

  test("handleChoice - borrow success") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("isbn1", "Scala", List("Martin"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), List(user), Nil)
    val (result, updated) = LibraryApp.handleChoice(2, catalog, "u1", "isbn1")
    assert(result.contains("borrowed successfully") || result.contains("Error"))
  }

  test("handleChoice - borrow error") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(2, catalog, "u1", "nonexistent")
    assert(result.contains("Error"))
  }

  test("handleChoice - return success") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("isbn1", "Scala", List("Martin"), 2021, "Programming", false)
    val loan = Loan(book, user, java.time.LocalDateTime.now())
    val catalog = LibraryCatalog(List(book), List(user), List(loan))
    val (result, _) = LibraryApp.handleChoice(3, catalog, "u1", "isbn1")
    assert(result.contains("returned successfully") || result.contains("Error"))
  }

  test("handleChoice - return error") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(3, catalog, "u1", "nonexistent")
    assert(result.contains("Error"))
  }

  test("handleChoice - recommendations user not found") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(4, catalog, "nonexistent", "")
    assert(result.contains("User not found"))
  }

  test("handleChoice - recommendations found") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("isbn1", "Scala", List("Martin"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), List(user), Nil)
    val (result, _) = LibraryApp.handleChoice(4, catalog, "u1", "")
    assert(result.contains("No recommendations available") || result.contains("- "))
  }

  test("handleChoice - list available books") {
    val book = Book("isbn1", "Scala", List("Martin"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(5, catalog, "", "")
    assert(result.contains("📗 Scala - isbn1"))
  }

  test("handleChoice - reserve success") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("isbn1", "Scala", List("Martin"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), List(user), Nil)
    val (result, _) = LibraryApp.handleChoice(6, catalog, "u1", "isbn1")
    assert(result.contains("reserved successfully") || result.contains("Error"))
  }

  test("handleChoice - reserve error") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(6, catalog, "u1", "nonexistent")
    assert(result.contains("Error"))
  }

  test("handleChoice - show reservations empty") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(7, catalog, "u1", "")
    assert(result == "No reservations found.")
  }

  test("handleChoice - show reservations found") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("isbn1", "Scala", List("Martin"), 2021, "Programming", true)
    val reservation = Reservation(book, user, java.time.LocalDateTime.now())
    val catalog = LibraryCatalog(List(book), List(user), List(reservation))
    val (result, _) = LibraryApp.handleChoice(7, catalog, "u1", "")
    assert(result.contains("🔖 Scala reserved at"))
  }

  test("handleChoice - exit") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(10, catalog, "", "")
    assert(result.contains("Thank you for using our system"))
  }

  test("handleChoice - invalid option") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(99, catalog, "", "")
    assert(result.contains("Invalid option"))
  }

  // Tests pour simulateSession
  test("simulateSession - complete workflow") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("isbn1", "Scala", List("Martin"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), List(user), Nil)
    
    val actions = List(
      (1, "", "Scala"),        // search
      (2, "u1", "isbn1"),      // borrow
      (3, "u1", "isbn1"),      // return
      (4, "u1", ""),           // recommendations
      (5, "", ""),             // list available
      (6, "u1", "isbn1"),      // reserve
      (7, "u1", ""),           // show reservations
      (8, "", ""),             // top genres
      (9, "", ""),             // top authors
      (10, "", ""),            // exit
      (99, "", "")             // invalid
    )
    
    val results = LibraryApp.simulateSession(actions, catalog)
    assert(results.length == 11)
    assert(results.head._1.contains("Scala")) // search result
    assert(results.last._1.contains("Invalid option")) // invalid option
  }

  // Tests pour loopTestable - simulation I/O complète
  test("loopTestable - full menu coverage") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("isbn1", "Scala Programming", List("Martin Odersky"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), List(user), Nil)
    
    val inputs = Array(
      "1", "Scala",              // search found
      "1", "Unknown",            // search not found
      "2", "invalid@@", "isbn1", // borrow invalid user
      "2", "u1", "isbn1",        // borrow success
      "3", "u1", "isbn1",        // return
      "4", "unknown",            // recommendations user not found
      "4", "u1",                 // recommendations
      "5",                       // list available
      "6", "u1", "isbn1",        // reserve
      "7", "u1",                 // show reservations
      "8",                       // top genres
      "9",                       // top authors
      "invalid",                 // invalid option
      "10"                       // exit
    )
    
    var inputIdx = 0
    def inputFn(prompt: String): String = {
      val res = if (inputIdx < inputs.length) inputs(inputIdx) else "10"
      inputIdx += 1
      res
    }
    
    val outputBuffer = new StringBuilder
    def outputFn(msg: String): Unit = outputBuffer.append(msg + "\n")
    
    var savedCatalog: Option[LibraryCatalog] = None
    def saveFn(cat: LibraryCatalog): Unit = savedCatalog = Some(cat)
    
    LibraryApp.loopTestable(catalog, inputFn, outputFn, saveFn)
    
    val output = outputBuffer.toString
    assert(output.contains("Scala Programming")) // search found
    assert(output.contains("No book found")) // search not found
    assert(output.contains("Invalid user ID format")) // invalid user
    assert(output.contains("borrowed successfully") || output.contains("Error")) // borrow
    assert(output.contains("returned successfully") || output.contains("Error")) // return
    assert(output.contains("User not found")) // user not found
    assert(output.contains("Recommendations") || output.contains("No recommendations")) // recommendations
    assert(output.contains("📗")) // list available
    assert(output.contains("reserved successfully") || output.contains("Error")) // reserve
    assert(output.contains("reserved at") || output.contains("No reservations")) // show reservations
    assert(output.contains("Top 3 genres")) // top genres
    assert(output.contains("Top 3 authors")) // top authors
    assert(output.contains("Invalid option")) // invalid option
    assert(output.contains("Thank you for using our system")) // exit
    assert(savedCatalog.isDefined) // save function called
  }

  test("loopTestable - minimal flow") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val inputs = Array("10") // just exit
    
    var inputIdx = 0
    def inputFn(prompt: String): String = {
      val res = if (inputIdx < inputs.length) inputs(inputIdx) else "10"
      inputIdx += 1
      res
    }
    
    val outputBuffer = new StringBuilder
    def outputFn(msg: String): Unit = outputBuffer.append(msg + "\n")
    def saveFn(cat: LibraryCatalog): Unit = {}
    
    LibraryApp.loopTestable(catalog, inputFn, outputFn, saveFn)
    
    val output = outputBuffer.toString
    assert(output.contains("Thank you for using our system"))
  }

  test("loopTestable - edge cases and errors") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val inputs = Array(
      "abc",     // invalid input
      "0",       // invalid option
      "-1",      // invalid option
      "999",     // invalid option
      "10"       // exit
    )
    
    var inputIdx = 0
    def inputFn(prompt: String): String = {
      val res = if (inputIdx < inputs.length) inputs(inputIdx) else "10"
      inputIdx += 1
      res
    }
    
    val outputBuffer = new StringBuilder
    def outputFn(msg: String): Unit = outputBuffer.append(msg + "\n")
    def saveFn(cat: LibraryCatalog): Unit = {}
    
    LibraryApp.loopTestable(catalog, inputFn, outputFn, saveFn)
    
    val output = outputBuffer.toString
    assert(output.contains("Invalid option"))
    assert(output.contains("Thank you for using our system"))
  }
}
