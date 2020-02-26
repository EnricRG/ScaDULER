package file.imprt

case class ImportError(line: Int, column: Int, field: String, value: String, message: String) //TODO extend generic Error with method message()
