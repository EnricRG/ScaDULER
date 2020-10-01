package model.blueprint

import model.SubjectLikeImpl
import model.descriptor.SubjectDescriptor
/*
class SubjectBlueprint{
  var name: String = ""
  var shortName: String = ""
  var description: String = ""
  var course: CourseBlueprint = _
  var quarter: Quarter = _
  val events: mutable.Set[EventBlueprint] = new mutable.HashSet
  val additionalInformation: mutable.Map[String, Any] = new mutable.HashMap
}*/

class SubjectBlueprint
  extends SubjectLikeImpl[SubjectBlueprint, CourseBlueprint, ResourceBlueprint, EventBlueprint] { }

object SubjectBlueprint{
  
  def fromDescriptorWithoutEvents(sd: SubjectDescriptor[CourseBlueprint, _]): SubjectBlueprint = {
    val sb = new SubjectBlueprint
    sb.name = sd.name
    sb.shortName = sd.shortName
    sb.description = sd.description
    sb.course = sd.course
    sb.quarter = sd.quarter
    sb.color = sd.color
    //sb.events_++=(sd.events)
    sd.additionalFields.foreach(entry => sb.updateAdditionalField(entry._1,entry._2))
    sb.eventTypeIncompatibilities_++=(sd.eventTypeIncompatibilities)
    sb
  }
}
