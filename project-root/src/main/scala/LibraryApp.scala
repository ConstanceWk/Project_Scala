import services.{LibraryCatalog, given}
import models._
import utils.{JsonIO, parseUserId, isValidUserId, given}

import scala.io.StdIn.readLine

object LibraryApp {

  def main(args: Array[String]): Unit = {
    val jsonPath = "Data/Library.json"
    val loadedCatalog = JsonIO.loadFromFile[LibraryCatalog](jsonPath).getOrElse(LibraryCatalog(Nil, Nil, Nil))
    val initialCatalog = loadedCatalog.synchronizeBookAvailability

    println("\n📚 Welcome to the Library Management System 📚")
    println(s"📁 Loaded ${initialCatalog.books.length} books.")

    loop(initialCatalog)
  }

  def loop(
    catalog: LibraryCatalog,
    input: String => String = prompt => readLine(prompt),
    output: String => Unit = println,
    save: LibraryCatalog => Unit = JsonIO.saveToFile(_, "Data/Library.json")
  ): Unit = {
    output(
      """
        |------------------------------
        |1. Search for a book
        |2. Borrow a book
        |3. Return a book
        |4. Show my recommendations
        |5. List available books
        |6. Reserve a book
        |7. Show my reservations
        |8. Top 3 genres
        |9. Top 3 authors
        |10. Exit
        |------------------------------
        |""".stripMargin
    )

    input("Your choice: ").trim.toIntOption match {
      case Some(1) =>
        val title = input("Title to search: ")
        val results = catalog.findByTitle(title)
        if results.nonEmpty then
          results.foreach(b => output(s"📖 ${b.title} - ${b.authors.mkString(", ")} (${b.publicationYear})"))
        else
          output("No book found.")
        loop(catalog, input, output, save)

      case Some(2) =>
        val userIdInput = input("Your user ID: ")
        val userId = parseUserId(userIdInput)
        if !userId.isValidUserId then
          output("❌ Error: Invalid user ID format.")
          loop(catalog, input, output, save)
        else {
          val bookId = input("ID of the book to borrow: ")
          catalog.loanBook(bookId, userId) match {
            case Right(updated) =>
              output("✅ Book borrowed successfully.")
              save(updated)
              loop(updated, input, output, save)
            case Left(error) =>
              output(s"❌ Error: $error")
              loop(catalog, input, output, save)
          }
        }

      case Some(3) =>
        val userId = input("Your user ID: ")
        val bookId = input("ID of the book to return: ")
        catalog.returnBook(bookId, userId) match {
          case Right(updated) =>
            output("✅ Book returned successfully.")
            save(updated)
            loop(updated, input, output, save)
          case Left(error) =>
            output(s"❌ Error: $error")
            loop(catalog, input, output, save)
        }

      case Some(4) =>
        val userId = input("Your user ID: ")
        if !catalog.users.exists(_.id == userId) then
          output("❌ Error: User not found.")
        else {
          val recs = catalog.recommendBooks(userId)
          output("🎯 Recommendations:")
          if recs.isEmpty then output("No recommendations available.")
          else recs.foreach(b => output(s"- ${b.title} (${b.genre})"))
        }
        loop(catalog, input, output, save)

      case Some(5) =>
        catalog.availableBooks.foreach(b => output(s"📗 ${b.title} - ${b.isbn}"))
        loop(catalog, input, output, save)

      case Some(6) =>
        val userId = input("Your user ID: ")
        val bookId = input("ID of the book to reserve: ")
        catalog.reserveBook(bookId, userId) match {
          case Right(updated) =>
            output("✅ Book reserved successfully.")
            save(updated)
            loop(updated, input, output, save)
          case Left(error) =>
            output(s"❌ Error: $error")
            loop(catalog, input, output, save)
        }

      case Some(7) =>
        val userId = input("Your user ID: ")
        val reservations = catalog.reservationsForUser(userId)
        if reservations.isEmpty then output("No reservations found.")
        else reservations.foreach(r => output(s"🔖 ${r.book.title} reserved at ${r.timestamp}"))
        loop(catalog, input, output, save)

      case Some(8) =>
        output("🏆 Top 3 genres:")
        catalog.topGenres().foreach { case (genre, count) => output(s"$genre: $count") }
        loop(catalog, input, output, save)

      case Some(9) =>
        output("🏆 Top 3 authors:")
        catalog.topAuthors().foreach { case (author, count) => output(s"$author: $count") }
        loop(catalog, input, output, save)

      case Some(10) =>
        output("👋 Thank you for using our system. See you soon!")

      case _ =>
        output("❌ Invalid option.")
        loop(catalog, input, output, save)
    }
  }

