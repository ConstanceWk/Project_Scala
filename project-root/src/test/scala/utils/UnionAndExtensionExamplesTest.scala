package utils

import org.scalatest.funsuite.AnyFunSuite

class UnionAndExtensionExamplesTest extends AnyFunSuite {
  test("parseUserId works for String and Int") {
    assert(parseUserId("abc") == "abc")
    assert(parseUserId(12345) == "12345")
  }
  test("isValidUserId extension method") {
    assert("user123".isValidUserId)
    assert(!"!@#".isValidUserId)
  }
  test("parseUserId handles empty string and negative int") {
    assert(parseUserId(0) == "0")
    assert(parseUserId("") == "")
  }

  test("isValidUserId edge cases") {
    assert(!"".isValidUserId)
    assert(!"a".isValidUserId)
    assert("ab".isValidUserId)
    assert(!("a" * 21).isValidUserId)
  }
}
