package model

import javafx.scene.paint
import misc.EventTypeIncompatibility
import service.{ID, Identifiable}

import scala.collection.mutable

@SerialVersionUID(1L)
class Subject2(id: ID) extends Identifiable(id) with SubjectLikeImpl[Course, Resource, Event2]{

  @deprecated
  def getEventSummary: String =
    EventTypes.commonEventTypes.zip(
      EventTypes.commonEventTypes.map(
        evType => events.count(_.eventType == evType)
      )
    ).map{ case (evType, n) => evType + ": " + n}.mkString("\n")
}

@SerialVersionUID(1L)
class Subject(id: ID) extends Identifiable(id) with SubjectLike with Serializable {

  def DefaultColor: paint.Color = paint.Color.WHITESMOKE

  private var name: String = ""
  private var shortName: String = ""
  private var description: String = ""
  private var course: Course = NoCourse
  private var quarter: Quarter = NoQuarter
  private var color: Color = new Color(DefaultColor)
  private val events: mutable.Map[ID,Event] = new mutable.HashMap
  private val additionalInformation: mutable.Map[String, Any] = new mutable.HashMap
  private val eventTypeIncompatibilities: mutable.Set[EventTypeIncompatibility] = new mutable.HashSet

  def getName: String = name
  def setName(n: String): Unit = name = n

  def getShortName: String = shortName
  def setShortName(sn: String): Unit = shortName = sn

  def getDescription: String = description
  def setDescription(desc: String): Unit = description = desc

  def getColor: Color = color
  def setColor(c: Color): Color = {
    val oldColor = color
    color = c
    oldColor
  }

  def getCourse: Course = course
  def setCourse(c: Course): Course = {
    val oldCourse = course;
    course = c
    oldCourse
  }
  def unsetCourse(): Unit = course = NoCourse

  def getQuarter: Quarter = quarter
  def setQuarter(q: Quarter): Unit = quarter = q

  def getEvents: Iterable[Event] = events.values
  def getEventIDs: Iterable[ID] = events.keys
  def getEventSummary: String =
    EventTypes.commonEventTypes.zip(
      EventTypes.commonEventTypes.map(
        evType => events.count(_._2.getEventType == evType)
      )
    ).map{ case (evType, n) => evType + ": " + n}.mkString("\n")

  //def setEvents(el: Iterable[NewEvent]): Unit = events = new ListBuffer() ++= el
  def addEvent(id: ID, e: Event): Unit = events.getOrElseUpdate(id, e)
  def removeEvent(id: ID): Unit = events.remove(id)

  /*def removeEventFromId(eid: Int): Unit = events.find(_.num == eid) match {
      case Some(e) => events -= e
      case None =>
  }*/

  def getAdditionalField(f: String): Option[Any] = additionalInformation.get(f)
  def setAdditionalField(f: String, v: Any): Unit = additionalInformation.update(f, v)
  def getAdditionalFields: Map[String, Any] = additionalInformation.toMap

  def addEventTypeIncompatibility(eti: EventTypeIncompatibility): Unit = eventTypeIncompatibilities += eti
  def removeEventTypeIncompatibility(eti: EventTypeIncompatibility): Unit = eventTypeIncompatibilities -= eti

  //debug method
  def summary: String = List(name, shortName, description, events, color).mkString("\n")
}