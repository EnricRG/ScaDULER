package model

import app.AppSettings
import service.{ID, Identifiable}

@SerialVersionUID(1L)
trait Quarter extends Serializable

object NoQuarter extends Quarter{
    def noQuarter: Quarter = this
    override def toString: String = AppSettings.language.getItem("noQuarter")
}
object FirstQuarter extends Quarter{
    override def toString: String = AppSettings.language.getItem("firstQuarter")
}
object SecondQuarter extends Quarter{
    override def toString: String = AppSettings.language.getItem("secondQuarter")
}

object Quarters{
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

//TODO: update to new database model
@SerialVersionUID(1L)
class Course(id: ID) extends Identifiable(id) with Serializable {

    var name: String = ""
    var description: String = ""
    var firstQuarter: QuarterData = new QuarterData
    var secondQuarter: QuarterData = new QuarterData

    def getName: String = name
    def setName(n: String): Unit = name = n

    def getDescription: String = description
    def setDescription(d: String): Unit = description = d

    def getFirstQuarterEvents: Iterable[Event] = firstQuarter.getSchedule.getEvents
    def getSecondQuarterEvents: Iterable[Event] = secondQuarter.getSchedule.getEvents
    //WARNING danger, future implementations may allow repeated events appear in this iterable.
    def getAllEvents: Iterable[Event] = getFirstQuarterEvents ++ getSecondQuarterEvents

    override def toString: String = name
}

object NoCourse extends Course(-1){
    setName(AppSettings.language.getItem("noCourse"))
    def noCourse: Course = this //Be careful

    override def toString: String = name
} //non bd object
