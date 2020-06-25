package form

class Field[F](name: String, initialValue: F){

  private var _value: F = initialValue
  private var _edited: Boolean = false

  def fieldName: String = name

  def value: F = _value
  def value_=(nv: F): Unit = {
    _value = nv
    _edited = true
  }

  def edited: Boolean = _edited
  def unedited: Boolean = !edited
}
