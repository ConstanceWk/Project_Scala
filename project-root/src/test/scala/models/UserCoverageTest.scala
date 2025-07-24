package models

import org.scalatest.funsuite.AnyFunSuite

class UserCoverageTest extends AnyFunSuite {
  test("Student, Faculty, Librarian instantiation and equality") {
    val s = Student("id1", "Alice", "Bachelor")
    val f = Faculty("id2", "Bob", "CS")
    val l = Librarian("id3", "Carol", "Head")
    assert(s.id == "id1" && s.name == "Alice" && s.level == "Bachelor")
    assert(f.id == "id2" && f.name == "Bob" && f.department == "CS")
    assert(l.id == "id3" && l.name == "Carol" && l.position == "Head")
    assert(s != f && f != l && l != s)
  }

  test("User equality and copy") {
    val s1 = Student("id1", "Alice", "Bachelor")
    val s2 = s1.copy(name = "Alicia")
    assert(s1 != s2)
    assert(s1.copy() == s1)
    val f1 = Faculty("id2", "Bob", "CS")
    val f2 = f1.copy(department = "Math")
    assert(f1 != f2)
    val l1 = Librarian("id3", "Carol", "Head")
    val l2 = l1.copy(position = "Assistant")
    assert(l1 != l2)
  }

  test("User toString and hashCode") {
    val s = Student("id1", "Alice", "Bachelor")
    assert(s.toString.contains("Student"))
    assert(s.hashCode != 0)
  }
}
