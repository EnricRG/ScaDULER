package service

import model.blueprint.SubjectBlueprint
import model.descriptor.SubjectDescriptor
import model.{Course, Event, Subject}

class SubjectDatabase(appDatabase: AppDatabase) extends DatabaseImpl[Subject] {

  def createSubject: (ID, Subject) = {
    val id = reserveNextId()
    val subject = new Subject(id)
    addElement(id, subject)
  }

  def createSubjectFromDescriptor(sd: SubjectDescriptor[Course, _], events: Iterable[Event]): (ID, Subject) = {
    val entry = createSubject
    Subject.setSubjectFromDescriptor(entry._2, sd, events)
    entry
  }

  def createSubjectFromDescriptor(sd: SubjectDescriptor[Course, _]): (ID, Subject) =
    createSubjectFromDescriptor(sd, Nil)

  def createResourceFromBlueprint(sb: SubjectBlueprint): (ID, Subject) = {
    val entry = createSubject
    Subject.setSubjectFromBlueprint(entry._2, sb)
    entry
  }

  def removeSubject(id: ID): Unit = getElement(id) match {
    case Some(s) =>
      s.events.foreach(appDatabase.removeEvent)
      removeElement(id)
    case _ =>
  }

  def removeSubject(s: Subject): Unit =
    removeSubject(s.id)

  def removeSubjects(subjects: Iterable[Subject]): Unit =
    subjects.foreach(removeSubject)

  def subjects: Iterable[Subject] =
    getElements
}
