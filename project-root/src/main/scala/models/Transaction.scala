package models

import java.time.LocalDateTime

sealed trait Transaction {
  def book: Book
  def user: User
  def timestamp: LocalDateTime
}

case class Loan(book: Book, user: User, timestamp: LocalDateTime) extends Transaction
case class Return(book: Book, user: User, timestamp: LocalDateTime) extends Transaction
case class Reservation(book: Book, user: User, timestamp: LocalDateTime) extends Transaction
