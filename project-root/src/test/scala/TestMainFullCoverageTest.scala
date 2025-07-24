import org.scalatest.funsuite.AnyFunSuite
import scala.util.Try

class TestMainFullCoverageTest extends AnyFunSuite {
  test("main method runs without exception") {
    val result = Try(TestMain.main(Array.empty))
    assert(result.isSuccess)
  }
}
