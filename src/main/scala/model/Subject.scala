package model

import javafx.scene.paint
import service.{ID, Identifiable}

@SerialVersionUID(1L)
class Subject(id: ID) extends Identifiable(id) with SubjectLikeImpl[Subject, Course, Resource, Event]{

  @deprecated
  def getEventSummary: String =
    EventTypes.commonEventTypes.zip(
      EventTypes.commonEventTypes.map(
        evType => events.count(_.eventType == evType)
      )
    ).map{ case (evType, n) => evType + ": " + n}.mkString("\n")
}

object Subject{
  def DefaultColor: paint.Color = paint.Color.WHITESMOKE

  def setSubjectFromDescriptor(s: Subject, sd: SubjectDescriptor[Course,_], events: Iterable[Event]): Unit = {
    s.name = sd.name
    s.shortName = sd.shortName
    s.description = sd.description
    s.color = sd.color
    s.course = sd.course
    s.quarter = sd.quarter

    sd.additionalFields.foreach(entry => s.updateAdditionalField(entry._1,entry._2))
    events.foreach(e => s.events_+=(e))
    s.eventTypeIncompatibilities_++=(sd.eventTypeIncompatibilities)
  }
}