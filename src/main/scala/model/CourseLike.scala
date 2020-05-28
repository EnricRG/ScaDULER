package model

trait CourseLike {
  def name: String
  def name_=(name: String): Unit

  def description: String
  def description_=(description: String): Unit

  def firstQuarterData: QuarterData
  def secondQuarterData: QuarterData

  def firstQuarterEvents: Iterable[Event]
  def secondQuarterEvents: Iterable[Event]
  //events from both quarters
  def events: Iterable[Event]
}

trait CourseLikeImpl extends CourseLike {
  private var _name: String = ""
  private var _description: String = ""
  private val _firstQuarterData: QuarterData = new QuarterData(FirstQuarter)
  private val _secondQuarterData: QuarterData = new QuarterData(SecondQuarter)


  def name: String = _name
  def name_=(name: String): Unit = _name = name

  def description: String = _description
  def description_=(description: String): Unit = _description = description

  def firstQuarterData: QuarterData = _firstQuarterData

  def secondQuarterData: QuarterData = _secondQuarterData

  def firstQuarterEvents: Iterable[Event] = firstQuarterData.getSchedule.getEvents

  def secondQuarterEvents: Iterable[Event] = secondQuarterData.getSchedule.getEvents

  def events: Iterable[Event] = firstQuarterEvents ++ secondQuarterEvents
}
