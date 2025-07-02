package models


sealed trait User {
  def id: UserID
  def name: String
}
case class Student(id: UserID, name: String, level: String) extends User
case class Faculty(id: UserID, name: String, department: String) extends User
case class Librarian(id: UserID, name: String, position: String) extends User

type UserID = String