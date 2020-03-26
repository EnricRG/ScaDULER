package file.imprt

trait MCFImportError extends ImportError {
    def row: Int
    def field: Int
    def header: String
    def value: String

    def basicMessage: String = "Import error at row " + row + " when reading field " + header + " (column " + field + ")"
    def specificMessage: String
    def message: String = basicMessage + ": " + specificMessage + "."
}

case class EmptyFieldError(row: Int, field: Int, header: String, value: String) extends MCFImportError{

    override def specificMessage: String = "field cannot be empty"
}

case class WrongSemesterNumberError(row: Int, field: Int, header: String, value: String) extends MCFImportError{

    override def specificMessage: String = "Semester can only take integer values between 1 and 2"
}

case class NumberFormatError(row: Int, field: Int, header: String, value: String) extends MCFImportError{

    override def specificMessage: String = "Incorrect number format"
}

case class UnexpectedCharacterError(row: Int, field: Int, header: String, charValue: Char, candidates: String)
    extends MCFImportError{

    def value: String = charValue.toString

    //TODO use a list of characters instead of a string to represent candidates
    override def specificMessage: String =
        "Unexpected character " + charValue + ". Accepted characters are \""+ candidates + "\""
}

case class OutOfRangeEventDurationError(row: Int, field: Int, header: String, value: String) extends MCFImportError{

    override def specificMessage: String = "Value " + value + " is out of the event duration range"
}

case class NumberOutOfRangeError(row: Int, field: Int, header: String, value: String, min: Int) extends MCFImportError{

    override def specificMessage: String = "Value " + value + " is out of range. Minimum value is " + min
}

