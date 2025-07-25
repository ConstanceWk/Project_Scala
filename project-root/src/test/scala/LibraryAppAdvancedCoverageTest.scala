import org.scalatest.funsuite.AnyFunSuite
import services.LibraryCatalog
import models._
import java.time.LocalDateTime

class LibraryAppAdvancedCoverageTest extends AnyFunSuite {

  // Tests avancés pour maximiser le coverage de LibraryApp$

  test("main method integration - full flow simulation") {
    // Test de la méthode main avec simulation d'un fichier JSON
    val book = Book("978-0134685991", "Test Book", List("Author"), 2021, "Fiction", true)
    val catalog = LibraryCatalog(List(book), Nil, Nil)
    
    // Test de synchronizeBookAvailability appelée dans main
    val syncedCatalog = catalog.synchronizeBookAvailability
    assert(syncedCatalog.books.size == 1)
    assert(syncedCatalog.books.head.available)
  }

  test("loop method - comprehensive edge cases") {
    val user = Student("u1", "Alice", "Master")
    val book1 = Book("978-0134685991", "Book1", List("Author1"), 2021, "Fiction", true)
    val book2 = Book("978-0134685992", "Book2", List("Author2"), 2022, "Science", false)
    val loan = Loan(book2, user, LocalDateTime.now())
    val catalog = LibraryCatalog(List(book1, book2), List(user), List(loan))
    
    val inputs = Array(
      "2", "u1", "978-0134685991",  // borrow available book
      "2", "u1", "978-0134685992",  // try to borrow unavailable book
      "3", "u1", "978-0134685992",  // return borrowed book
      "4", "u1",                    // get recommendations
      "6", "u1", "978-0134685991",  // reserve book
      "7", "u1",                    // show reservations
      "10"                          // exit
    )
    
    var inputIdx = 0
    def inputFn(prompt: String): String = {
      val res = if (inputIdx < inputs.length) inputs(inputIdx) else "10"
      inputIdx += 1
      res
    }
    
    val outputBuffer = new StringBuilder
    def outputFn(msg: String): Unit = outputBuffer.append(msg + "\n")
    
    var saveCallCount = 0
    def saveFn(cat: LibraryCatalog): Unit = saveCallCount += 1
    
    LibraryApp.loop(catalog, inputFn, outputFn, saveFn)
    
    val output = outputBuffer.toString
    assert(output.contains("Thank you for using our system"))
    assert(saveCallCount > 0) // Verify save was called
  }

  test("loopTestable - complex scenarios with multiple operations") {
    val user1 = Student("s1", "Student1", "Master")
    val user2 = Faculty("f1", "Faculty1", "Computer Science")
    val book1 = Book("978-0134685991", "Programming", List("Author1"), 2021, "Tech", true)
    val book2 = Book("978-0134685992", "Mathematics", List("Author2"), 2022, "Math", true)
    val catalog = LibraryCatalog(List(book1, book2), List(user1, user2), Nil)
    
    val inputs = Array(
      "1", "Programming",            // search - found
      "1", "Nonexistent",           // search - not found
      "2", "s1", "978-0134685991",  // borrow as student
      "2", "f1", "978-0134685992",  // borrow as faculty
      "3", "s1", "978-0134685991",  // return by student
      "3", "f1", "978-0134685992",  // return by faculty
      "4", "s1",                    // recommendations for student
      "4", "f1",                    // recommendations for faculty
      "5",                          // list available books
      "6", "s1", "978-0134685991",  // reserve by student
      "6", "f1", "978-0134685992",  // reserve by faculty
      "7", "s1",                    // show student reservations
      "7", "f1",                    // show faculty reservations
      "8",                          // top genres
      "9",                          // top authors
      "abc",                        // invalid input (non-numeric)
      "",                           // empty input
      " ",                          // whitespace only
      "0",                          // invalid option
      "-5",                         // negative option
      "100",                        // out of range option
      "10"                          // exit
    )
    
    var inputIdx = 0
    def inputFn(prompt: String): String = {
      val res = if (inputIdx < inputs.length) inputs(inputIdx) else "10"
      inputIdx += 1
      res
    }
    
    val outputBuffer = new StringBuilder
    def outputFn(msg: String): Unit = outputBuffer.append(msg + "\n")
    
    var catalogsaved: List[LibraryCatalog] = Nil
    def saveFn(cat: LibraryCatalog): Unit = catalogsaved = cat :: catalogsaved
    
    LibraryApp.loopTestable(catalog, inputFn, outputFn, saveFn, "test.json")
    
    val output = outputBuffer.toString
    assert(output.contains("Programming")) // search result
    assert(output.contains("No book found")) // search not found
    assert(output.contains("borrowed successfully") || output.contains("Error"))
    assert(output.contains("returned successfully") || output.contains("Error"))
    assert(output.contains("Invalid option")) // invalid options
    assert(output.contains("Thank you for using our system"))
    assert(catalogsaved.nonEmpty) // save was called
  }

