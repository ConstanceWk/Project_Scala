import services.LibraryCatalog
import services.given_Encoder_LibraryCatalog  
import services.given_Decoder_LibraryCatalog

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

    def loop(catalog: LibraryCatalog): Unit = {
      println("""
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
        |""".stripMargin)
      
      readLine("Your choice: ").trim.toIntOption match {
        case Some(1) =>
          val title = readLine("Title to search: ")
          val results = catalog.findByTitle(title)
          if results.nonEmpty then 
            results.foreach(b => println(s"📖 ${b.title} - ${b.authors.mkString(", ")} (${b.publicationYear})"))
          else 
            println("No book found.")
          loop(catalog)

        case Some(2) =>
          val userIdInput = readLine("Your user ID: ")
          val userId = parseUserId(userIdInput)
          if !userId.isValidUserId then
            println("❌ Error: Invalid user ID format.")
            loop(catalog)
          else {
            val bookId = readLine("ID of the book to borrow: ")
            catalog.loanBook(bookId, userId) match {
              case Right(updated) =>
                println("✅ Book borrowed successfully.")
                JsonIO.saveToFile(updated, jsonPath)
                loop(updated)
              case Left(error) =>
                println(s"❌ Error: $error")
                loop(catalog)
            }
          }

        case Some(3) =>
          val userId = readLine("Your user ID: ")
          val bookId = readLine("ID of the book to return: ")
          catalog.returnBook(bookId, userId) match {
            case Right(updated) =>
              println("✅ Book returned successfully.")
              JsonIO.saveToFile(updated, jsonPath)
              loop(updated)
            case Left(error) =>
              println(s"❌ Error: $error")
              loop(catalog)
          }

        case Some(4) =>
          val userId = readLine("Your user ID: ")
          if !catalog.users.exists(_.id == userId) then
            println("❌ Error: User not found.")
            loop(catalog)
          else
            val recs = catalog.recommendBooks(userId)
            println("🎯 Recommendations:")
            if recs.isEmpty then println("No recommendations available.")
            else recs.foreach(b => println(s"- ${b.title} (${b.genre})"))
            loop(catalog)

        case Some(5) =>
          catalog.availableBooks.foreach(b => println(s"📗 ${b.title} - ${b.isbn}"))
          loop(catalog)

        case Some(6) =>
          val userId = readLine("Your user ID: ")
          val bookId = readLine("ID of the book to reserve: ")
          catalog.reserveBook(bookId, userId) match {
            case Right(updated) =>
              println("✅ Book reserved successfully.")
              JsonIO.saveToFile(updated, jsonPath)
              loop(updated)
            case Left(error) =>
              println(s"❌ Error: $error")
              loop(catalog)
          }
        case Some(7) =>
          val userId = readLine("Your user ID: ")
          val reservations = catalog.reservationsForUser(userId)
          if reservations.isEmpty then println("No reservations found.")
          else reservations.foreach(r => println(s"🔖 ${r.book.title} reserved at ${r.timestamp}"))
          loop(catalog)
        case Some(8) =>
          println("🏆 Top 3 genres:")
          catalog.topGenres().foreach { case (genre, count) => println(s"$genre: $count") }
          loop(catalog)
        case Some(9) =>
          println("🏆 Top 3 authors:")
          catalog.topAuthors().foreach { case (author, count) => println(s"$author: $count") }
          loop(catalog)
        case Some(10) =>
          println("👋 Thank you for using our system. See you soon!")

        case _ =>
          println("❌ Invalid option.")
          loop(catalog)
      }
    }

    loop(initialCatalog)
  }
}