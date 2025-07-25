import org.scalatest.funsuite.AnyFunSuite
import services.LibraryCatalog
import models._
import java.time.LocalDateTime

class LibraryAppMaximumCoverageTest extends AnyFunSuite {

  // Tests ultra-spécifiques pour atteindre 80%+ de coverage

  test("loop method - exact branch coverage for all cases") {
    val user = Student("user1", "TestUser", "PhD")
    val book1 = Book("978-0134685991", "Available Book", List("Author1"), 2021, "Fiction", true)
    val book2 = Book("978-0134685992", "Borrowed Book", List("Author2"), 2022, "Science", false)
    val existingLoan = Loan(book2, user, LocalDateTime.now())
    val existingReservation = Reservation(book1, user, LocalDateTime.now())
    val catalog = LibraryCatalog(
      List(book1, book2), 
      List(user), 
      List(existingLoan, existingReservation)
    )
    
    val testInputs = Array(
      // Test search found and not found
      "1", "Available",
      "1", "NotFound",
      // Test borrow with invalid user and valid user
      "2", "invalid@user", "978-0134685991",
      "2", "user1", "978-0134685991",
      // Test return
      "3", "user1", "978-0134685992",
      // Test recommendations user not found and found
      "4", "nonexistent", 
      "4", "user1",
      // Test list available
      "5",
      // Test reserve
      "6", "user1", "978-0134685991",
      // Test show reservations
      "7", "user1",
      // Test statistics
      "8",
      "9",
      // Test invalid inputs
      "abc", "xyz", " ", "",
      // Test invalid numbers
      "-10", "0", "11", "999",
      // Exit
      "10"
    )
    
    var inputIndex = 0
    def mockInput(prompt: String): String = {
      val result = if (inputIndex < testInputs.length) testInputs(inputIndex) else "10"
      inputIndex += 1
      result
    }
    
    val outputs = new StringBuilder
    def mockOutput(message: String): Unit = {
      outputs.append(message).append("\n")
    }
    
    var saveCount = 0
    def mockSave(cat: LibraryCatalog): Unit = {
      saveCount += 1
    }
    
    LibraryApp.loop(catalog, mockInput, mockOutput, mockSave)
    
    val finalOutput = outputs.toString
    assert(finalOutput.contains("Available Book"))
    assert(finalOutput.contains("No book found"))
    assert(finalOutput.contains("Invalid user ID format"))
    assert(finalOutput.contains("Invalid option"))
    assert(finalOutput.contains("Thank you for using our system"))
    assert(saveCount > 0)
  }

