package model.descriptor

import model.Weeks.Periodicity
import model.{EventType, Quarter}

import scala.collection.mutable

//TODO bound types
class EventDescriptor[S, C, R, E] {
  var name: String = ""
  var shortName: String = ""
  var description: String = ""
  var eventType: EventType = _
  var duration: Int = _
  var periodicity: Periodicity = _

  var subject: Option[S] = None
  var course: C = _
  var quarter: Quarter = _
  var neededResource: Option[R] = None

  var incompatibilities: mutable.Set[E] = new mutable.HashSet
}