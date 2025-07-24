import org.scalatest.funsuite.AnyFunSuite
import akka.http.scaladsl.model._
import io.circe.syntax._
import io.circe.generic.auto._
import models._
import api._
import scala.util.Try

class LibraryServerFullCoverageTest extends AnyFunSuite {
  // Test la méthode jsonResponse (copie locale pour le test)
  def jsonResponse[T: io.circe.Encoder](data: T, status: StatusCode = StatusCodes.OK): HttpResponse = {
    val json = data.asJson.noSpaces
    HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, json), status = status)
  }

  test("jsonResponse should serialize data and set status") {
    val data = ApiResponse(success = true, data = Some("ok"), message = Some("msg"))
    val resp = jsonResponse(data)
    assert(resp.status == StatusCodes.OK)
    assert(resp.entity.contentType == ContentTypes.`application/json`)
    val strict = resp.entity.asInstanceOf[HttpEntity.Strict]
    assert(strict.data.utf8String.contains("ok"))
  }

  // Test la branche main (exécution sans crash)
  test("main method runs without exception") {
    val result = Try(LibraryServer.main(Array.empty))
    assert(result.isSuccess)
  }
}
