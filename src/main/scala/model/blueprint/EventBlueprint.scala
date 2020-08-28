package model.blueprint

import model.EventLikeImpl
import model.descriptor.EventDescriptor

class EventBlueprint
  extends EventLikeImpl[SubjectBlueprint, CourseBlueprint, ResourceBlueprint, EventBlueprint] {

  def addIncompatibility(e: EventBlueprint): Unit = if (e != this) {
    _incompatibilities.add(e)
    if(!e.incompatibilities.contains(this)) e.addIncompatibility(this)
  }

  def removeIncompatibility(e: EventBlueprint): Unit = {
    _incompatibilities.remove(e)
    if(e.incompatibilities.contains(this)) e.removeIncompatibility(this)
  }
}

object EventBlueprint {

  //To reduce verbosity
  type SB = SubjectBlueprint
  type CB = CourseBlueprint
  type RB = ResourceBlueprint
  type EB = EventBlueprint

  def fromDescriptor(ed: EventDescriptor[SB,CB,RB,EB]): EventBlueprint = {
    val eb = new EventBlueprint

    eb.name = ed.name
    eb.shortName = ed.shortName
    eb.description = ed.description
    eb.eventType = ed.eventType
    eb.duration = ed.duration
    eb.periodicity = ed.periodicity

    eb.subject = ed.subject
    eb.course = ed.course
    eb.quarter = ed.quarter
    eb.neededResource = ed.neededResource

    ed.incompatibilities.foreach(eb.addIncompatibility)

    eb
  }
}