  test("loopTestable method - complete branch coverage") {
    val student = Student("st1", "Student", "Master")
    val faculty = Faculty("fc1", "Faculty", "Engineering") 
    val librarian = Librarian("lib1", "Librarian", "Head Position")
    
    val book1 = Book("978-0134685991", "Book One", List("Author A"), 2020, "Tech", true)
    val book2 = Book("978-0134685992", "Book Two", List("Author B"), 2021, "Math", false)
    val book3 = Book("978-0134685993", "Book Three", List("Author C"), 2022, "Science", true)
    
    val loan1 = Loan(book2, student, LocalDateTime.now())
    val reservation1 = Reservation(book3, faculty, LocalDateTime.now())
    
    val catalog = LibraryCatalog(
      List(book1, book2, book3),
      List(student, faculty, librarian),
      List(loan1, reservation1)
    )
    
    val comprehensiveInputs = Array(
      // Search scenarios
      "1", "Book One",      // found
      "1", "Nonexistent",   // not found
      "1", "",              // empty search
      "1", "Book",          // partial match
      
      // Borrow scenarios  
      "2", "st1", "978-0134685991",     // success
      "2", "invalid@@", "978-0134685991", // invalid user
      "2", "st1", "978-0134685992",     // already borrowed
      "2", "st1", "nonexistent",        // book not found
      "2", "nonexistent", "978-0134685991", // user not found
      
      // Return scenarios
      "3", "st1", "978-0134685992",     // success  
      "3", "st1", "978-0134685991",     // not borrowed
      "3", "nonexistent", "978-0134685992", // user not found
      "3", "st1", "nonexistent",        // book not found
      
      // Recommendation scenarios
      "4", "st1",           // with history
      "4", "fc1",           // different user
      "4", "lib1",          // no history
      "4", "nonexistent",   // user not found
      
      // List available
      "5",
      
      // Reserve scenarios
      "6", "st1", "978-0134685993",     // success
      "6", "st1", "978-0134685992",     // unavailable book
      "6", "nonexistent", "978-0134685991", // user not found
      "6", "st1", "nonexistent",        // book not found
      
      // Show reservations
      "7", "fc1",           // has reservations
      "7", "st1",           // no reservations  
      "7", "nonexistent",   // user not found
      
      // Statistics
      "8",                  // top genres
      "9",                  // top authors
      
      // Invalid inputs - comprehensive
      "abc", "def", "xyz",  // non-numeric
      "", " ", "   ",       // empty/whitespace
      "-1", "-10", "-999",  // negative
      "0",                  // zero
      "11", "12", "100", "999", // out of range
      
      // Exit
      "10"
    )
    
    var idx = 0
    def inputProvider(prompt: String): String = {
      val value = if (idx < comprehensiveInputs.length) comprehensiveInputs(idx) else "10"
      idx += 1
      value
    }
    
    val outputCollector = new StringBuilder
    def outputHandler(msg: String): Unit = {
      outputCollector.append(msg).append("\n")
    }
    
    var savedCatalogs = List.empty[LibraryCatalog]
    def saveCollector(cat: LibraryCatalog): Unit = {
      savedCatalogs = cat :: savedCatalogs
    }
    
    LibraryApp.loopTestable(catalog, inputProvider, outputHandler, saveCollector, "test-data.json")
    
    val allOutput = outputCollector.toString
    
    // Verify all branches were hit
    assert(allOutput.contains("Book One"))
    assert(allOutput.contains("No book found"))  
    assert(allOutput.contains("borrowed successfully") || allOutput.contains("Error"))
    assert(allOutput.contains("Invalid user ID format"))
    assert(allOutput.contains("returned successfully") || allOutput.contains("Error"))
    assert(allOutput.contains("User not found"))
    assert(allOutput.contains("Recommendations") || allOutput.contains("No recommendations"))
    assert(allOutput.contains("📗"))
    assert(allOutput.contains("reserved successfully") || allOutput.contains("Error"))
    assert(allOutput.contains("reserved at") || allOutput.contains("No reservations"))
    assert(allOutput.contains("Top 3 genres"))
    assert(allOutput.contains("Top 3 authors"))
    assert(allOutput.contains("Invalid option"))
    assert(allOutput.contains("Thank you for using our system"))
    assert(savedCatalogs.nonEmpty)
  }

