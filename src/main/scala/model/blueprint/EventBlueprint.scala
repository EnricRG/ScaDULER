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
  //Sets only non generic fields
  def fromDescriptor(ed: EventDescriptor[_,_,_,_]): EventBlueprint = {
    val eb = new EventBlueprint

    eb.name = ed.name
    eb.shortName = ed.shortName
    eb.description = ed.description
    eb.eventType = ed.eventType
    eb.duration = ed.duration
    eb.periodicity = ed.periodicity

    //eb.subject = ed.subject
    //eb.course = ed.course
    eb.quarter = ed.quarter
    //eb.neededResource = ed.neededResource

    //ed.incompatibilities.foreach(eb.addIncompatibility)
    eb
  }
}

