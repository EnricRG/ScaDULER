package model

import app.AppSettings
import javafx.scene.paint
import misc.Weeks.{EveryWeek, Week}
import service.Identifiable

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

@SerialVersionUID(1L)
abstract class EventType extends Serializable {
    def toString: String
    def toShortString: String
    def color: paint.Color
}
object TheoryEvent extends EventType {
    override def toString: String = AppSettings.language.getItem("theoryEvent")
    override def toShortString: String = AppSettings.language.getItem("theoryEventShort")
    override def color: paint.Color = paint.Color.web("#CABDBF")
}
object LaboratoryEvent extends EventType {
    override def toString: String = AppSettings.language.getItem("labEvent")
    override def toShortString: String = AppSettings.language.getItem("labEventShort")
    override def color: paint.Color = paint.Color.web("#FBE983")
}
object ComputerEvent extends EventType {
    override def toString: String = AppSettings.language.getItem("computerEvent")
    override def toShortString: String = AppSettings.language.getItem("computerEventShort")
    override def color: paint.Color = paint.Color.web("#4986E7")
}

object ProblemsEvent extends EventType {
    override def toString: String = AppSettings.language.getItem("problemEvent")
    override def toShortString: String = AppSettings.language.getItem("problemEventShort")
    override def color: paint.Color = TheoryEvent.color
}

object SpecialEvent extends EventType {
    override def toString: String = AppSettings.language.getItem("specialEvent")
    override def toShortString: String = AppSettings.language.getItem("specialEventShort")
    override def color: paint.Color = paint.Color.web("#af77c9")
}

object EventTypes extends Serializable {
    val commonEventTypes: List[EventType] = List(TheoryEvent, ProblemsEvent, LaboratoryEvent, ComputerEvent)
    val allEventTypes: List[EventType] = SpecialEvent :: commonEventTypes
}

case class Precedence(event: Event, isStrict: Boolean)

@SerialVersionUID(1L)
class Event extends Identifiable with Serializable {

    private var startInterval: Int = -1
    private var name: String = ""
    private var shortName: String = ""
    private var description: String = ""
    private var neededResource: Option[Resource] = None
    private var eventType: EventType = _
    private var subject: Option[Subject] = None
    private var week: Week = EveryWeek
    private var duration: Int = AppSettings.maxEventDuration
    private val incompatibilities: mutable.Set[Event] = new mutable.HashSet[Event]
    private var precedences: ListBuffer[Precedence] = new ListBuffer

    def getStartInterval: Int = startInterval
    def assign(interval: Int): Unit = startInterval = interval
    def unassign(): Unit = startInterval = -1
    def isAssigned: Boolean = startInterval >= 0
    def isUnassigned: Boolean = !isAssigned

    def getName: String = name
    def setName(n: String): Unit = name = n

    def getShortName: String = shortName
    def setShortName(n: String): Unit = shortName = n

    def getDescription: String = description
    def setDescription(n: String): Unit = description = n

    def getSafeSubject: Subject = subject.orNull
    def getSubject: Option[Subject] = subject
    def setSubject(s: Subject): Unit = subject = Some(s)

    def getEventType: EventType = eventType
    def setEventType(et: EventType): Unit = eventType = et

    def needsResource: Boolean = neededResource match{
        case Some(_) => true
        case _ => false
    }
    def getNeededResource: Resource = neededResource.orNull
    def getSafeNeededResource: Resource = getNeededResource
    def setNeededResource(nr: Resource): Unit = neededResource = Some(nr)

    def getWeek: Week = week
    def setWeek(week: Week): Unit = this.week = week

    def getDuration: Int = duration
    def setDuration(duration: Int): Unit = this.duration = duration

    def getIncompatibilities: mutable.Set[Event] = incompatibilities
    def addIncompatibility(e: Event): Unit = {
        incompatibilities.add(e)
        if(!e.getIncompatibilities.contains(this)) e.addIncompatibility(this)
    }
    def removeIncompatibility(e: Event): Unit = {
        incompatibilities.remove(e)
        if(e.getIncompatibilities.contains(this)) e.removeIncompatibility(this)
    }

    //def getPrecedent

    /*def getResourceName: String = neededResource match{
        case r if r != null => r.name
        case _ => ""
    }*/
}
