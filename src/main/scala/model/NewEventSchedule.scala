package model

import scala.collection.mutable.ListBuffer

class NewEventSchedule(intervalsPerWeek: Int) extends Schedule[ListBuffer[NewEvent]](intervalsPerWeek){


    //def getEventsAtInterval(week: Int = 0, interval: Int): Option[ListBuffer[NewEvent]] = getWeekSchedule(week).getValueAtInterval(interval)
    private def getEventsAtIntervalOrElse(week: Int = 0, interval: Int): ListBuffer[NewEvent] = getValueAtIntervalOrElse(interval, new ListBuffer)
    def getEventsAtIntervalOrElseCreate(week: Int = 0, interval: Int): ListBuffer[NewEvent] =
        getValueAtIntervalOrElseUpdate(interval, new ListBuffer)
    def addEvent(week: Int = 0, interval: Int, event: NewEvent): ListBuffer[NewEvent] = {
        event.assign(interval)
        getEventsAtIntervalOrElseCreate(week,interval) += event
    }

    def removeEvent(week: Int = 0, interval: Int, event: NewEvent) = getEventsAtIntervalOrElse(week, interval).contains(event) match {
        case true => {
            updateInterval(interval, getValueAtIntervalOrElse(interval, new ListBuffer)-=event)
            event.unassign()
            println(this) //TODO remove this
        }
        case _ =>
    }

    def getIncompatibleEvents(e: NewEvent, week: Int, interval: Int): Iterable[NewEvent] = e.getIncompatibilities.toList intersect getEventsAtIntervalOrElse(week,interval)
        //e.getIncompatibilities.filter(getEventsAtIntervalOrElse(week,interval).contains(_))
}
