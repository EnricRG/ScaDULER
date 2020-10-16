package model

import app.{AppSettings, MainApp}
import model.blueprint.CourseBlueprint
import model.descriptor.CourseDescriptor
import service.{ID, Identifiable2}

@SerialVersionUID(1L)
trait Quarter extends Serializable{
  def toShortString: String
}

object FirstQuarter extends Quarter{
  override def toString: String = AppSettings.language.getItem("firstQuarter")
  override def toShortString: String = "Q1" //TODO language specific
}
object SecondQuarter extends Quarter{
  override def toString: String = AppSettings.language.getItem("secondQuarter")
  override def toShortString: String = "Q2" //TODO language specific

}

object Quarters{
  def firstQuarter: Quarter = FirstQuarter
  def secondQuarter: Quarter = SecondQuarter
  val quarters: List[Quarter] = List(FirstQuarter,SecondQuarter)
}

/** Course quarter
 *  A Quarter holds all relevant information about a course quarter, like the schedule for its events.
 *
 *  @param schedule Event Schedule
 */
@SerialVersionUID(1L)
class QuarterData(quarter: Quarter = FirstQuarter, schedule: EventSchedule = new EventSchedule(AppSettings.timeSlots)) extends Serializable {
  def getQuarter: Quarter = quarter
  def getSchedule: EventSchedule = schedule
}

@SerialVersionUID(1L)
class Course(val id: ID) extends Identifiable2 with CourseLikeImpl with Serializable {

  private val _firstQuarterData: QuarterData = new QuarterData(FirstQuarter)
  private val _secondQuarterData: QuarterData = new QuarterData(SecondQuarter)

  def firstQuarterData: QuarterData = _firstQuarterData
  def secondQuarterData: QuarterData = _secondQuarterData

  def firstQuarterEvents: Iterable[Event] = firstQuarterData.getSchedule.getEvents
  def secondQuarterEvents: Iterable[Event] = secondQuarterData.getSchedule.getEvents
  //events from both quarters
  def events: Iterable[Event] = firstQuarterEvents ++ secondQuarterEvents
  //FIXME this is a workaround while we wait for the new data model which will relate courses, subjects and events directly
  def subjects: Iterable[Subject] =
    MainApp.getDatabase.subjectDatabase.subjects.filter(_.course.contains(this))
}

object Course{
  def setCourseFromDescriptor(c: Course, cd: CourseDescriptor): Unit = {
    c.name = cd.name
    c.description = cd.description
  }

  def setCourseFromBlueprint(c: Course, cb: CourseBlueprint): Unit = {
    c.name = cb.name
    c.description = cb.description
  }
}
