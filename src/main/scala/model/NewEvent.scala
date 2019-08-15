package model

import app.AppSettings
import javafx.scene.paint.Color
import misc.Weeks.{AWeek, Week}
import service.Identifiable

abstract class EventType extends Serializable {
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

object EventTypes extends Serializable { val eventTypes: List[EventType] = List(TheoryEvent, LaboratoryEvent, ComputerEvent) }

class NewEvent extends Identifiable with Serializable {

    private var startInterval: Int = -1
    private var name: String = ""
    private var shortName: String = ""
    private var description: String = ""
    private var neededResource: Resource = _
    private var eventType: EventType = _
    private var subject: Option[Subject] = None //FIXME be careful with serialization
    private var week: Week = AWeek

    def getStartInterval: Int = startInterval
    def setStartInterval(interval: Int): Unit = startInterval = interval
    def isAssigned: Boolean = startInterval >= 0
    def isUnassigned: Boolean = !isAssigned

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

    def getWeek: Week = week
    def setWeek(week: Week): Unit = this.week = week

    /*def getResourceName: String = neededResource match{
        case r if r != null => r.name
        case _ => ""
    }*/
}
