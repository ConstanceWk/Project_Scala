import services.LibraryCatalog
import models._

object TestMain {
  def main(args: Array[String]): Unit = {
    println("Test simple de compilation")
    val catalog = LibraryCatalog(List.empty, List.empty, List.empty)
    println(s"Catalogue créé avec ${catalog.books.length} livres")
  }
}
