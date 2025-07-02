error id: file://<WORKSPACE>/project-root/src/main/scala/Main.scala:nonEmpty.
file://<WORKSPACE>/project-root/src/main/scala/Main.scala
empty definition using pc, found symbol in pc: nonEmpty.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -models/results/nonEmpty.
	 -models/results/nonEmpty#
	 -models/results/nonEmpty().
	 -results/nonEmpty.
	 -results/nonEmpty#
	 -results/nonEmpty().
	 -scala/Predef.results.nonEmpty.
	 -scala/Predef.results.nonEmpty#
	 -scala/Predef.results.nonEmpty().
offset: 1013
uri: file://<WORKSPACE>/project-root/src/main/scala/Main.scala
text:
```scala
package cli

import services.LibraryCatalog
import models._
import utils.JsonIO

import scala.io.StdIn.readLine
import java.time.LocalDateTime

object LibraryCLI:
  def main(args: Array[String]): Unit =
    val jsonPath = "library.json"
    val initialCatalog = JsonIO.loadFromFile[LibraryCatalog](jsonPath).getOrElse(LibraryCatalog(Nil, Nil, Nil))

    println("\n📚 Bienvenue dans le système de gestion de bibliothèque 📚")

    def loop(catalog: LibraryCatalog): Unit =
      println("""
------------------------------
1. Rechercher un livre
2. Emprunter un livre
3. Retourner un livre
4. Afficher mes recommandations
5. Lister les livres disponibles
6. Quitter
------------------------------
""".stripMargin)
      val choice = readLine("Votre choix : ")

      choice match
        case "1" =>
          val title = readLine("Titre à rechercher : ")
          val results = catalog.searchByTitle(title)
          if results.non@@Empty then results.foreach(b => println(s"📖 ${b.title} - ${b.authors.mkString(", ")} (${b.year})"))
          else println("Aucun livre trouvé.")
          loop(catalog)

        case "2" =>
          val userId = readLine("Votre ID utilisateur : ")
          val bookId = readLine("ID du livre à emprunter : ")
          catalog.loanBook(bookId, userId) match
            case Right(updated) =>
              println("✅ Livre emprunté avec succès.")
              JsonIO.saveToFile(updated, jsonPath)
              loop(updated)
            case Left(error) =>
              println(s"❌ Erreur : $error")
              loop(catalog)

        case "3" =>
          val userId = readLine("Votre ID utilisateur : ")
          val bookId = readLine("ID du livre à retourner : ")
          catalog.returnBook(bookId, userId) match
            case Right(updated) =>
              println("✅ Livre retourné avec succès.")
              JsonIO.saveToFile(updated, jsonPath)
              loop(updated)
            case Left(error) =>
              println(s"❌ Erreur : $error")
              loop(catalog)

        case "4" =>
          val userId = readLine("Votre ID utilisateur : ")
          val recs = catalog.recommendBooks(userId)
          println("🎯 Recommandations :")
          recs.foreach(b => println(s"- ${b.title} (${b.genre})"))
          loop(catalog)

        case "5" =>
          catalog.availableBooks.foreach(b => println(s"📗 ${b.title} - ${b.isbn}"))
          loop(catalog)

        case "6" =>
          println("👋 Merci d'avoir utilisé notre système. À bientôt !")

        case _ =>
          println("❌ Option invalide.")
          loop(catalog)

    loop(initialCatalog)
```


#### Short summary: 

empty definition using pc, found symbol in pc: nonEmpty.