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

    println("\n📚 Bienvenue dans le système de gestion de bibliothèque 📚")
    println(s"📁 Chargement de ${initialCatalog.books.length} livres.")

    def loop(catalog: LibraryCatalog): Unit = {
      println("""
        |------------------------------
        |1. Rechercher un livre
        |2. Emprunter un livre
        |3. Retourner un livre
        |4. Afficher mes recommandations
        |5. Lister les livres disponibles
        |6. Quitter
        |------------------------------
        |""".stripMargin)
      
      readLine("Votre choix : ").trim.toIntOption match {
        case Some(1) =>
          val title = readLine("Titre à rechercher : ")
          val results = catalog.findByTitle(title)
          if results.nonEmpty then 
            results.foreach(b => println(s"📖 ${b.title} - ${b.authors.mkString(", ")} (${b.publicationYear})"))
          else 
            println("Aucun livre trouvé.")
          loop(catalog)

        case Some(2) =>
          val userId = readLine("Votre ID utilisateur : ")
          val bookId = readLine("ID du livre à emprunter : ")
          catalog.loanBook(bookId, userId) match {
            case Right(updated) =>
              println("✅ Livre emprunté avec succès.")
              JsonIO.saveToFile(updated, jsonPath)
              loop(updated)
            case Left(error) =>
              println(s"❌ Erreur : $error")
              loop(catalog)
          }

        case Some(3) =>
          val userId = readLine("Votre ID utilisateur : ")
          val bookId = readLine("ID du livre à retourner : ")
          catalog.returnBook(bookId, userId) match {
            case Right(updated) =>
              println("✅ Livre retourné avec succès.")
              JsonIO.saveToFile(updated, jsonPath)
              loop(updated)
            case Left(error) =>
              println(s"❌ Erreur : $error")
              loop(catalog)
          }

        case Some(4) =>
          val userId = readLine("Votre ID utilisateur : ")
          val recs = catalog.recommendBooks(userId)
          println("🎯 Recommandations :")
          recs.foreach(b => println(s"- ${b.title} (${b.genre})"))
          loop(catalog)

        case Some(5) =>
          catalog.availableBooks.foreach(b => println(s"📗 ${b.title} - ${b.isbn}"))
          loop(catalog)

        case Some(6) =>
          println("👋 Merci d'avoir utilisé notre système. À bientôt !")

        case _ =>
          println("❌ Option invalide.")
          loop(catalog)
      }
    }

    loop(initialCatalog)
  }
}