package model.descriptor

import misc.EventTypeIncompatibility
import model.{Color, Quarter}

import scala.collection.mutable

class SubjectDescriptor[C, E]{
  var name: String = ""
  var shortName: String = ""
  var description: String = ""
  var course: Option[C] = None
  var quarter: Option[Quarter] = None
  var color: Option[Color] = None

  val events: mutable.Set[E] = new mutable.HashSet
  val additionalFields: mutable.Map[String, Any] = new mutable.HashMap
  val eventTypeIncompatibilities: mutable.Set[EventTypeIncompatibility] = new mutable.HashSet
}
