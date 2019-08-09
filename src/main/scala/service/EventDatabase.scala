package service

import model.NewEvent

class EventDatabase extends Database[NewEvent] {

    class Initializer{

    }

    def this(initializer: EventDatabase#Initializer) = this

    def newEvent: Long = addElement(new NewEvent)
}
