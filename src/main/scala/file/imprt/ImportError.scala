package file.imprt

abstract case class ImportError(line: Int, column: Int, field: String, value: String) extends misc.Error
