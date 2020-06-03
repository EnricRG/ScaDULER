package model

import model.Weeks.Week

import scala.collection.mutable

@SerialVersionUID(1L)
class EventSchedule(intervalsPerWeek: Int) extends Schedule[mutable.Set[Event]](intervalsPerWeek){

    //def getEventsAtInterval(week: Int = 0, interval: Int): Option[ListBuffer[NewEvent]] = getWeekSchedule(week).getValueAtInterval(interval)
    private def getEventsAtIntervalOrElse(interval: Int): mutable.Set[Event] = getValueAtIntervalOrElse(interval, new mutable.HashSet)
    def getEventsAtIntervalOrElseCreate(interval: Int): mutable.Set[Event] =
        getValueAtIntervalOrElseUpdate(interval, new mutable.HashSet)
    def addEvent(week: Week, interval: Int, event: Event): mutable.Set[Event] = {
        event.assign(week, interval)
        getEventsAtIntervalOrElseCreate(interval) += event
    }

    //TODO improvable performance
    def removeEvent(interval: Int, event: Event): Unit = if (getEventsAtIntervalOrElse(interval).contains(event)) {
        updateInterval(interval, getValueAtIntervalOrElse(interval, new mutable.HashSet) -= event)
        event.unassign()
    }

    def getEvents: Iterable[Event] = super.getAllElements.flatten

    def getIncompatibleEvents(e: Event, week: Int, interval: Int): Iterable[Event] = e.incompatibilities.toList intersect getEventsAtIntervalOrElse(interval).toList
}
