package utils

// Example of a union type: can be either String or Int

def parseUserId(input: String | Int): String = input match {
  case s: String => s.trim
  case i: Int => i.toString
}

// Example of an extension method for String

extension (s: String)
  def isValidUserId: Boolean =
    s.matches("[a-zA-Z0-9]{2,20}")