  test("handleChoice - edge cases and error conditions") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("978-0134685991", "Scala", List("Martin"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), List(user), Nil)
    
    // Test case 2 with various invalid user IDs
    val invalidUserIds = List("", "a", "user@domain", "user123@", "@@invalid", "toolongusername123456")
    for (invalidUserId <- invalidUserIds) {
      val (result, _) = LibraryApp.handleChoice(2, catalog, invalidUserId, "978-0134685991")
      assert(result.contains("Invalid user ID format"))
    }
    
    // Test case 2 with book that has invalid ISBN
    val invalidBook = Book("invalid", "Bad Book", List("Author"), 2021, "Fiction", true)
    val catalogWithInvalidBook = LibraryCatalog(List(invalidBook), List(user), Nil)
    val (result, _) = LibraryApp.handleChoice(2, catalogWithInvalidBook, "u1", "invalid")
    assert(result.contains("Error"))
    
    // Test case 4 with user that exists but has no loan history
    val (recResult, _) = LibraryApp.handleChoice(4, catalog, "u1", "")
    assert(recResult.contains("No recommendations available") || recResult.contains("- "))
  }

  test("simulateSession - stress test with many operations") {
    val users = List(
      Student("s1", "Student1", "Bachelor"),
      Student("s2", "Student2", "Master"),
      Faculty("f1", "Prof1", "CS"),
      Librarian("l1", "Librarian1", "Head Position")
    )
    val books = List(
      Book("978-0134685991", "Book1", List("Author1"), 2021, "Tech", true),
      Book("978-0134685992", "Book2", List("Author2"), 2022, "Math", true),
      Book("978-0134685993", "Book3", List("Author3"), 2023, "Science", true)
    )
    val catalog = LibraryCatalog(books, users, Nil)
    
    val actions = List(
      // Search operations
      (1, "Book1", ""),
      (1, "Book2", ""), 
      (1, "Nonexistent", ""),
      // Borrow operations
      (2, "s1", "978-0134685991"),
      (2, "s2", "978-0134685992"),
      (2, "f1", "978-0134685993"),
      (2, "invalid", "978-0134685991"), // invalid user
      (2, "s1", "nonexistent"),         // nonexistent book
      // Return operations
      (3, "s1", "978-0134685991"),
      (3, "s2", "978-0134685992"),
      (3, "unknown", "978-0134685991"), // unknown user
      (3, "s1", "nonexistent"),         // nonexistent book
      // Recommendations
      (4, "s1", ""),
      (4, "s2", ""),
      (4, "unknown", ""), // unknown user
      // List available
      (5, "", ""),
      // Reserve operations
      (6, "s1", "978-0134685991"),
      (6, "s2", "978-0134685992"),
      (6, "unknown", "978-0134685991"), // unknown user
      (6, "s1", "nonexistent"),         // nonexistent book
      // Show reservations
      (7, "s1", ""),
      (7, "s2", ""),
      (7, "unknown", ""), // unknown user
      // Statistics
      (8, "", ""),
      (9, "", ""),
      // Exit and invalid
      (10, "", ""),
      (99, "", ""),
      (-1, "", ""),
      (0, "", "")
    )
    
    val results = LibraryApp.simulateSession(actions, catalog)
    assert(results.length == actions.length)
    
    // Verify specific results
    assert(results.exists(_._1.contains("Book1"))) // search found
    assert(results.exists(_._1 == "No book found.")) // search not found
    assert(results.exists(_._1.contains("Error"))) // various errors
    assert(results.exists(_._1.contains("User not found"))) // unknown user
    assert(results.exists(_._1.contains("Thank you for using our system"))) // exit
    assert(results.exists(_._1 == "❌ Invalid option.")) // invalid option
  }

  test("handleChoice - case 1 search with special characters and edge cases") {
    val books = List(
      Book("978-0134685991", "Scala: The Complete Guide", List("Martin Odersky"), 2021, "Programming", true),
      Book("978-0134685992", "C++ Programming", List("Bjarne Stroustrup"), 2022, "Programming", true),
      Book("978-0134685993", "Python & Data Science", List("Guido van Rossum"), 2023, "Data", true)
    )
    val catalog = LibraryCatalog(books, Nil, Nil)
    
    // Search with partial matches
    val (result1, _) = LibraryApp.handleChoice(1, catalog, "Scala", "")
    assert(result1.contains("Scala: The Complete Guide"))
    
    // Search with special characters
    val (result2, _) = LibraryApp.handleChoice(1, catalog, "C++", "")
    assert(result2.contains("C++ Programming"))
    
    // Search with case sensitivity
    val (result3, _) = LibraryApp.handleChoice(1, catalog, "scala", "")
    assert(result3.contains("Scala") || result3 == "No book found.")
    
    // Search with empty string
    val (result4, _) = LibraryApp.handleChoice(1, catalog, "", "")
    assert(result4.contains("Scala") || result4.contains("C++") || result4.contains("Python"))
    
    // Search with very long string
    val longSearch = "a" * 1000
    val (result5, _) = LibraryApp.handleChoice(1, catalog, longSearch, "")
    assert(result5 == "No book found.")
  }

  test("handleChoice - comprehensive error handling") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    
    // Test all menu options with empty catalog
    for (choice <- 1 to 10) {
      val (result, updatedCatalog) = LibraryApp.handleChoice(choice, catalog, "testuser", "testbook")
      assert(result.nonEmpty || choice == 5) // choice 5 might return empty string for no books
      assert(updatedCatalog != null)
    }
    
    // Test invalid menu options
    val invalidChoices = List(-100, -1, 0, 11, 99, 1000)
    for (choice <- invalidChoices) {
      val (result, _) = LibraryApp.handleChoice(choice, catalog, "", "")
      assert(result == "❌ Invalid option.")
    }
  }

  test("loopTestable - boundary conditions and stress test") {
    val catalog = LibraryCatalog(Nil, Nil, Nil)
    
    // Test with rapid exit
    val inputs1 = Array("10")
    var inputIdx1 = 0
    def inputFn1(prompt: String): String = {
      val res = if (inputIdx1 < inputs1.length) inputs1(inputIdx1) else "10"
      inputIdx1 += 1
      res
    }
    
    val outputBuffer1 = new StringBuilder
    def outputFn1(msg: String): Unit = outputBuffer1.append(msg + "\n")
    def saveFn1(cat: LibraryCatalog): Unit = {}
    
    LibraryApp.loopTestable(catalog, inputFn1, outputFn1, saveFn1)
    assert(outputBuffer1.toString.contains("Thank you for using our system"))
    
    // Test with many invalid inputs before exit
    val inputs2 = Array("invalid", "abc", "", " ", "-1", "0", "999", "10")
    var inputIdx2 = 0
    def inputFn2(prompt: String): String = {
      val res = if (inputIdx2 < inputs2.length) inputs2(inputIdx2) else "10"
      inputIdx2 += 1
      res
    }
    
    val outputBuffer2 = new StringBuilder
    def outputFn2(msg: String): Unit = outputBuffer2.append(msg + "\n")
    def saveFn2(cat: LibraryCatalog): Unit = {}
    
    LibraryApp.loopTestable(catalog, inputFn2, outputFn2, saveFn2)
    val output2 = outputBuffer2.toString
    assert(output2.contains("Invalid option"))
    assert(output2.contains("Thank you for using our system"))
  }

  test("main method components - integration test") {
    // Test des composants utilisés dans main sans appeler main directement
    val testBook = Book("978-0134685991", "Test", List("Author"), 2021, "Genre", true)
    val testCatalog = LibraryCatalog(List(testBook), Nil, Nil)
    
    // Test synchronizeBookAvailability
    val synced = testCatalog.synchronizeBookAvailability
    assert(synced.books.size == 1)
    
    // Test que le catalogue a le bon nombre de livres
    assert(synced.books.length == 1)
    
    // Simuler l'appel de loop avec sortie immédiate
    var called = false
    def inputFn(prompt: String): String = "10"
    def outputFn(msg: String): Unit = called = true
    def saveFn(cat: LibraryCatalog): Unit = {}
    
    LibraryApp.loop(synced, inputFn, outputFn, saveFn)
    assert(called)
  }
}
