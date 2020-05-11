package model

import model.Weeks.{Periodicity, Weekly}

import scala.collection.mutable

trait EventLike {
  @deprecated
  def getName: String

  def neededResource: ResourceLike
}

trait EventLike2[
  S <: SubjectLike2[C,R,E],
  C <: CourseLike,
  R <: ResourceLike,
  E <: EventLike2[S,C,R,E]] {

  def name: String
  def name_=(s: String): Unit

  def shortName: String
  def shortName_=(s: String): Unit

  def description: String
  def description_=(s: String): Unit

  def eventType: EventType
  def eventType_=(et: EventType): Unit

  def duration: Int
  def duration_=(i: Int): Unit

  def periodicity: Periodicity
  def periodicity_=(p: Periodicity): Unit

  def subject: Option[S]
  def subject_=(so: Option[S]): Unit
  def subject_=(s: S): Unit

  def course: Option[C]
  def course_=(oc: Option[C]): Unit
  def course_=(c: C): Unit

  def quarter: Option[Quarter]
  def quarter_=(oq: Option[Quarter]): Unit
  def quarter_=(q: Quarter): Unit

  def neededResource: Option[R]
  def neededResource_=(or: Option[R]): Unit
  def neededResource_=(r: R): Unit
  def needsResource: Boolean

  def isValid: Boolean

  def incompatibilities: Set[E]
  def addIncompatibility(e: E): Unit
  def removeIncompatibility(e: E): Unit
}

trait EventLikeImpl[S <: SubjectLike2[C,R,E],
  C <: CourseLike,
  R <: ResourceLike,
  E <: EventLike2[S,C,R,E]] extends EventLike2[S,C,R,E]{

  private var _name: String = "" //TODO dynamic naming
  private var _shortName: String = "" //TODO dynamic naming
  private var _description: String = ""
  private var _eventType: EventType = SpecialEvent
  private var _duration: Int = -1
  private var _periodicity: Periodicity = Weekly

  private var _subject: Option[S] = None
  private var _course: Option[C] = None
  private var _quarter: Option[Quarter] = None
  private var _neededResource: Option[R] = None

  protected val _incompatibilities: mutable.Set[E] = new mutable.HashSet

  def name: String = _name
  def name_=(s: String): Unit = _name = s

  def shortName: String = _shortName
  def shortName_=(s: String): Unit = _shortName = s

  def description: String = _description
  def description_=(s: String): Unit = _description = s

  def eventType: EventType = _eventType
  def eventType_=(et: EventType): Unit = _eventType = et

  def duration: Int = _duration
  def duration_=(i: Int): Unit = _duration = i

  def periodicity: Periodicity = _periodicity
  def periodicity_=(p: Periodicity): Unit = _periodicity = p

  def subject: Option[S] = _subject
  def subject_=(so: Option[S]): Unit = _subject = so
  def subject_=(s: S): Unit = subject = Some(s)

  def course: Option[C] = _course
  def course_=(oc: Option[C]): Unit = _course = oc
  def course_=(c: C): Unit = course = Some(c)

  def quarter: Option[Quarter] = _quarter
  def quarter_=(oq: Option[Quarter]): Unit = _quarter = oq
  def quarter_=(q: Quarter): Unit = quarter = Some(q)

  def neededResource: Option[R] = _neededResource
  def neededResource_=(ro: Option[R]): Unit = _neededResource = ro
  def neededResource_=(r: R): Unit = neededResource = Some(r)
  def needsResource: Boolean = _neededResource.nonEmpty

  def isValid: Boolean = course.nonEmpty && quarter.nonEmpty

  def incompatibilities: Set[E] = _incompatibilities.toSet
}

class EventDescriptor2[
  S <: SubjectLike2[C,R,EventDescriptor2[S,C,R]],
  C <: CourseLike,
  R <: ResourceLike] extends EventLikeImpl[S,C,R,EventDescriptor2[S,C,R]]{

  def mutableIncompatibilities: mutable.Set[EventDescriptor2[S,C,R]] = _incompatibilities

  def addIncompatibility(e: EventDescriptor2[S,C,R]): Unit = if (e != this) {
    _incompatibilities.add(e)
    if(!e.incompatibilities.contains(this)) e.addIncompatibility(this)
  }

  def removeIncompatibility(e: EventDescriptor2[S,C,R]): Unit = {
    _incompatibilities.remove(e)
    if(e.incompatibilities.contains(this)) e.removeIncompatibility(this)
  }
}
