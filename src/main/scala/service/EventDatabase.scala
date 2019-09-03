package service

import model.Event

class EventDatabase extends Database[Event] {

    class Initializer{

    }

    def this(initializer: EventDatabase#Initializer) = this

    def newEvent: Long = addElement(new Event)

    def getUnassignedEvents: Iterable[Event] = getElements.filter(_.isUnassigned)
}
