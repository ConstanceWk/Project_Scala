package utils

import org.scalatest.funsuite.AnyFunSuite
import models._
import services._
import java.nio.file.{Files, Paths}
import io.circe.generic.auto._

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
    result match {
      case Left(error) => assert(error.nonEmpty)
      case Right(_) => fail("Expected failure but got success")
    }
  }

  test("Cleanup temporary file") {
    Files.deleteIfExists(Paths.get(testPath))
    assert(!Files.exists(Paths.get(testPath)))
  }

  test("saveToFile should throw for invalid path") {
    assertThrows[Exception] {
      JsonIO.saveToFile("test", "/invalid_dir/should_fail.json")
    }
  }

  test("loadFromFile should return Left for malformed JSON") {
    val badPath = "bad.json"
    Files.write(Paths.get(badPath), "not a json".getBytes())
    val result = JsonIO.loadFromFile[LibraryCatalog](badPath)
    assert(result.isLeft)
    Files.deleteIfExists(Paths.get(badPath))
  }

  test("saveToFile and loadFromFile with empty catalog") {
    val path = "empty_catalog.json"
    val empty = LibraryCatalog(Nil, Nil, Nil)
    JsonIO.saveToFile(empty, path)
    val loaded = JsonIO.loadFromFile[LibraryCatalog](path)
    assert(loaded.isRight)
    assert(loaded.toOption.get.books.isEmpty)
    Files.deleteIfExists(Paths.get(path))
  }

  test("loadFromFile returns Left for invalid JSON structure") {
    val badPath = "bad2.json"
    Files.write(Paths.get(badPath), "{}".getBytes())
    val result = JsonIO.loadFromFile[LibraryCatalog](badPath)
    assert(result.isLeft)
    Files.deleteIfExists(Paths.get(badPath))
  }

  // Test pour couvrir Left(ex.getMessage) lors d'une exception à la lecture d'un fichier
  test("loadFromFile should return Left on unreadable file (exception)") {
    val path = "/root/forbidden.json" // chemin normalement non lisible sans droits root
    val result = JsonIO.loadFromFile[Map[String, String]](path)
    assert(result.isLeft)
    result.left.foreach { msg =>
      assert(msg.nonEmpty)
    }
  }
}
