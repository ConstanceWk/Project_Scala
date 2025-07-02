error id: file://<WORKSPACE>/project-root/src/test/scala/utils/JsonIOTest.scala:`<none>`.
file://<WORKSPACE>/project-root/src/test/scala/utils/JsonIOTest.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -models/Files.deleteIfExists.
	 -models/Files.deleteIfExists#
	 -models/Files.deleteIfExists().
	 -services/Files.deleteIfExists.
	 -services/Files.deleteIfExists#
	 -services/Files.deleteIfExists().
	 -java/nio/file/Files.deleteIfExists.
	 -java/nio/file/Files.deleteIfExists#
	 -java/nio/file/Files.deleteIfExists().
	 -Files.deleteIfExists.
	 -Files.deleteIfExists#
	 -Files.deleteIfExists().
	 -scala/Predef.Files.deleteIfExists.
	 -scala/Predef.Files.deleteIfExists#
	 -scala/Predef.Files.deleteIfExists().
offset: 867
uri: file://<WORKSPACE>/project-root/src/test/scala/utils/JsonIOTest.scala
text:
```scala
package utils

import org.scalatest.funsuite.AnyFunSuite
import models._
import services._
import java.nio.file.{Files, Paths}

class JsonIOTest extends AnyFunSuite {

  val testPath = "test_library.json"
  val book = Book("999", "Test Book", List("Test Author"), 2020, "Test", true)
  val user = Student("u1", "Test User", "Bachelor")
  val catalog = LibraryCatalog(List(book), List(user), Nil)

  test("Save and load JSON should be consistent") {
    JsonIO.saveToFile(catalog, testPath)
    val loaded = JsonIO.loadFromFile[LibraryCatalog](testPath)

    assert(loaded.isRight)
    assert(loaded.toOption.get.books.head.title == "Test Book")
  }

  test("Load should fail with non-existent file") {
    val result = JsonIO.loadFromFile[LibraryCatalog]("non_existent_file.json")
    assert(result.isLeft)
  }

  override def afterAll(): Unit = {
    Files.deleteIfE@@xists(Paths.get(testPath))
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.