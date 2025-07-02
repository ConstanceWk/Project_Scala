package services

import models._
import java.time.LocalDateTime
import scala.util.{Either, Left, Right}

case class LibraryCatalog(
  books: List[Book],
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

  def returnBook(isbn: ISBN, userId: UserID): Either[String, LibraryCatalog] = {
    val maybeBook = books.find(b => b.isbn == isbn && !b.available)
    val maybeUser = users.find(_.id == userId)

    (maybeBook, maybeUser) match {
      case (Some(book), Some(user)) =>
        val updatedBook = book.copy(available = true)
        val returnTrans = Return(updatedBook, user, LocalDateTime.now())
        Right(copy(
          books = books.map(b => if (b.isbn == isbn) updatedBook else b),
          transactions = returnTrans :: transactions
        ))
      case _ => Left("Book not found or User not found")
    }
  }

  def recommendBooks(userId: UserID): List[Book] = {
    val history = transactions.collect {
      case Loan(book, user, _) if user.id == userId => book.genre
    }
    val preferredGenres = history.groupBy(identity).view.mapValues(_.size).toList.sortBy(-_._2).map(_._1)
    preferredGenres.flatMap(g => books.filter(b => b.genre == g && b.available)).distinct
  }

  // Méthode pour synchroniser l'état des livres avec les transactions
  def synchronizeBookAvailability: LibraryCatalog = {
    val borrowedISBNs = transactions.collect {
      case Loan(book, _, _) => book.isbn
    }.toSet
    
    val returnedISBNs = transactions.collect {
      case Return(book, _, _) => book.isbn
    }.toSet
    
    val currentlyBorrowedISBNs = borrowedISBNs -- returnedISBNs
    
    val synchronizedBooks = books.map { book =>
      book.copy(available = !currentlyBorrowedISBNs.contains(book.isbn))
    }
    
    copy(books = synchronizedBooks)
  }
}


