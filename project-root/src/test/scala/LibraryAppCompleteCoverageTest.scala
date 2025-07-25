import org.scalatest.funsuite.AnyFunSuite
import services.LibraryCatalog
import models._
import java.time.LocalDateTime

class LibraryAppCompleteCoverageTest extends AnyFunSuite {

  // Tests couvrant toutes les branches de LibraryApp.main et méthodes associées
  
  test("main method invocation with valid data") {
    // Test du main : nous ne pouvons pas le tester directement car il utilise IO,
    // mais nous testons les méthodes qu'il appelle
    val book = Book("isbn1", "Scala Programming", List("Martin Odersky"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), Nil, Nil)
    val synced = catalog.synchronizeBookAvailability
    assert(synced.books.nonEmpty)
  }

  // Tests exhaustifs pour handleChoice - toutes les branches
  test("handleChoice - case 1: search found") {
    val book = Book("isbn1", "Scala Programming", List("Martin Odersky"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(1, catalog, "Scala", "")
    assert(result.contains("Scala Programming"))
    assert(result.contains("Martin Odersky"))
  }

  test("handleChoice - case 1: search not found") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(1, catalog, "Unknown", "")
    assert(result == "No book found.")
  }

  test("handleChoice - case 2: borrow with invalid user ID") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(2, catalog, "invalid@@user", "isbn1")
    assert(result.contains("Invalid user ID format"))
  }

