package model

trait CourseLike {
  def name: String
  def name_=(name: String): Unit

  def description: String
  def description_=(description: String): Unit
}

trait CourseLikeImpl extends CourseLike {
  private var _name: String = ""
  private var _description: String = ""

  def name: String = _name
  def name_=(name: String): Unit = _name = name

  def description: String = _description
  def description_=(description: String): Unit = _description = description
}
