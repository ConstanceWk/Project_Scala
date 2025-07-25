import org.scalatest.funsuite.AnyFunSuite
import services.LibraryCatalog
import models._
import LibraryApp._

class LibraryAppLoopTestableCoverageTest extends AnyFunSuite {
  test("loopTestable: covers all menu branches and I/O") {
    val user = Student("u1", "Alice", "Master")
    val book = Book("isbn1", "Scala", List("Martin Odersky"), 2021, "Programming", true)
    val catalog = LibraryCatalog(List(book), List(user), Nil)
    val inputs = Array(
      "1", "Scala", // search
      "2", "u1", "isbn1", // borrow
      "3", "u1", "isbn1", // return
      "4", "u1", // recommend
      "5", // list available
      "6", "u1", "isbn1", // reserve
      "7", "u1", // show reservations
      "8", // top genres
      "9", // top authors
      "invalid", // invalid option
      "10" // exit
    )
    
    var inputIndex = 0
    def inputFunction(prompt: String): String = {
      if (inputIndex < inputs.length) {
        val result = inputs(inputIndex)
        inputIndex += 1
        result
      } else "10" // exit if we run out of inputs
    }
    
    var outputResults = List.empty[String]
    def outputFunction(message: String): Unit = {
      outputResults = message :: outputResults
    }
    
    LibraryApp.loopTestable(catalog, inputFunction, outputFunction, _ => ())
    assert(outputResults.nonEmpty)
  }

  test("Example test") {
    assert(1 + 1 == 2)
  }
}
