package model

import app.AppSettings
import model.blueprint.CourseBlueprint
import service.{ID, Identifiable}

@SerialVersionUID(1L)
trait Quarter extends Serializable

object NoQuarter extends Quarter{
  def noQuarter: Quarter = this //should not be used
  override def toString: String = AppSettings.language.getItem("noQuarter")
}
object FirstQuarter extends Quarter{
  override def toString: String = AppSettings.language.getItem("firstQuarter")
}
object SecondQuarter extends Quarter{
  override def toString: String = AppSettings.language.getItem("secondQuarter")
}

object Quarters{
  def firstQuarter: Quarter = FirstQuarter
  def secondQuarter: Quarter = SecondQuarter
  def noQuarter: Quarter = NoQuarter
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
class Course(id: ID) extends Identifiable(id) with CourseLikeImpl with Serializable {
  override def toString: String = name //TODO remove this and use custom cell factories
}

object Course{
  def setCourseFromBlueprint(c: Course, cb: CourseBlueprint): Unit = {
    c.name = cb.name
    c.description = cb.description
  }
}

object NoCourse extends Course(-1){
  name = AppSettings.language.getItem("noCourse")

  def noCourse: Course = this
} //non bd object
