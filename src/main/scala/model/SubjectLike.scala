package model

import misc.EventTypeIncompatibility

import scala.collection.mutable

trait SubjectLike[
  S <: SubjectLike[S,C,R,E],
  C <: CourseLike,
  R <: ResourceLike,
  E <: EventLike[S,C,R,E]] {

  def name: String
  def name_=(s: String): Unit

  def shortName: String
  def shortName_=(s: String): Unit

  def description: String
  def description_=(s: String): Unit

  def color: Option[Color]
  def color_=(oc: Option[Color]): Unit
  def color_=(c: Color): Unit
  def hasColor: Boolean

  def course: Option[C]
  def course_=(oc: Option[C]): Unit
  def course_=(c: C): Unit
  def hasCourse: Boolean

  def quarter: Option[Quarter]
  def quarter_=(oq: Option[Quarter]): Unit
  def quarter_=(q: Quarter): Unit
  def hasQuarter: Boolean

  def events: Iterable[E]
  def events_+=(e: E): Unit
  def events_-=(e: E): Unit
  def events_++=(es: TraversableOnce[E]): Unit
  def events_--=(es: TraversableOnce[E]): Unit

  def getAdditionalField(f: String): Option[String]
  def updateAdditionalField(f: String, v: String): Unit
  def additionalFields: Map[String, String]

  def eventTypeIncompatibilities: Set[EventTypeIncompatibility]
  def eventTypeIncompatibilities_+=(eti: EventTypeIncompatibility): Unit
  def eventTypeIncompatibilities_-=(eti: EventTypeIncompatibility): Unit
}

trait SubjectLikeImpl[
  S <: SubjectLike[S,C,R,E],
  C <: CourseLike,
  R <: ResourceLike,
  E <: EventLike[S,C,R,E]] extends SubjectLike[S,C,R,E] {

  private var _name: String = ""
  private var _shortName: String = ""
  private var _description: String = ""
  private var _course: Option[C] = None
  private var _quarter: Option[Quarter] = None
  private var _color: Option[Color] = None

  private val _events: mutable.Set[E] = new mutable.HashSet
  private val _additionalInformation: mutable.Map[String, String] = new mutable.HashMap
  private val _eventTypeIncompatibilities: mutable.Set[EventTypeIncompatibility] = new mutable.HashSet

  def name: String = _name
  def name_=(s: String): Unit = _name = s

  def shortName: String = _shortName
  def shortName_=(s: String): Unit = _shortName = s

  def description: String = _description
  def description_=(s: String): Unit = _description = s

  def color: Option[Color] = _color
  def color_=(oc: Option[Color]): Unit = _color = oc
  def color_=(c: Color): Unit = color = Some(c)
  def hasColor: Boolean = color.nonEmpty

  def course: Option[C] = _course
  def course_=(oc: Option[C]): Unit = _course = oc
  def course_=(c: C): Unit = course = Some(c)
  def hasCourse: Boolean = course.nonEmpty

  def quarter: Option[Quarter] = _quarter
  def quarter_=(oq: Option[Quarter]): Unit = _quarter = oq
  def quarter_=(q: Quarter): Unit = quarter = Some(q)
  def hasQuarter: Boolean = quarter.nonEmpty

  def events: Iterable[E] = _events
  def events_+=(e: E): Unit = _events.add(e)
  def events_-=(e: E): Unit = _events.remove(e)
  def events_++=(es: TraversableOnce[E]): Unit = _events ++= es
  def events_--=(es: TraversableOnce[E]): Unit = _events --= es

  def getAdditionalField(f: String): Option[String] = _additionalInformation.get(f)
  def updateAdditionalField(f: String, v: Any): Unit = _additionalInformation.update(f, v.toString)
  def updateAdditionalField(f: String, v: String): Unit = _additionalInformation.update(f, v)
  def additionalFields: Map[String, String] = _additionalInformation.toMap

  def eventTypeIncompatibilities: Set[EventTypeIncompatibility] = _eventTypeIncompatibilities.toSet
  def eventTypeIncompatibilities_+=(eti: EventTypeIncompatibility): Unit = _eventTypeIncompatibilities += eti
  def eventTypeIncompatibilities_-=(eti: EventTypeIncompatibility): Unit = _eventTypeIncompatibilities -= eti
  def eventTypeIncompatibilities_++=(elems: TraversableOnce[EventTypeIncompatibility]): Unit =
    _eventTypeIncompatibilities ++= elems
}