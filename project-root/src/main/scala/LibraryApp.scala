import services.LibraryCatalog
import services.given_Encoder_LibraryCatalog  
import services.given_Decoder_LibraryCatalog

import models._
import utils.JsonIO

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
        |6. Exit
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
          val userId = readLine("Your user ID: ")
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
          println("👋 Thank you for using our system. See you soon!")

        case _ =>
          println("❌ Invalid option.")
          loop(catalog)
      }
    }

    loop(initialCatalog)
  }
}