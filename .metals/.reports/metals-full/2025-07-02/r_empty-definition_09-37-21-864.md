error id: file://<WORKSPACE>/project-root/src/main/scala/services/LibraryCatalog.scala:`<none>`.
file://<WORKSPACE>/project-root/src/main/scala/services/LibraryCatalog.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -models/Book#
	 -Book#
	 -scala/Predef.Book#
offset: 147
uri: file://<WORKSPACE>/project-root/src/main/scala/services/LibraryCatalog.scala
text:
```scala
package services

import models._
import java.time.LocalDateTime
import scala.util.{Either, Left, Right}

case class LibraryCatalog(
  books: List[@@Book],
  users: List[User],
  transactions: List[Transaction]
) {
  def findBooks(predicate: Book => Boolean): List[Book] = books.filter(predicate)

  def findByTitle(title: String): List[Book] = findBooks(_.title.contains(title))

  def findByAuthor(author: String): List[Book] = findBooks(_.authors.exists(_.contains(author)))

  def availableBooks: List[Book] = findBooks(_.available)

  def loanBook(isbn: ISBN, userId: UserID): Either[String, LibraryCatalog] = {
    val maybeBook = books.find(b => b.isbn == isbn && b.available)
    val maybeUser = users.find(_.id == userId)

    (maybeBook, maybeUser) match {
      case (Some(book), Some(user)) =>
        val newBook = book.copy(available = false)
        val newTrans = Loan(newBook, user, LocalDateTime.now())
        Right(copy(
          books = books.map(b => if (b.isbn == isbn) newBook else b),
          transactions = newTrans :: transactions
        ))
      case _ => Left("Book not available or User not found")
    }
  }

  def recommendBooks(userId: UserID): List[Book] = {
    val history = transactions.collect {
      case Loan(book, user, _) if user.id == userId => book.genre
    }
    val preferredGenres = history.groupBy(identity).view.mapValues(_.size).toList.sortBy(-_._2).map(_._1)
    preferredGenres.flatMap(g => books.filter(b => b.genre == g && b.available)).distinct
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.