file://<WORKSPACE>/project-root/src/main/scala/services/LibraryCatalogCodec.scala
### java.lang.AssertionError: assertion failed: position error, parent span does not contain child span
parent      =  extends Encoder[Book] {
  final def apply(a: Book) = io.circe.Json.obj(_root_.scala.Predef.???)
} # -1,
parent span = <666..3514>,
child       = final def apply(a: Book) = io.circe.Json.obj(_root_.scala.Predef.???) # -1,
child span  = [688..698..3515]

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
uri: file://<WORKSPACE>/project-root/src/main/scala/services/LibraryCatalogCodec.scala
text:
```scala
package services

import io.circe.{Encoder, Decoder, HCursor}
import io.circe.generic.semiauto._
import models._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

given Encoder[LocalDateTime] = Encoder.encodeString.contramap[LocalDateTime](_.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
given Decoder[LocalDateTime] = Decoder.decodeString.emap { str =>
  try Right(LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
  catch case _: Exception => Left(s"Unable to parse LocalDateTime: $str")
}

// --- Encoders/Decoders manuels pour maximiser la couverture Scoverage ---

// Book
implicit val manualBookEncoder: Encoder[Book] = new Encoder[Book] {
  final def apply(a: Book) = io.circe.Json.obj(
def encoderTransaction: Encoder[Transaction] = deriveEncoder[Transaction]
def encoderLibraryCatalog: Encoder[LibraryCatalog] = deriveEncoder[LibraryCatalog]
def decoderLibraryCatalog: Decoder[LibraryCatalog] = deriveDecoder[LibraryCatalog]

given Encoder[Book] = encoderBook
given Decoder[Book] = decoderBook

given Decoder[User] = (c: HCursor) => {
  c.keys.flatMap(_.headOption) match {
    case Some("Student") =>
      val studentCursor = c.downField("Student")
      for {
        id <- studentCursor.downField("id").as[String]
        name <- studentCursor.downField("name").as[String]
        level <- studentCursor.downField("level").as[String]
      } yield Student(id, name, level)
    case Some("Faculty") =>
      val facultyCursor = c.downField("Faculty")
      for {
        id <- facultyCursor.downField("id").as[String]
        name <- facultyCursor.downField("name").as[String]
        department <- facultyCursor.downField("department").as[String]
      } yield Faculty(id, name, department)
    case Some("Librarian") =>
      val librarianCursor = c.downField("Librarian")
      for {
        id <- librarianCursor.downField("id").as[String]
        name <- librarianCursor.downField("name").as[String]
        position <- librarianCursor.downField("position").as[String]
      } yield Librarian(id, name, position)
    case _ =>
      
      for {
        id <- c.downField("id").as[String]
        name <- c.downField("name").as[String]
        level = c.downField("level").as[String].getOrElse("Undergraduate")
      } yield Student(id, name, level)
  }
}

given Encoder[User] = encoderUser

given Decoder[Transaction] = (c: HCursor) => {
  
  c.keys.flatMap(_.headOption) match {
    case Some("Loan") =>
      val loanCursor = c.downField("Loan")
      for {
        book <- loanCursor.downField("book").as[Book]
        user <- loanCursor.downField("user").as[User]
        timestamp <- loanCursor.downField("timestamp").as[LocalDateTime]
      } yield Loan(book, user, timestamp)
    case Some("Return") =>
      val returnCursor = c.downField("Return")
      for {
        book <- returnCursor.downField("book").as[Book]
        user <- returnCursor.downField("user").as[User]
        timestamp <- returnCursor.downField("timestamp").as[LocalDateTime]
      } yield Return(book, user, timestamp)
    case _ =>
      for {
        book <- c.downField("book").as[Book]
        user <- c.downField("user").as[User]
        timestamp <- c.downField("timestamp").as[LocalDateTime]
      } yield Loan(book, user, timestamp)
  }
}

given Encoder[Loan] = encoderLoan
given Encoder[Return] = encoderReturn
given Encoder[Transaction] = encoderTransaction
given Encoder[LibraryCatalog] = encoderLibraryCatalog
given Decoder[LibraryCatalog] = decoderLibraryCatalog

```



#### Error stacktrace:

```
scala.runtime.Scala3RunTime$.assertFailed(Scala3RunTime.scala:8)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:175)
	dotty.tools.dotc.ast.Positioned.check$1$$anonfun$3(Positioned.scala:205)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.immutable.List.foreach(List.scala:334)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:205)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:226)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:200)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:226)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:200)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:226)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:200)
	dotty.tools.dotc.ast.Positioned.check$1$$anonfun$3(Positioned.scala:205)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.immutable.List.foreach(List.scala:334)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:205)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:226)
	dotty.tools.dotc.parsing.Parser.parse$$anonfun$1(ParserPhase.scala:39)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	dotty.tools.dotc.core.Phases$Phase.monitor(Phases.scala:467)
	dotty.tools.dotc.parsing.Parser.parse(ParserPhase.scala:40)
	dotty.tools.dotc.parsing.Parser.$anonfun$2(ParserPhase.scala:52)
	scala.collection.Iterator$$anon$6.hasNext(Iterator.scala:479)
	scala.collection.Iterator$$anon$9.hasNext(Iterator.scala:583)
	scala.collection.immutable.List.prependedAll(List.scala:152)
	scala.collection.immutable.List$.from(List.scala:685)
	scala.collection.immutable.List$.from(List.scala:682)
	scala.collection.IterableOps$WithFilter.map(Iterable.scala:900)
	dotty.tools.dotc.parsing.Parser.runOn(ParserPhase.scala:51)
	dotty.tools.dotc.Run.runPhases$1$$anonfun$1(Run.scala:315)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.ArrayOps$.foreach$extension(ArrayOps.scala:1324)
	dotty.tools.dotc.Run.runPhases$1(Run.scala:308)
	dotty.tools.dotc.Run.compileUnits$$anonfun$1(Run.scala:348)
	dotty.tools.dotc.Run.compileUnits$$anonfun$adapted$1(Run.scala:357)
	dotty.tools.dotc.util.Stats$.maybeMonitored(Stats.scala:69)
	dotty.tools.dotc.Run.compileUnits(Run.scala:357)
	dotty.tools.dotc.Run.compileSources(Run.scala:261)
	dotty.tools.dotc.interactive.InteractiveDriver.run(InteractiveDriver.scala:161)
	dotty.tools.pc.CachingDriver.run(CachingDriver.scala:45)
	dotty.tools.pc.WithCompilationUnit.<init>(WithCompilationUnit.scala:31)
	dotty.tools.pc.SimpleCollector.<init>(PcCollector.scala:351)
	dotty.tools.pc.PcSemanticTokensProvider$Collector$.<init>(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector$lzyINIT1(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:88)
	dotty.tools.pc.ScalaPresentationCompiler.semanticTokens$$anonfun$1(ScalaPresentationCompiler.scala:111)
```
#### Short summary: 

java.lang.AssertionError: assertion failed: position error, parent span does not contain child span
parent      =  extends Encoder[Book] {
  final def apply(a: Book) = io.circe.Json.obj(_root_.scala.Predef.???)
} # -1,
parent span = <666..3514>,
child       = final def apply(a: Book) = io.circe.Json.obj(_root_.scala.Predef.???) # -1,
child span  = [688..698..3515]