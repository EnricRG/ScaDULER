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
    def firstQuarter: Quarter = FirstQuarter
    def secondQuarter: Quarter  = SecondQuarter
    def noQuarter: Quarter  = NoQuarter
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
class Course(id: ID) extends Identifiable(id) with Serializable {

    private var name: String = ""
    private var description: String = ""
    private var firstQuarterData: QuarterData = new QuarterData(FirstQuarter)
    private var secondQuarterData: QuarterData = new QuarterData(SecondQuarter)

    def getFirstQuarterData: QuarterData = firstQuarterData
    def getSecondQuarterData: QuarterData = secondQuarterData

    def getName: String = name
    def setName(n: String): Unit = name = n

    def getDescription: String = description
    def setDescription(d: String): Unit = description = d

    def getFirstQuarterEvents: Iterable[Event] = firstQuarterData.getSchedule.getEvents
    def getSecondQuarterEvents: Iterable[Event] = secondQuarterData.getSchedule.getEvents
    //WARNING danger, future implementations may allow repeated events appear in this iterable.
    def getAllEvents: Iterable[Event] = getFirstQuarterEvents ++ getSecondQuarterEvents

    override def toString: String = name
}

object NoCourse extends Course(-1){
    setName(AppSettings.language.getItem("noCourse"))

    def noCourse: Course = this
    override def toString: String = getName
} //non bd object
