package services
import org.scalatest.funsuite.AnyFunSuite

object TestScala3SyntaxModels {
  case class Dummy(a: Int)
}

class TestScala3Syntax extends AnyFunSuite {
  import TestScala3SyntaxModels._
  test("dummy case class instantiation") {
    val d = Dummy(42)
    assert(d.a == 42)
  }
}
