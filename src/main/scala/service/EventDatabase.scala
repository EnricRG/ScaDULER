package service

import model.Event

class EventDatabase extends Database[Event] {

    class Initializer{

    }

    def this(initializer: EventDatabase#Initializer) = this

    def createEvent: (ID, Event) = {
        val id = reserveNextId
        val event = new Event(id)
        addElement(id, event)
    }

    //TODO removing an event should remove all its incompatibilities
    def removeEvent(eid: ID): Unit = removeElement(eid)
    def removeEvent(e: Event): Unit = removeElement(e.getID)

    def deleteEvent(eid: ID): Unit = deleteElement(eid)
    def deleteEvent(e: Event): Unit = deleteElement(e.getID)

    def getUnassignedEvents: Iterable[Event] = getElements.filter(_.isUnassigned)
}

class ReadOnlyEventDatabase(eventDatabase: EventDatabase){

}