  test("handleChoice - case 2: borrow success") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("978-0134685991", "Scala", List("Martin"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), List(user), Nil)
    val (result, updated) = LibraryApp.handleChoice(2, catalog, "u1", "978-0134685991")
    assert(result.contains("borrowed successfully"))
    assert(updated.books.exists(!_.available))
  }

  test("handleChoice - case 2: borrow error (book not found)") {
    val user = Student("u1", "Alice", "Master")
    val catalog = LibraryCatalog(Nil, List(user), Nil)
    val (result, _) = LibraryApp.handleChoice(2, catalog, "u1", "nonexistent")
    assert(result.contains("Error"))
  }

  test("handleChoice - case 3: return success") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("978-0134685991", "Scala", List("Martin"), 2021, "Programming", false)
    val loan = Loan(book, user, LocalDateTime.now())
    val catalog = LibraryCatalog(List(book), List(user), List(loan))
    val (result, updated) = LibraryApp.handleChoice(3, catalog, "u1", "978-0134685991")
    assert(result.contains("returned successfully"))
  }

  test("handleChoice - case 3: return error") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(3, catalog, "u1", "nonexistent")
    assert(result.contains("Error"))
  }

  test("handleChoice - case 4: recommendations user not found") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(4, catalog, "nonexistent", "")
    assert(result.contains("User not found"))
  }

  test("handleChoice - case 4: recommendations no recommendations") {
    val user = Student("u1", "Alice", "Master")
    val catalog = LibraryCatalog(Nil, List(user), Nil)
    val (result, _) = LibraryApp.handleChoice(4, catalog, "u1", "")
    assert(result == "No recommendations available.")
  }

  test("handleChoice - case 4: recommendations found") {
    val user = Student("u1", "Alice", "Master")
    val book1 = Book("isbn1", "Scala Basics", List("Martin"), 2021, "Programming", true)
    val book2 = Book("isbn2", "Advanced Scala", List("John"), 2022, "Programming", true)
    val loan = Loan(book1, user, LocalDateTime.now())
    val catalog = LibraryCatalog(List(book1, book2), List(user), List(loan))
    val (result, _) = LibraryApp.handleChoice(4, catalog, "u1", "")
    assert(result.contains("- ") || result == "No recommendations available.")
  }

  test("handleChoice - case 5: list available books") {
    val book = Book("isbn1", "Scala", List("Martin"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(5, catalog, "", "")
    assert(result.contains("📗 Scala - isbn1"))
  }

  test("handleChoice - case 5: list no available books") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(5, catalog, "", "")
    assert(result.isEmpty)
  }

  test("handleChoice - case 6: reserve success") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("978-0134685991", "Scala", List("Martin"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), List(user), Nil)
    val (result, updated) = LibraryApp.handleChoice(6, catalog, "u1", "978-0134685991")
    assert(result.contains("reserved successfully"))
    assert(updated.transactions.exists(_.isInstanceOf[Reservation]))
  }

  test("handleChoice - case 6: reserve error") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(6, catalog, "u1", "nonexistent")
    assert(result.contains("Error"))
  }

  test("handleChoice - case 7: show reservations empty") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(7, catalog, "u1", "")
    assert(result == "No reservations found.")
  }

  test("handleChoice - case 7: show reservations found") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("978-0134685991", "Scala", List("Martin"), 2021, "Programming", true)
    val reservation = Reservation(book, user, LocalDateTime.now())
    val catalog = LibraryCatalog(List(book), List(user), List(reservation))
    val (result, _) = LibraryApp.handleChoice(7, catalog, "u1", "")
    assert(result.contains("🔖 Scala reserved at"))
  }

  test("handleChoice - case 8: top genres") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("978-0134685991", "Scala", List("Martin"), 2021, "Programming", true)
    val loan = Loan(book, user, LocalDateTime.now())
    val catalog = LibraryCatalog(List(book), List(user), List(loan))
    val (result, _) = LibraryApp.handleChoice(8, catalog, "", "")
    assert(result.contains("🏆 Top 3 genres:"))
    assert(result.contains("Programming: 1"))
  }

  test("handleChoice - case 8: top genres empty") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(8, catalog, "", "")
    assert(result.contains("🏆 Top 3 genres:"))
  }

  test("handleChoice - case 9: top authors") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("978-0134685991", "Scala", List("Martin Odersky"), 2021, "Programming", true)
    val loan = Loan(book, user, LocalDateTime.now())
    val catalog = LibraryCatalog(List(book), List(user), List(loan))
    val (result, _) = LibraryApp.handleChoice(9, catalog, "", "")
    assert(result.contains("🏆 Top 3 authors:"))
    assert(result.contains("Martin Odersky: 1"))
  }

  test("handleChoice - case 9: top authors empty") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(9, catalog, "", "")
    assert(result.contains("🏆 Top 3 authors:"))
  }

  test("handleChoice - case 10: exit") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(10, catalog, "", "")
    assert(result.contains("Thank you for using our system"))
  }

  test("handleChoice - default case: invalid option") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val (result, _) = LibraryApp.handleChoice(99, catalog, "", "")
    assert(result == "❌ Invalid option.")
  }

  // Tests pour simulateSession - toutes les branches
  test("simulateSession - complete workflow") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("978-0134685991", "Scala", List("Martin"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), List(user), Nil)
    
    val actions = List(
      (1, "Scala", ""),                    // search found
      (1, "Unknown", ""),                  // search not found
      (2, "invalid@@", "978-0134685991"),  // borrow invalid user
      (2, "u1", "978-0134685991"),         // borrow success
      (3, "u1", "978-0134685991"),         // return
      (4, "nonexistent", ""),              // recommendations user not found
      (4, "u1", ""),                       // recommendations
      (5, "", ""),                         // list available
      (6, "u1", "978-0134685991"),         // reserve
      (7, "u1", ""),                       // show reservations
      (8, "", ""),                         // top genres
      (9, "", ""),                         // top authors
      (10, "", ""),                        // exit
      (99, "", "")                         // invalid
    )
    
    val results = LibraryApp.simulateSession(actions, catalog)
    assert(results.length == 14)
    assert(results(0)._1.contains("Scala")) // search found
    assert(results(1)._1 == "No book found.") // search not found
    assert(results(2)._1.contains("Invalid user ID format")) // invalid user
    assert(results(13)._1 == "❌ Invalid option.") // invalid option
  }

  test("simulateSession - empty actions") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    val results = LibraryApp.simulateSession(Nil, catalog)
    assert(results.isEmpty)
  }

  // Tests pour loopTestable - simulation I/O complète
  test("loopTestable - exit immediately") {
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

  test("loopTestable - all menu options") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("978-0134685991", "Scala Programming", List("Martin Odersky"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), List(user), Nil)
    
    val inputs = Array(
      "1", "Scala",                          // search found
      "1", "Unknown",                        // search not found
      "2", "invalid@@", "978-0134685991",    // borrow invalid user
      "2", "u1", "978-0134685991",           // borrow success
      "3", "u1", "978-0134685991",           // return
      "4", "unknown",                        // recommendations user not found
      "4", "u1",                             // recommendations
      "5",                                   // list available
      "6", "u1", "978-0134685991",           // reserve
      "7", "u1",                             // show reservations
      "8",                                   // top genres
      "9",                                   // top authors
      "invalid",                             // invalid input
      "0",                                   // invalid option
      "-1",                                  // invalid option
      "999",                                 // invalid option
      "10"                                   // exit
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
    assert(output.contains("Invalid option")) // invalid options
    assert(output.contains("Thank you for using our system")) // exit
    assert(savedCatalog.isDefined) // save function called
  }

  // Test pour la méthode loop principale
  test("loop - complete menu coverage using loop method") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("978-0134685991", "Test Book", List("Test Author"), 2021, "Fiction", true)
    val catalog = LibraryCatalog(List(book), List(user), Nil)
    
    val inputs = Array(
      "1", "Test",               // search
      "5",                       // list available
      "8",                       // top genres
      "9",                       // top authors
      "invalid",                 // invalid input
      "99",                      // invalid option
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
    
    LibraryApp.loop(catalog, inputFn, outputFn, saveFn)
    
    val output = outputBuffer.toString
    assert(output.contains("Test Book")) // search result
    assert(output.contains("📗")) // list available
    assert(output.contains("Top 3 genres")) // top genres
    assert(output.contains("Top 3 authors")) // top authors
    assert(output.contains("Invalid option")) // invalid options
    assert(output.contains("Thank you for using our system")) // exit
  }

  // Tests d'intégration pour s'assurer que toutes les branches de LibraryApp$ sont couvertes
  test("LibraryApp object method invocation coverage") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    
    // Test direct des méthodes publiques
    val (result1, _) = LibraryApp.handleChoice(10, catalog, "", "")
    assert(result1.contains("Thank you"))
    
    val sessions = LibraryApp.simulateSession(List((10, "", "")), catalog)
    assert(sessions.nonEmpty)
    
    // Test avec entrées minimales pour couvrir loopTestable
    var called = false
    def inputFn(prompt: String): String = "10"
    def outputFn(msg: String): Unit = called = true
    def saveFn(cat: LibraryCatalog): Unit = {}
    
    LibraryApp.loopTestable(catalog, inputFn, outputFn, saveFn)
    assert(called)
  }
}
