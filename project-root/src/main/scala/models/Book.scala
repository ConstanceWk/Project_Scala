package models

case class Book(
  isbn: ISBN,
  title: String,
  authors: List[String],
  publicationYear: Int,
  genre: String,
  available: Boolean
)

type ISBN = String