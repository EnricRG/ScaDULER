package service

import app.MainApp
import model.Subject

class SubjectDatabase extends Database[Subject]{

    private lazy val eventDatabase = MainApp.database.eventDatabase

    class Initializer{
        //TODO
    }

    def this(initializer: SubjectDatabase#Initializer) = this

    def newSubject(): Long = addElement(new Subject)
    def removeSubject(sid: Long): Option[Subject] = getElement(sid) match{
        case Some(s) => {
            s.events.foreach{ case (eid,_) => eventDatabase.removeElement(eid) }
            removeElement(sid)
        }
        case _ => None
    }

    def getFinishedSubjectsIDs(): Iterable[Long] = getIDs.filter(getElement(_).get.isFinished)
}
