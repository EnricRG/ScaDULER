package model

import scala.collection.mutable

@SerialVersionUID(1L)
class EventSchedule(intervalsPerWeek: Int) extends Schedule[mutable.Set[NewEvent]](intervalsPerWeek){


    //def getEventsAtInterval(week: Int = 0, interval: Int): Option[ListBuffer[NewEvent]] = getWeekSchedule(week).getValueAtInterval(interval)
    private def getEventsAtIntervalOrElse(week: Int = 0, interval: Int): mutable.Set[NewEvent] = getValueAtIntervalOrElse(interval, new mutable.HashSet)
    def getEventsAtIntervalOrElseCreate(week: Int = 0, interval: Int): mutable.Set[NewEvent] =
        getValueAtIntervalOrElseUpdate(interval, new mutable.HashSet)
    def addEvent(week: Int = 0, interval: Int, event: NewEvent): mutable.Set[NewEvent] = {
        event.assign(interval)
        val x = getEventsAtIntervalOrElseCreate(week,interval) += event
        println(x)
        x
    }

    def removeEvent(week: Int = 0, interval: Int, event: NewEvent): Unit = getEventsAtIntervalOrElse(week, interval).contains(event) match {
        case true => {
            updateInterval(interval, getValueAtIntervalOrElse(interval, new mutable.HashSet) -= event)
            event.unassign()
        }
        case _ =>
    }

    def getEvents: Iterable[NewEvent] = super.getAllElements.flatten

    def getIncompatibleEvents(e: NewEvent, week: Int, interval: Int): Iterable[NewEvent] = e.getIncompatibilities.toList intersect getEventsAtIntervalOrElse(week,interval).toList
        //e.getIncompatibilities.filter(getEventsAtIntervalOrElse(week,interval).contains(_))
}
