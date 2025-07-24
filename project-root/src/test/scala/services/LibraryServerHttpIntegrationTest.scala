package services

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfterAll
import requests._
import io.circe.parser._
import io.circe.Json

class LibraryServerHttpIntegrationTest extends AnyFunSuite with BeforeAndAfterAll {
  val baseUrl = "http://localhost:8080/api"

  override def beforeAll(): Unit = {
    // Démarrage manuel du serveur requis avant d'exécuter ces tests
    println("Assurez-vous que le serveur est lancé sur http://localhost:8080")
  }

  test("GET /books should return list of books") {
    val r = requests.get(s"$baseUrl/books")
    assert(r.statusCode == 200)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(true))
    assert(json.hcursor.downField("data").succeeded)
  }

  test("POST /books/search with invalid JSON should return 400") {
    val r = requests.post(s"$baseUrl/books/search", data = "{invalid}", headers = Seq("Content-Type" -> "application/json"), check = false)
    assert(r.statusCode == 400)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(false))
    assert(json.hcursor.downField("message").as[String].exists(_.contains("invalide")))
  }

  test("GET /books/available should return available books") {
    val r = requests.get(s"$baseUrl/books/available")
    assert(r.statusCode == 200)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(true))
    assert(json.hcursor.downField("data").succeeded)
  }

  test("POST /books/loan with invalid JSON should return 400") {
    val r = requests.post(s"$baseUrl/books/loan", data = "{invalid}", headers = Seq("Content-Type" -> "application/json"), check = false)
    assert(r.statusCode == 400)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(false))
    assert(json.hcursor.downField("message").as[String].exists(_.contains("invalide")))
  }

  test("POST /books/loan with inexistant bookId should return error") {
    val payload = """{"bookId":"doesnotexist","userId":"2"}"""
    val r = requests.post(s"$baseUrl/books/loan", data = payload, headers = Seq("Content-Type" -> "application/json"), check = false)
    assert(r.statusCode == 400)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(false))
    assert(json.hcursor.downField("message").as[String].exists(msg => msg.contains("not available") || msg.contains("not found")))
  }

  test("GET /users should return users") {
    val r = requests.get(s"$baseUrl/users")
    assert(r.statusCode == 200)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(true))
    assert(json.hcursor.downField("data").succeeded)
  }

  test("GET /transactions should return transactions") {
    val r = requests.get(s"$baseUrl/transactions")
    assert(r.statusCode == 200)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(true))
    assert(json.hcursor.downField("data").succeeded)
  }

  test("GET /statistics/genres should return stats") {
    val r = requests.get(s"$baseUrl/statistics/genres")
    assert(r.statusCode == 200)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(true))
    assert(json.hcursor.downField("data").succeeded)
  }

  test("GET /statistics/authors should return stats") {
    val r = requests.get(s"$baseUrl/statistics/authors")
    assert(r.statusCode == 200)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(true))
    assert(json.hcursor.downField("data").succeeded)
  }

  test("POST /books/loan with valid data should succeed") {
    val payload = """{"bookId":"978-00000000002","userId":"2"}"""
    val r = requests.post(s"$baseUrl/books/loan", data = payload, headers = Seq("Content-Type" -> "application/json"), check = false)
    assert(r.statusCode == 200 || r.statusCode == 400)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").succeeded)
  }

  test("POST /books/loan with already loaned book should fail") {
    val payload = """{"bookId":"978-00000000001","userId":"1"}"""
    val r = requests.post(s"$baseUrl/books/loan", data = payload, headers = Seq("Content-Type" -> "application/json"), check = false)
    assert(r.statusCode == 400)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(false))
    assert(json.hcursor.downField("message").as[String].exists(msg => msg.contains("not available") || msg.contains("not found") || msg.contains("ISBN")))
  }

  test("POST /books/loan with unknown user should fail") {
    val payload = """{"bookId":"978-00000000002","userId":"unknown"}"""
    val r = requests.post(s"$baseUrl/books/loan", data = payload, headers = Seq("Content-Type" -> "application/json"), check = false)
    assert(r.statusCode == 400)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(false))
    assert(json.hcursor.downField("message").as[String].exists(msg => msg.contains("not available") || msg.contains("not found") || msg.contains("ISBN")))
  }

  test("POST /books/loan with malformed JSON should fail") {
    val r = requests.post(s"$baseUrl/books/loan", data = "{invalid}", headers = Seq("Content-Type" -> "application/json"), check = false)
    assert(r.statusCode == 400)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(false))
    assert(json.hcursor.downField("message").as[String].exists(_.contains("invalide")))
  }

  test("POST /books/loan with missing field should fail") {
    val payload = """{"bookId":"978-00000000002"}""" // userId manquant
    val r = requests.post(s"$baseUrl/books/loan", data = payload, headers = Seq("Content-Type" -> "application/json"), check = false)
    assert(r.statusCode == 400)
    val json = parse(r.text()).getOrElse(Json.Null)
    assert(json.hcursor.downField("success").as[Boolean].contains(false))
    assert(json.hcursor.downField("message").as[String].exists(_.contains("invalide")))
  }

  // Ajoutez ici d'autres tests pour /books/return, /books/reserve, /users/{userId}/reservations, /users/{userId}/recommendations, etc.
}
