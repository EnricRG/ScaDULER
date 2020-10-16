package service

import model.{Course, Event, Resource, Subject}

class AppDatabase extends Serializable {

  lazy val eventDatabase: EventDatabase = new EventDatabase

  lazy val subjectDatabase: SubjectDatabase = new SubjectDatabase(this)

  lazy val courseDatabase: CourseDatabase = new CourseDatabase

  lazy val resourceDatabase: ResourceDatabase = new ResourceDatabase

  /** Subjects */

  def subjects: Iterable[Subject] =
    subjectDatabase.subjects

  def createSubject(): (ID, Subject) =
    subjectDatabase.createSubject

  def removeSubject(s: Subject): Unit = {
    eventDatabase.removeEvents(s.events)
    subjectDatabase.removeSubject(s)
  }

  def removeSubjects(subjects: Iterable[Subject]): Unit =
    subjects.foreach(removeSubject)

  /** Courses */

  def courses: Iterable[Course] =
    courseDatabase.courses

  def createCourse(): (ID, Course) =
    courseDatabase.createCourse()

  def removeCourse(c: Course, hardDelete: Boolean): (Iterable[Subject], Iterable[Event]) = {
    val affectedSubjects = subjectDatabase.subjects.filter(sb => sb.course.contains(c))
    lazy val otherAffectedEvents = eventDatabase.events.filter(e=> e.subject.isEmpty && e.course.contains(c))

    courseDatabase.removeCourse(c)

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

  def resources: Iterable[Resource] =
    resourceDatabase.resources

  def createResource(): (ID, Resource) =
    resourceDatabase.createResource

  def removeResource(r: Resource): Unit = {
    //TODO optimize with new data model relations.
    events.filter(_.neededResource.contains(r)).foreach(_.neededResource = None)
    resourceDatabase.removeResource(r)
  }

  def removeResources(resources: Iterable[Resource]): Unit =
    resources.foreach(removeResource)

  /** Events */

  def events: Iterable[Event] =
    eventDatabase.events

  def createEvent(): (ID, Event) =
    eventDatabase.createEvent

  def removeEvent(e: Event): Unit = {
    eventDatabase.removeEvent(e)
  }

  def removeEvents(events: Iterable[Event]): Unit =
    events.foreach(removeEvent)
}
