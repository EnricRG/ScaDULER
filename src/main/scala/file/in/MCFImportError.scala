package file.in

abstract class MCFImportError(row: Int, field: Int, header: String, value: String) extends ImportError{
    def message: String = "Import error at row " + row + " when reading field " + header + " (column " + field + ")"
}

case class EmptyFieldError(row: Int, field: Int, header: String, value: String)
  extends MCFImportError(row, field, header, value) {

    override def message: String = super.message + ": field cannot be empty."
}

case class WrongSemesterNumberError(row: Int, field: Int, header: String, value: String)
  extends MCFImportError(row, field, header, value) {

    override def message: String = super.message + ": Semester can only take integer values between 1 and 2."
}

case class NumberFormatError(row: Int, field: Int, header: String, value: String)
  extends MCFImportError(row, field, header, value) {

    override def message: String = super.message + ": Incorrect number format."
}

case class UnexpectedCharacterError(row: Int, field: Int, header: String, value: Char, possibleValues: String)
  extends MCFImportError(row, field, header, value.toString) {

    override def message: String =
        super.message + ": Unexpected character " + value + ". Accepted characters are \""+ possibleValues + "\"."
}

case class OutOfRangeEventDurationError(row: Int, field: Int, header: String, value: String)
  extends MCFImportError(row, field, header, value) {

    override def message: String = super.message + ": Value " + value + " is out of the event duration range."
}

case class NumberOutOfRangeError(row: Int, field: Int, header: String, value: String, min: Int)
  extends MCFImportError(row, field, header, value) {

    override def message: String = super.message + ": Value " + value + " is out of range. Minimum value is " + min + "."
}

