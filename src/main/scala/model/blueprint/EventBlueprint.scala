package model.blueprint

import model.EventLikeImpl

class EventBlueprint extends EventLikeImpl[
  SubjectBlueprint,
  CourseBlueprint,
  ResourceBlueprint,
  EventBlueprint]{

  def addIncompatibility(e: EventBlueprint): Unit = if (e != this) {
    _incompatibilities.add(e)
    if(!e.incompatibilities.contains(this)) e.addIncompatibility(this)
  }

  def removeIncompatibility(e: EventBlueprint): Unit = {
    _incompatibilities.remove(e)
    if(e.incompatibilities.contains(this)) e.removeIncompatibility(this)
  }
}