  test("simulateSession - maximum scenario coverage") {
    val users = List(
      Student("s1", "Alice", "Bachelor"),
      Student("s2", "Bob", "Master"),  
      Faculty("f1", "Prof Smith", "Computer Science"),
      Faculty("f2", "Prof Jones", "Mathematics"),
      Librarian("l1", "Head Librarian", "Chief Position")
    )
    
    val books = List(
      Book("978-0134685991", "Scala Fundamentals", List("Martin Odersky"), 2020, "Programming", true),
      Book("978-0134685992", "Advanced Mathematics", List("John Doe"), 2021, "Mathematics", true),
      Book("978-0134685993", "Data Structures", List("Jane Smith"), 2022, "Computer Science", false),
      Book("978-0134685994", "Machine Learning", List("AI Expert"), 2023, "AI", true),
      Book("invalid-isbn", "Bad Book", List("Bad Author"), 2024, "Bad", true)
    )
    
    val existingLoan = Loan(books(2), users(0), LocalDateTime.now())
    val existingReservation = Reservation(books(3), users(1), LocalDateTime.now())
    
    val catalog = LibraryCatalog(books, users, List(existingLoan, existingReservation))
    
    val maximalActions = List(
      // Comprehensive search tests
      (1, "Scala", ""),
      (1, "Advanced", ""),
      (1, "Data", ""),
      (1, "NonExistent Book", ""),
      (1, "", ""),
      (1, "scala", ""), // case sensitivity
      
      // Comprehensive borrow tests
      (2, "s1", "978-0134685991"),        // valid borrow
      (2, "s2", "978-0134685992"),        // another valid borrow
      (2, "f1", "978-0134685994"),        // faculty borrow
      (2, "s1", "978-0134685993"),        // try to borrow unavailable
      (2, "nonexistent", "978-0134685991"), // invalid user
      (2, "s1", "nonexistent-book"),      // invalid book
      (2, "invalid@@user", "978-0134685991"), // malformed user ID
      (2, "s1", "invalid-isbn"),          // invalid ISBN
      
      // Comprehensive return tests  
      (3, "s1", "978-0134685993"),        // valid return
      (3, "s2", "978-0134685992"),        // another return
      (3, "f1", "978-0134685994"),        // faculty return
      (3, "nonexistent", "978-0134685991"), // invalid user
      (3, "s1", "nonexistent-book"),      // invalid book
      (3, "s1", "978-0134685991"),        // return non-borrowed book
      
      // Comprehensive recommendation tests
      (4, "s1", ""),                      // user with history
      (4, "s2", ""),                      // another user
      (4, "f1", ""),                      // faculty
      (4, "l1", ""),                      // librarian
      (4, "nonexistent", ""),             // invalid user
      
      // List available books
      (5, "", ""),
      
      // Comprehensive reserve tests
      (6, "s1", "978-0134685991"),        // valid reserve
      (6, "s2", "978-0134685992"),        // another reserve
      (6, "f2", "978-0134685994"),        // faculty reserve
      (6, "s1", "978-0134685993"),        // try to reserve unavailable
      (6, "nonexistent", "978-0134685991"), // invalid user
      (6, "s1", "nonexistent-book"),      // invalid book
      
      // Comprehensive show reservations tests
      (7, "s1", ""),                      // user with reservations
      (7, "s2", ""),                      // user might have reservations
      (7, "f1", ""),                      // faculty reservations
      (7, "l1", ""),                      // librarian reservations
      (7, "nonexistent", ""),             // invalid user
      
      // Statistics tests
      (8, "", ""),                        // top genres
      (9, "", ""),                        // top authors
      
      // Exit and invalid options
      (10, "", ""),                       // exit
      (0, "", ""),                        // invalid option
      (-1, "", ""),                       // negative option
      (11, "", ""),                       // out of range option
      (999, "", ""),                      // very high option
      (-999, "", "")                      // very negative option
    )
    
    val results = LibraryApp.simulateSession(maximalActions, catalog)
    
    assert(results.length == maximalActions.length)
    
    // Verify specific outcomes
    assert(results.exists(_._1.contains("Scala Fundamentals")))
    assert(results.exists(_._1 == "No book found."))
    assert(results.exists(_._1.contains("borrowed successfully")))
    assert(results.exists(_._1.contains("Error")))
    assert(results.exists(_._1.contains("Invalid user ID format")))
    assert(results.exists(_._1.contains("returned successfully")))
    assert(results.exists(_._1.contains("User not found")))
    assert(results.exists(_._1.contains("reserved successfully")))
    assert(results.exists(r => r._1.contains("reserved at") || r._1.contains("No reservations")))
    assert(results.exists(_._1.contains("Top 3 genres")))
    assert(results.exists(_._1.contains("Top 3 authors")))
    assert(results.exists(_._1.contains("Thank you for using our system")))
    assert(results.exists(_._1 == "❌ Invalid option."))
  }

