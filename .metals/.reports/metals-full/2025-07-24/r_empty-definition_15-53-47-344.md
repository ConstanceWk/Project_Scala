error id: file://<WORKSPACE>/project-root/src/test/scala/services/LibraryAppCoverageTest.scala:`<none>`.
file://<WORKSPACE>/project-root/src/test/scala/services/LibraryAppCoverageTest.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -models/returned.
	 -returned.
	 -scala/Predef.returned.
offset: 748
uri: file://<WORKSPACE>/project-root/src/test/scala/services/LibraryAppCoverageTest.scala
text:
```scala
package services

import org.scalatest.funsuite.AnyFunSuite
import models._
import utils.JsonIO
import java.nio.file.{Files, Paths}

class LibraryAppCoverageTest extends AnyFunSuite {
  val book = Book("9780000000001", "Test", List("A"), 2020, "Test", true)
  val user = Student("u1", "User", "Bachelor")
  val catalog = LibraryCatalog(List(book), List(user), Nil)
  val testPath = "test_app_coverage.json"

  test("search for a book by title") {
    val found = catalog.findByTitle("Test")
    assert(found.nonEmpty)
  }

  test("borrow and return a book via API logic") {
    val borrowed = catalog.loanBook("9780000000001", "u1")
    assert(borrowed.isRight)
    val returned = borrowed.toOption.get.returnBook("9780000000001", "u1")
    assert(@@returned.isRight)
  }

  test("save and reload catalog via JsonIO in app context") {
    JsonIO.saveToFile(catalog, testPath)
    val loaded = JsonIO.loadFromFile[LibraryCatalog](testPath)
    assert(loaded.isRight)
    assert(loaded.toOption.get.books.head == book)
    Files.deleteIfExists(Paths.get(testPath))
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.