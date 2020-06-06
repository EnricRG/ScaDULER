package model.descriptor

import model.Weeks.{Periodicity, Weekly}
import model.{EventType, Quarter, SpecialEvent}

import scala.collection.mutable

class EventDescriptor[S, C, R, E] {
  var name: String = ""
  var shortName: String = ""
  var description: String = ""
  var eventType: EventType = SpecialEvent
  var duration: Int = -1
  var periodicity: Periodicity = Weekly

  var subject: Option[S] = None
  var course: Option[C] = None
  var quarter: Option[Quarter] = None
  var neededResource: Option[R] = None

  var incompatibilities: mutable.Set[E] = new mutable.HashSet
}