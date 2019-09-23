package service

import app.MainApp
import model.Subject

class SubjectDatabase extends Database[Subject] {

    private lazy val eventDatabase = MainApp.getDatabase.eventDatabase

    class Initializer{

    }

    def this(initializer: SubjectDatabase#Initializer) = this

    def newSubject(): (ID, Subject) = {
        val id = reserveNextId
        val subject = new Subject(id)
        addElement(id, subject)
    }

    def removeSubject(sid: ID): Unit = getElement(sid) match{
        case Some(s) =>
            s.getEventIDs.foreach(eventDatabase.removeEvent)
            removeElement(sid)
        case _ =>
    }
    def removeSubject(s: Subject): Unit = removeSubject(s.getID)

    def deleteSubject(sid: ID): Unit = getElement(sid) match{
        case Some(s) =>
            s.getEventIDs.foreach(eventDatabase.deleteEvent)
            deleteElement(sid)
        case _ =>
    }
    def deleteSubject(s: Subject): Unit = deleteSubject(s.getID)

    def getFinishedSubjectsIDs: Iterable[Long] = getIDs.filter(getElement(_).get.isFinished)
    def getFinishedSubjects: Iterable[Subject] = getElements.filter(_.isFinished)
}
