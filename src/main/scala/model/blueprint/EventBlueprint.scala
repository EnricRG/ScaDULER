package model.blueprint

import model.descriptor.EventDescriptor
import model.EventLikeImpl

class EventBlueprint extends EventDescriptor[
  SubjectBlueprint,
  CourseBlueprint,
  ResourceBlueprint,
  EventBlueprint]
/*
class EventBlueprint2 extends EventLikeImpl[
  SubjectBlueprint2,
  CourseBlueprint2,
  ResourceBlueprint2,
  EventBlueprint2]{

  def addIncompatibility(e: EventBlueprint2): Unit = if (e != this) {
    _incompatibilities.add(e)
    if(!e.incompatibilities.contains(this)) e.addIncompatibility(this)
  }

  def removeIncompatibility(e: EventBlueprint2): Unit = {
    _incompatibilities.remove(e)
    if(e.incompatibilities.contains(this)) e.removeIncompatibility(this)
  }
}*/

