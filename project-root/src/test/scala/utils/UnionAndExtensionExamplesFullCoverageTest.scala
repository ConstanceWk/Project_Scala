package utils

import org.scalatest.funsuite.AnyFunSuite
import utils.{parseUserId, isValidUserId}

class UnionAndExtensionExamplesFullCoverageTest extends AnyFunSuite {
  test("parseUserId handles empty string") {
    assert(parseUserId("") == "")
  }
  test("parseUserId handles negative int") {
    assert(parseUserId(-42) == "-42")
  }
  test("parseUserId handles normal string") {
    assert(parseUserId("user42") == "user42")
  }
  test("parseUserId handles int zero") {
    assert(parseUserId(0) == "0")
  }
  test("parseUserId handles int positive") {
    assert(parseUserId(123) == "123")
  }
  test("parseUserId trims string with spaces") {
    assert(parseUserId("  user  ") == "user")
  }
  test("isValidUserId false for too short") {
    assert(!"a".isValidUserId)
  }
  test("isValidUserId false for too long") {
    val longId = "a" * 21
    assert(!longId.isValidUserId)
  }
  test("isValidUserId true for valid id") {
    assert("abc123".isValidUserId)
  }
  test("isValidUserId false for special chars") {
    assert(!"abc$%".isValidUserId)
  }
}
