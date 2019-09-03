package model

import scala.collection.mutable

@SerialVersionUID(1L)
class EventSchedule(intervalsPerWeek: Int) extends Schedule[mutable.Set[Event]](intervalsPerWeek){


    //def getEventsAtInterval(week: Int = 0, interval: Int): Option[ListBuffer[NewEvent]] = getWeekSchedule(week).getValueAtInterval(interval)
    private def getEventsAtIntervalOrElse(week: Int = 0, interval: Int): mutable.Set[Event] = getValueAtIntervalOrElse(interval, new mutable.HashSet)
    def getEventsAtIntervalOrElseCreate(week: Int = 0, interval: Int): mutable.Set[Event] =
        getValueAtIntervalOrElseUpdate(interval, new mutable.HashSet)
    def addEvent(week: Int = 0, interval: Int, event: Event): mutable.Set[Event] = {
        event.assign(interval)
        val x = getEventsAtIntervalOrElseCreate(week,interval) += event
        println(x)
        x
    }

    def removeEvent(week: Int = 0, interval: Int, event: Event): Unit = if (getEventsAtIntervalOrElse(week, interval).contains(event)) {
        updateInterval(interval, getValueAtIntervalOrElse(interval, new mutable.HashSet) -= event)
        event.unassign()
    }

    def getEvents: Iterable[Event] = super.getAllElements.flatten

    def getIncompatibleEvents(e: Event, week: Int, interval: Int): Iterable[Event] = e.getIncompatibilities.toList intersect getEventsAtIntervalOrElse(week,interval).toList
}
