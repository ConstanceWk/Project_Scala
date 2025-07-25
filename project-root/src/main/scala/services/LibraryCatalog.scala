package services

import models._
import java.time.LocalDateTime
import scala.util.{Either, Left, Right}

case class LibraryCatalog(
  books: List[Book],
  users: List[User],
  transactions: List[Transaction]
) {
  private def findBooks(predicate: Book => Boolean): List[Book] = books.filter(predicate)

  def findByTitle(title: String): List[Book] = findBooks(_.title.contains(title))

  def findByAuthor(author: String): List[Book] = findBooks(_.authors.exists(_.contains(author)))

  def availableBooks: List[Book] = findBooks(_.available)

  def loanBook(isbn: ISBN, userId: UserID): Either[String, LibraryCatalog] = {
    val maybeBook = books.find(b => b.isbn == isbn && b.available)
    val maybeUser = users.find(_.id == userId)

    (maybeBook, maybeUser) match {
      case (Some(book), Some(user)) if book.isValidISBN =>
        val newBook = book.copy(available = false)
        val newTrans = Loan(newBook, user, LocalDateTime.now())
        Right(copy(
          books = books.map(b => if (b.isbn == isbn) newBook else b),
          transactions = newTrans :: transactions
        ))
      case (Some(book), _) if !book.isValidISBN =>
        Left("Invalid ISBN format")
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

  def reserveBook(isbn: ISBN, userId: UserID): Either[String, LibraryCatalog] = {
    val maybeBook = books.find(b => b.isbn == isbn && b.available)
    val maybeUser = users.find(_.id == userId)
    (maybeBook, maybeUser) match {
      case (Some(book), Some(user)) =>
        val reservation = Reservation(book, user, LocalDateTime.now())
        Right(copy(transactions = reservation :: transactions))
      case _ => Left("Book not available or User not found")
    }
  }

  def reservationsForUser(userId: UserID): List[Reservation] =
    transactions.collect { case r: Reservation if r.user.id == userId => r }

  def recommendBooks(userId: UserID): List[Book] = {
  val history = transactions.collect {
    case Loan(book, user, _) if user.id == userId => book.genre
  }
  val preferredGenres = history.groupBy(identity).view.mapValues(_.size).toList.sortBy(-_._2).map(_._1).take(1) // Limite aux genres les plus fréquents
  preferredGenres.flatMap(g => books.filter(b => b.genre == g && b.available)).distinct
}

  def topGenres(n: Int = 3): List[(String, Int)] = {
    val genres = transactions.collect { case Loan(book, _, _) => book.genre }
    genres.groupBy(identity).view.mapValues(_.size).toList.sortBy(-_._2).take(n)
  }

  def topAuthors(n: Int = 3): List[(String, Int)] = {
    val authors = transactions.collect { case Loan(book, _, _) => book.authors }.flatten
    authors.groupBy(identity).view.mapValues(_.size).toList.sortBy(-_._2).take(n)
  }

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