  // Boucle principale testable (I/O injecté)
  def loopTestable(
    catalog: LibraryCatalog,
    input: String => String,
    output: String => Unit,
    save: LibraryCatalog => Unit,
    jsonPath: String = "Data/Library.json"
  ): Unit = {
    output(
      """
        |------------------------------
        |1. Search for a book
        |2. Borrow a book
        |3. Return a book
        |4. Show my recommendations
        |5. List available books
        |6. Reserve a book
        |7. Show my reservations
        |8. Top 3 genres
        |9. Top 3 authors
        |10. Exit
        |------------------------------
        |""".stripMargin
    )
    input("Your choice: ").trim.toIntOption match {
      case Some(1) =>
        val title = input("Title to search: ")
        val results = catalog.findByTitle(title)
        if results.nonEmpty then results.foreach(b => output(s"📖 ${b.title} - ${b.authors.mkString(", ")} (${b.publicationYear})"))
        else output("No book found.")
        loopTestable(catalog, input, output, save, jsonPath)
      case Some(2) =>
        val userIdInput = input("Your user ID: ")
        val userId = parseUserId(userIdInput)
        if !userId.isValidUserId then
          output("❌ Error: Invalid user ID format.")
          loopTestable(catalog, input, output, save, jsonPath)
        else {
          val bookId = input("ID of the book to borrow: ")
          catalog.loanBook(bookId, userId) match {
            case Right(updated) =>
              output("✅ Book borrowed successfully.")
              save(updated)
              loopTestable(updated, input, output, save, jsonPath)
            case Left(error) =>
              output(s"❌ Error: $error")
              loopTestable(catalog, input, output, save, jsonPath)
          }
        }
      case Some(3) =>
        val userId = input("Your user ID: ")
        val bookId = input("ID of the book to return: ")
        catalog.returnBook(bookId, userId) match {
          case Right(updated) =>
            output("✅ Book returned successfully.")
            save(updated)
            loopTestable(updated, input, output, save, jsonPath)
          case Left(error) =>
            output(s"❌ Error: $error")
            loopTestable(catalog, input, output, save, jsonPath)
        }
      case Some(4) =>
        val userId = input("Your user ID: ")
        if !catalog.users.exists(_.id == userId) then
          output("❌ Error: User not found.")
          loopTestable(catalog, input, output, save, jsonPath)
        else {
          val recs = catalog.recommendBooks(userId)
          output("🎯 Recommendations:")
          if recs.isEmpty then output("No recommendations available.")
          else recs.foreach(b => output(s"- ${b.title} (${b.genre})"))
          loopTestable(catalog, input, output, save, jsonPath)
        }
      case Some(5) =>
        catalog.availableBooks.foreach(b => output(s"📗 ${b.title} - ${b.isbn}"))
        loopTestable(catalog, input, output, save, jsonPath)
      case Some(6) =>
        val userId = input("Your user ID: ")
        val bookId = input("ID of the book to reserve: ")
        catalog.reserveBook(bookId, userId) match {
          case Right(updated) =>
            output("✅ Book reserved successfully.")
            save(updated)
            loopTestable(updated, input, output, save, jsonPath)
          case Left(error) =>
            output(s"❌ Error: $error")
            loopTestable(catalog, input, output, save, jsonPath)
        }
      case Some(7) =>
        val userId = input("Your user ID: ")
        val reservations = catalog.reservationsForUser(userId)
        if reservations.isEmpty then output("No reservations found.")
        else reservations.foreach(r => output(s"🔖 ${r.book.title} reserved at ${r.timestamp}"))
        loopTestable(catalog, input, output, save, jsonPath)
      case Some(8) =>
        output("🏆 Top 3 genres:")
        catalog.topGenres().foreach { case (genre, count) => output(s"$genre: $count") }
        loopTestable(catalog, input, output, save, jsonPath)
      case Some(9) =>
        output("🏆 Top 3 authors:")
        catalog.topAuthors().foreach { case (author, count) => output(s"$author: $count") }
        loopTestable(catalog, input, output, save, jsonPath)
      case Some(10) =>
        output("👋 Thank you for using our system. See you soon!")
      case _ =>
        output("❌ Invalid option.")
        loopTestable(catalog, input, output, save, jsonPath)
    }
  }

