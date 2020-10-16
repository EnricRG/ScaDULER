package model

import app.AppSettings
import javafx.scene.paint
import model.Weeks.Week
import model.blueprint.EventBlueprint
import model.descriptor.EventDescriptor
import service.{ID, Identifiable2}

@SerialVersionUID(1L)
abstract class EventType extends Serializable {
  def toString: String
  def toShortString: String
  def color: paint.Color
}

object TheoryEvent extends EventType {
  override def toString: String = AppSettings.language.getItem("theoryEvent")
  override def toShortString: String = AppSettings.language.getItem("theoryEventShort")
  override def color: paint.Color = paint.Color.SKYBLUE
}

object LaboratoryEvent extends EventType {
  override def toString: String = AppSettings.language.getItem("labEvent")
  override def toShortString: String = AppSettings.language.getItem("labEventShort")
  override def color: paint.Color = paint.Color.LIGHTGREEN
}

object ComputerEvent extends EventType {
  override def toString: String = AppSettings.language.getItem("computerEvent")
  override def toShortString: String = AppSettings.language.getItem("computerEventShort")
  override def color: paint.Color = paint.Color.web("#4986E7")
}

object ProblemsEvent extends EventType {
  override def toString: String = AppSettings.language.getItem("problemEvent")
  override def toShortString: String = AppSettings.language.getItem("problemEventShort")
  override def color: paint.Color = paint.Color.DARKSALMON
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

@deprecated
case class Precedence(event: Event, isStrict: Boolean)

@SerialVersionUID(1L)
class Event(val id: ID) extends Identifiable2
  with EventLikeImpl[Subject, Course, Resource, Event]
  with Serializable {

  private var _startInterval: Int = -1
  private var _week: Option[Week] = None

  def getStartInterval: Int = _startInterval
  def assign(week: Week, interval: Int): Unit = {
    _startInterval = interval
    this._week = Some(week)
  }
  def unassign(): Unit = {
    _startInterval = -1
    _week = None
  }

  def week: Option[Week] = _week
  def week_=(w: Option[Week]): Unit = _week = w
  def week_=(w: Week): Unit = _week = Some(w)

  def isAssigned: Boolean = _startInterval >= 0
  def isUnassigned: Boolean = !isAssigned
  def isAssignable: Boolean = isUnassigned

  def needsResource: Boolean = neededResource.nonEmpty
  def isValid: Boolean = course.nonEmpty && quarter.nonEmpty

  def addIncompatibility(e: Event): Unit = if (e != this) {
    _incompatibilities.add(e)
    if(!e.incompatibilities.contains(this)) e.addIncompatibility(this)
  }

  def removeIncompatibility(e: Event): Unit = {
    _incompatibilities.remove(e)
    if(e.incompatibilities.contains(this)) e.removeIncompatibility(this)
  }
}

object Event{
  def setEventFromDescriptor(event: Event, descriptor: EventDescriptor[Subject, Course, Resource, Event]): Unit = {
    event.name = descriptor.name
    event.shortName = descriptor.shortName
    event.description = descriptor.description
    event.eventType = descriptor.eventType
    event.duration = descriptor.duration
    event.periodicity = descriptor.periodicity

    event.subject = descriptor.subject
    event.course = descriptor.course
    event.quarter = descriptor.quarter
    event.neededResource = descriptor.neededResource

    descriptor.incompatibilities.foreach(event.addIncompatibility)
  }

  def setEventFromBlueprint(e: Event, eb: EventBlueprint): Unit = {
    e.name = eb.name
    e.shortName = eb.shortName
    e.eventType = eb.eventType
    e.duration = eb.duration
    e.periodicity = eb.periodicity
    e.quarter = eb.quarter
  }
}