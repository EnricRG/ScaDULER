package service

import model.{Course, Event, Subject}

class AppDatabase (eventDBInitializer: Option[EventDatabase#Initializer] = None,
                   subjectDBInitializer: Option[SubjectDatabase#Initializer] = None,
                   courseDBInitializer: Option[CourseDatabase#Initializer] = None,
                   resourceDBInitializer: Option[ResourceDatabase#Initializer] = None) extends Serializable {

  lazy val eventDatabase: EventDatabase = eventDBInitializer match {
    case Some(eDBi) => new EventDatabase(eDBi)
    case None => new EventDatabase
  }

  lazy val subjectDatabase: SubjectDatabase = subjectDBInitializer match {
    case Some(sDBi) => new SubjectDatabase(sDBi)
    case None => new SubjectDatabase
  }

  lazy val courseDatabase: CourseDatabase = courseDBInitializer match {
    case Some(cDBi) => new CourseDatabase(cDBi)
    case None => new CourseDatabase
  }

  lazy val resourceDatabase: ResourceDatabase = resourceDBInitializer match {
    case Some(crDBi) => new ResourceDatabase(crDBi)
    case None => new ResourceDatabase
  }

  def createCourse(): (ID, Course) = courseDatabase.createCourse()

  def removeCourse(c: Course, hardDelete: Boolean): (Iterable[Subject], Iterable[Event]) = {
    val affectedSubjects = subjectDatabase.getFinishedSubjects.filter(sb => sb.course.contains(c))
    lazy val otherAffectedEvents = eventDatabase.getElements.filter(e=> e.subject.isEmpty && e.course.contains(c))

    if(hardDelete) {
      //store subject events before deleting them
      val affectedSubjectEvents = affectedSubjects.flatMap(_.events)

      subjectDatabase.removeSubjects(affectedSubjects)
      eventDatabase.removeEvents(otherAffectedEvents)

      (affectedSubjects, affectedSubjectEvents ++ otherAffectedEvents)
    } else {
      affectedSubjects.foreach(sb => {
        sb.course = None
        sb.events.foreach(_.course = None)
      })
      otherAffectedEvents.foreach(_.course = None)

      (Nil, Nil)
    }
  }

}
