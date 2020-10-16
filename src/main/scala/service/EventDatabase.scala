package service

import model.descriptor.EventDescriptor
import model.{Course, Event, Resource, Subject}

class EventDatabase extends DatabaseImpl[Event] {

  def createEvent: (ID, Event) = {
    val id = reserveNextId()
    val event = new Event(id)
    addElement(id, event)
  }

  def createEventFromDescriptor(ed: EventDescriptor[Subject, Course, Resource, Event]): (ID, Event) = {
    val entry = createEvent
    Event.setEventFromDescriptor(entry._2, ed)
    entry
  }

  def removeEvent(id: ID): Unit = {
    getElement(id).foreach(e => e.incompatibilities.foreach(e.removeIncompatibility))
    removeElement(id)
  }

  def removeEvent(e: Event): Unit = {
    e.incompatibilities.foreach(e.removeIncompatibility)
    if(e.subject.nonEmpty) e.subject.get.events_-=(e)
    removeElement(e)
  }

  def removeEvents(es: TraversableOnce[Event]): Unit = {
    es.foreach(removeEvent)
  }

  def getEvent(id: ID): Option[Event] =
    getElement(id)

  def getEventOrElse(id: ID, alternative: => Event): Event =
    getElementOrElse(id, alternative)

  def unassignedEvents: Iterable[Event] =
    getElements.filter(_.isUnassigned)

  def events: Iterable[Event] =
    getElements
}
