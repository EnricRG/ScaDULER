package model

import model.Weeks.Week

import scala.collection.mutable

@SerialVersionUID(1L)
//TODO improve this class performance and structures
class EventSchedule(intervalsPerWeek: Int) extends Schedule[mutable.Set[Event]](intervalsPerWeek){

  //private def getEventsAtInterval(interval: Int): Option[mutable.Set[Event]] =
  //  getValueAtInterval(interval)
  private def getEventsAtIntervalOrElse(interval: Int): mutable.Set[Event] =
    getValueAtIntervalOrElse(interval, new mutable.HashSet)

  def getEventsAtIntervalOrElseCreate(interval: Int): mutable.Set[Event] =
    getValueAtIntervalOrElseUpdate(interval, new mutable.HashSet)

  def addEvent(week: Week, interval: Int, event: Event): mutable.Set[Event] = {
    event.assign(week, interval)
    getEventsAtIntervalOrElseCreate(interval) += event
  }

  def removeEvent(interval: Int, event: Event): Unit = {
    val eventsAtInterval = getValueAtInterval(interval)
    if (eventsAtInterval.nonEmpty) eventsAtInterval.get -= event
    event.unassign()
  }

  def getEvents: Iterable[Event] = super.getAllElements.flatten

  def getIncompatibleEvents(e: Event, week: Int, interval: Int): Iterable[Event] = e.incompatibilities.toList intersect getEventsAtIntervalOrElse(interval).toList
}
