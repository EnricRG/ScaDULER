package form

class SimpleField[V](initialValue: V){

  private var _value: V = initialValue

  def value: V = _value
  def value_=(nv: V): Unit = {
    _value = nv
  }

  def edited: Boolean = _value != initialValue
  def unedited: Boolean = !edited
}

class NamedField[V](name: String, initialValue: V) extends SimpleField[V](initialValue){
  def fieldName: String = name
}
