package models

case class Book(
  isbn: ISBN,
  title: String,
  authors: List[String],
  publicationYear: Int,
  genre: String,
  available: Boolean
) {
  def isValidISBN: Boolean = {
    val clean = isbn.replaceAll("-", "")
    clean.matches("\\d{13}")
  }
}

type ISBN = String