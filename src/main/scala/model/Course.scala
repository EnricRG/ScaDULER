package model

import app.AppSettings
import service.Identifiable

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
//TODO: Add course assigned event list
@SerialVersionUID(1L)
case class Course(val name: String, var descriptionOption: Option[String] = None,
                  var firstQuarter: Quarter, var secondQuarter: Quarter) extends Identifiable with Serializable {

    def description: String = descriptionOption match{
        case Some(description) => description
        case None => AppSettings.language.getItem("course_emptyDescription")
    }

    def getFirstQuarterEvents: Iterable[Event] = firstQuarter.getSchedule.getEvents
    def getSecondQuarterEvents: Iterable[Event] = secondQuarter.getSchedule.getEvents
    //WARNING danger, future implementations may allow repeated events appear in this iterable.
    def getAllEvents: Iterable[Event] = getFirstQuarterEvents ++ getSecondQuarterEvents
}
