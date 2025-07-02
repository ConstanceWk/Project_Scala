error id: file://<WORKSPACE>/project-root/src/main/scala/models/User.scala:`<none>`.
file://<WORKSPACE>/project-root/src/main/scala/models/User.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 71
uri: file://<WORKSPACE>/project-root/src/main/scala/models/User.scala
text:
```scala
package models

import io.circe.generic.JsonCodec

@JsonCodec sealed tr@@ait User {
  def id: UserID
  def name: String
}
@JsonCodec case class Student(id: UserID, name: String, level: String) extends User
@JsonCodec case class Faculty(id: UserID, name: String, department: String) extends User
@JsonCodec case class Librarian(id: UserID, name: String, position: String) extends User

type UserID = String
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.