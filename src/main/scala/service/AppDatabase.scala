package service

import model.{Course, Event, Subject}

class AppDatabase extends Serializable {

  lazy val eventDatabase: EventDatabase = new EventDatabase

  lazy val subjectDatabase: SubjectDatabase = new SubjectDatabase

  lazy val courseDatabase: CourseDatabase = new CourseDatabase

  lazy val resourceDatabase: ResourceDatabase = new ResourceDatabase

  /** Subjects */

  def subjects: Iterable[Subject] =
    subjectDatabase.subjects

  def createSubject(): (ID, Subject) =
    subjectDatabase.createSubject

  def removeSubject(s: Subject): Unit = {
    eventDatabase.removeEvents(s.events)
    subjectDatabase.removeSubject2(s)
  }

  def removeSubjects(subjects: Iterable[Subject]): Unit =
    subjects.foreach(removeSubject)

  /** Courses */

  def createCourse(): (ID, Course) =
    courseDatabase.createCourse()

  def removeCourse(c: Course, hardDelete: Boolean): (Iterable[Subject], Iterable[Event]) = {
    val affectedSubjects = subjectDatabase.getFinishedSubjects.filter(sb => sb.course.contains(c))
    lazy val otherAffectedEvents = eventDatabase.getElements.filter(e=> e.subject.isEmpty && e.course.contains(c))

    if(hardDelete) {
      //store subject events before deleting them
      val affectedSubjectEvents = affectedSubjects.flatMap(_.events)

      subjectDatabase.removeSubjects(affectedSubjects) //this also deletes subject events
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

  def removeCourses(courses: Iterable[Course], hardDelete: Boolean): (Iterable[Subject], Iterable[Event]) =
    courses.map(removeCourse(_,hardDelete)).reduce((a, b) => (a._1 ++ b._1, a._2 ++ b._2))

  /** Resources */

  /** Events */
}
