error id: file://<WORKSPACE>/project-root/src/main/scala/utils/JsonIO.scala:`<none>`.
file://<WORKSPACE>/project-root/src/main/scala/utils/JsonIO.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -io/circe/parser/io/circe.
	 -io/circe/syntax/io/circe.
	 -io/circe/generic/auto/io/circe.
	 -io/circe.
	 -scala/Predef.io.circe.
offset: 186
uri: file://<WORKSPACE>/project-root/src/main/scala/utils/JsonIO.scala
text:
```scala
package utils

import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.auto._
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import io.cir@@ce.generic.auto._

object JsonIO {
  def saveToFile[T: io.circe.Encoder](data: T, path: String): Unit = {
    val json = data.asJson.spaces2
    Files.write(Paths.get(path), json.getBytes(StandardCharsets.UTF_8))
  }

  def loadFromFile[T: io.circe.Decoder](path: String): Either[String, T] = {
    val content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8)
    decode[T](content).left.map(_.getMessage)
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.