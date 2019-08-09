package model

import app.AppSettings
import javafx.scene.paint.Color

abstract class EventType{
    def toString: String
    def color: Color
}
object TheoryEvent extends EventType {
    override def toString: String = AppSettings.language.getItem("theoryEvent")
    override def color: Color = Color.web("#CABDBF")
}
object LaboratoryEvent extends EventType {
    override def toString: String = AppSettings.language.getItem("labEvent")
    override def color: Color = Color.web("#FBE983")
}
object ComputerEvent extends EventType {
    override def toString: String = AppSettings.language.getItem("computerEvent")
    override def color: Color = Color.web("#4986E7")
}

object EventTypes{ val eventTypes: List[EventType] = List(TheoryEvent, LaboratoryEvent, ComputerEvent) }

class NewEvent{

    var name: String = ""
    var shortName: String = ""
    var description: String = ""
    var neededResource: Resource = _
    var eventType: EventType = _
    var subject: Option[Subject] = None

    def getName: String = name
    def setName(n: String): Unit = name = n

    def getShortName: String = shortName
    def setShortName(n: String): Unit = shortName = n

    def getSubject: Option[Subject] = subject
    def setSubject(s: Subject): Unit = subject = Some(s)

    def getEventType: EventType = eventType
    def setEventType(et: EventType): Unit = eventType = et

    def getNeededResource: Resource = neededResource
    def setNeededResource(nr: Resource): Unit = neededResource = nr

    /*def getResourceName: String = neededResource match{
        case r if r != null => r.name
        case _ => ""
    }*/
}
