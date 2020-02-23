package model

import app.AppSettings
import service.{ID, Identifiable}

/** Course quarter
 *  A Quarter holds all relevant information about a course quarter, like the schedule for its events.
 *
 *  @param schedule Event Schedule
 */
@SerialVersionUID(1L)
class Quarter(schedule: EventSchedule = new EventSchedule(AppSettings.timeSlots)) extends Serializable {
    def getSchedule: EventSchedule = schedule
}

//TODO: update to new database model
@SerialVersionUID(1L)
class Course(id: ID) extends Identifiable(id) with Serializable {

    var name: String = ""
    var description: String = ""
    var firstQuarter: Quarter = new Quarter
    var secondQuarter: Quarter = new Quarter

    def getName: String = name
    def setName(n: String): Unit = name = n

    def getDescription: String = description
    def setDescription(d: String): Unit = description = d

    def getFirstQuarterEvents: Iterable[Event] = firstQuarter.getSchedule.getEvents
    def getSecondQuarterEvents: Iterable[Event] = secondQuarter.getSchedule.getEvents
    //WARNING danger, future implementations may allow repeated events appear in this iterable.
    def getAllEvents: Iterable[Event] = getFirstQuarterEvents ++ getSecondQuarterEvents
}

object NoCourse extends Course(-1){
    setName(AppSettings.language.getItem("noCourse"))
    def noCourse: Course = this //Be careful
} //non bd object