  def simulateSession(actions: List[(Int, String, String)], initialCatalog: LibraryCatalog): List[(String, LibraryCatalog)] = {
    actions.foldLeft(List.empty[(String, LibraryCatalog)] -> initialCatalog) {
      case ((results, catalog), (choice, userId, bookId)) =>
        val (output, updated) = handleChoice(choice, catalog, userId, bookId)
        (results :+ (output, updated), updated)
    }._1
  }

  def handleChoice(choice: Int, catalog: LibraryCatalog, userId: String, bookId: String): (String, LibraryCatalog) = {
    choice match {
      case 1 =>
        val title = userId // Reusing userId variable for title in this context
        val results = catalog.findByTitle(title)
        if results.nonEmpty then
          results.map(b => s"📖 ${b.title} - ${b.authors.mkString(", ")} (${b.publicationYear})").mkString("\n") -> catalog
        else
          "No book found." -> catalog

      case 2 =>
        val parsedUserId = parseUserId(userId)
        if !parsedUserId.isValidUserId then
          "❌ Error: Invalid user ID format." -> catalog
        else {
          catalog.loanBook(bookId, parsedUserId) match {
            case Right(updated) =>
              "✅ Book borrowed successfully." -> updated
            case Left(error) =>
              s"❌ Error: $error" -> catalog
          }
        }

      case 3 =>
        catalog.returnBook(bookId, userId) match {
          case Right(updated) =>
            "✅ Book returned successfully." -> updated
          case Left(error) =>
            s"❌ Error: $error" -> catalog
        }

      case 4 =>
        if !catalog.users.exists(_.id == userId) then
          "❌ Error: User not found." -> catalog
        else {
          val recs = catalog.recommendBooks(userId)
          if recs.isEmpty then "No recommendations available." -> catalog
          else recs.map(b => s"- ${b.title} (${b.genre})").mkString("\n") -> catalog
        }

      case 5 =>
        catalog.availableBooks.map(b => s"📗 ${b.title} - ${b.isbn}").mkString("\n") -> catalog

      case 6 =>
        catalog.reserveBook(bookId, userId) match {
          case Right(updated) =>
            "✅ Book reserved successfully." -> updated
          case Left(error) =>
            s"❌ Error: $error" -> catalog
        }

      case 7 =>
        val reservations = catalog.reservationsForUser(userId)
        if reservations.isEmpty then "No reservations found." -> catalog
        else reservations.map(r => s"🔖 ${r.book.title} reserved at ${r.timestamp}").mkString("\n") -> catalog

      case 8 =>
        val topGenres = catalog.topGenres().map { case (genre, count) => s"$genre: $count" }.mkString("\n")
        s"🏆 Top 3 genres:\n$topGenres" -> catalog

      case 9 =>
        val topAuthors = catalog.topAuthors().map { case (author, count) => s"$author: $count" }.mkString("\n")
        s"🏆 Top 3 authors:\n$topAuthors" -> catalog

      case 10 =>
        "👋 Thank you for using our system. See you soon!" -> catalog

      case _ =>
        "❌ Invalid option." -> catalog
    }
  }
}