  test("handleChoice - exhaustive input combinations") {
    val complexCatalog = {
      val users = List(
        Student("valid_user", "Valid User", "PhD"),
        Faculty("prof1", "Professor", "Science")
      )
      val books = List(
        Book("978-0134685991", "Valid Book", List("Author"), 2021, "Genre", true),
        Book("978-0134685992", "Borrowed Book", List("Author2"), 2022, "Genre2", false),
        Book("invalid-isbn", "Invalid ISBN Book", List("Author3"), 2023, "Genre3", true)
      )
      val loan = Loan(books(1), users(0), LocalDateTime.now())
      val reservation = Reservation(books(0), users(1), LocalDateTime.now())
      LibraryCatalog(books, users, List(loan, reservation))
    }
    
    // Test every single menu option with various inputs
    val testCases = List(
      // Case 1: Search
      (1, "Valid", "", "should find book"),
      (1, "Borrowed", "", "should find borrowed book"),
      (1, "Nonexistent", "", "should not find book"),
      (1, "", "", "empty search"),
      
      // Case 2: Borrow
      (2, "valid_user", "978-0134685991", "valid borrow"),
      (2, "prof1", "978-0134685991", "faculty borrow"),
      (2, "invalid_user", "978-0134685991", "invalid user borrow"),
      (2, "user@domain", "978-0134685991", "malformed user borrow"),
      (2, "valid_user", "978-0134685992", "unavailable book borrow"),
      (2, "valid_user", "nonexistent", "nonexistent book borrow"),
      (2, "valid_user", "invalid-isbn", "invalid isbn borrow"),
      
      // Case 3: Return
      (3, "valid_user", "978-0134685992", "valid return"),
      (3, "prof1", "978-0134685992", "faculty return"),
      (3, "invalid_user", "978-0134685992", "invalid user return"),
      (3, "valid_user", "978-0134685991", "not borrowed return"),
      (3, "valid_user", "nonexistent", "nonexistent book return"),
      
      // Case 4: Recommendations
      (4, "valid_user", "", "user with history"),
      (4, "prof1", "", "faculty recommendations"),
      (4, "nonexistent_user", "", "invalid user recommendations"),
      
      // Case 5: List available
      (5, "", "", "list available books"),
      
      // Case 6: Reserve
      (6, "valid_user", "978-0134685991", "valid reserve"),
      (6, "prof1", "978-0134685992", "reserve unavailable"),
      (6, "nonexistent_user", "978-0134685991", "invalid user reserve"),
      (6, "valid_user", "nonexistent", "nonexistent book reserve"),
      
      // Case 7: Show reservations
      (7, "valid_user", "", "user reservations"),
      (7, "prof1", "", "faculty reservations"),
      (7, "nonexistent_user", "", "invalid user reservations"),
      
      // Case 8: Top genres
      (8, "", "", "top genres"),
      
      // Case 9: Top authors  
      (9, "", "", "top authors"),
      
      // Case 10: Exit
      (10, "", "", "exit"),
      
      // Invalid cases
      (-10, "", "", "very negative"),
      (-1, "", "", "negative"),
      (0, "", "", "zero"),
      (11, "", "", "eleven"),
      (99, "", "", "ninety-nine"),
      (1000, "", "", "thousand")
    )
    
    for ((choice, userId, bookId, description) <- testCases) {
      val (result, updatedCatalog) = LibraryApp.handleChoice(choice, complexCatalog, userId, bookId)
      
      // All results should be non-empty strings
      assert(result.nonEmpty, s"Result should not be empty for case: $description")
      
      // Updated catalog should never be null
      assert(updatedCatalog != null, s"Updated catalog should not be null for case: $description")
      
      // Verify specific behaviors
      choice match {
        case 1 => // Search should return book info or "No book found"
          assert(result.contains("Valid Book") || result.contains("Borrowed Book") || result == "No book found.")
        case 2 => // Borrow should succeed or show error
          assert(result.contains("borrowed successfully") || result.contains("Error") || result.contains("Invalid user ID format"))
        case 3 => // Return should succeed or show error  
          assert(result.contains("returned successfully") || result.contains("Error"))
        case 4 => // Recommendations should show recs or errors
          assert(result.contains("No recommendations") || result.contains("- ") || result.contains("User not found"))
        case 5 => // List available should show books
          assert(result.contains("📗") || result.isEmpty)
        case 6 => // Reserve should succeed or show error
          assert(result.contains("reserved successfully") || result.contains("Error"))
        case 7 => // Show reservations should show reservations or none
          assert(result.contains("🔖") || result.contains("No reservations") || result.contains("reserved at"))
        case 8 => // Top genres should show genre info
          assert(result.contains("🏆 Top 3 genres"))
        case 9 => // Top authors should show author info
          assert(result.contains("🏆 Top 3 authors"))
        case 10 => // Exit should show goodbye message
          assert(result.contains("Thank you for using our system"))
        case _ => // Invalid options should show error
          assert(result == "❌ Invalid option.")
      }
    }
  }
}
