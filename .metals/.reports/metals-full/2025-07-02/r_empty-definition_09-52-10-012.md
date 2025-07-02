error id: file://<WORKSPACE>/project-root/src/main/scala/Main.scala:`<none>`.
file://<WORKSPACE>/project-root/src/main/scala/Main.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -models/io/circe.
	 -services/io/circe.
	 -utils/io/circe.
	 -io/circe/generic/auto/io/circe.
	 -io/circe.
	 -scala/Predef.io.circe.
offset: 63
uri: file://<WORKSPACE>/project-root/src/main/scala/Main.scala
text:
```scala
import models._
import services._
import utils._
import io.circ@@e.generic.auto._


@main def runLibrary(): Unit = {
  val book1 = Book("123", "Scala for the Brave", List("Martin Odersky"), 2022, "Programming", true)
  val user1 = Student("s1", "Alice", "Bachelor")

  val catalog = LibraryCatalog(List(book1), List(user1), Nil)

  val result = catalog.loanBook("123", "s1")

  result match {
    case Right(updatedCatalog) =>
      println("Book loaned successfully.")
      JsonIO.saveToFile(updatedCatalog, "library.json")
    case Left(error) => println(s"Error: $error")
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.