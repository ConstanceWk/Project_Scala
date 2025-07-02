error id: file://<WORKSPACE>/project-root/src/main/scala/models/Transaction.scala:java/time/LocalDateTime#
file://<WORKSPACE>/project-root/src/main/scala/models/Transaction.scala
empty definition using pc, found symbol in pc: java/time/LocalDateTime#
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -java/time/LocalDateTime#
	 -LocalDateTime#
	 -scala/Predef.LocalDateTime#
offset: 174
uri: file://<WORKSPACE>/project-root/src/main/scala/models/Transaction.scala
text:
```scala
package models

import java.time.LocalDateTime
import io.circe.generic.JsonCodec

@JsonCodec sealed trait Transaction {
  def book: Book
  def user: User
  def timestamp: Loc@@alDateTime
}
@JsonCodec case class Loan(book: Book, user: User, timestamp: LocalDateTime) extends Transaction
@JsonCodec case class Return(book: Book, user: User, timestamp: LocalDateTime) extends Transaction
@JsonCodec case class Reservation(book: Book, user: User, timestamp: LocalDateTime) extends Transaction
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/time/LocalDateTime#