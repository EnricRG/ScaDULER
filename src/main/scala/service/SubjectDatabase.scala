package service

import app.MainApp
import model.Subject

class SubjectDatabase extends Database[Subject] {

  private lazy val eventDatabase = MainApp.getDatabase.eventDatabase

  def subjects: Iterable[Subject] = getElements

  def removeSubject2(s: Subject): Unit =
    removeElement(s) //TODO adapt to new single delete mode database

  def removeSubjects(subjects: Iterable[Subject]): Unit = {
    subjects.foreach(removeSubject2)
  }


  //TODO remove ID from public interfaces. Only for internal uses.
  def createSubject: (ID, Subject) = {
      val id = reserveNextId
      val subject = new Subject(id)
      addElement(id, subject)
  }

  def removeSubject(sid: ID): Unit = getElement(sid) match{
      case Some(s) =>
          s.events.map(_.getID).foreach(eventDatabase.removeEvent)
          removeElement(sid)
      case _ =>
  }
  def removeSubject(s: Subject): Unit = removeSubject(s.getID)



  def deleteSubject(sid: ID): Unit = getElement(sid) match{
      case Some(s) =>
          s.events.map(_.getID).foreach(eventDatabase.deleteEvent)
          deleteElement(sid)
      case _ =>
  }
  def deleteSubject(s: Subject): Unit = deleteSubject(s.getID)

  def getFinishedSubjectsIDs: Iterable[Long] = getIDs.filter(isFinished)
  def getFinishedSubjects: Iterable[Subject] = getFinishedSubjectsIDs.map(getElement(_).get)
}
