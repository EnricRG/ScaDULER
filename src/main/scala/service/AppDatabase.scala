package service

import model.descriptor.{CourseDescriptor, EventDescriptor, ResourceDescriptor, SubjectDescriptor}
import model.{Course, Event, Resource, Subject}

class AppDatabase extends Serializable {

  type ED = EventDescriptor[Subject, Course, Resource, Event]

  private val subjectDatabase: SubjectDatabase = new SubjectDatabase(this)

  private val courseDatabase: CourseDatabase = new CourseDatabase

  private val resourceDatabase: ResourceDatabase = new ResourceDatabase

  private val eventDatabase: EventDatabase = new EventDatabase

  /** Subjects */

  def subjects: Iterable[Subject] =
    subjectDatabase.subjects

  def createSubject(): (ID, Subject) =
    subjectDatabase.createSubject

  def createSubjectFromDescriptor(descriptor: SubjectDescriptor[Course, ED]): (ID, Subject) = {
    //TODO add new intermediate SubjectDescriptor without Subject type definition to fix type issues.
    val (id, subject) = subjectDatabase.createSubjectFromDescriptor(descriptor)
    val events = descriptor.events.map(createEventFromDescriptor(_)._2)

    (id, subject)
  }

  def removeSubject(s: Subject): Unit = {
    eventDatabase.removeEvents(s.events)
    subjectDatabase.removeSubject(s)
  }

  def removeSubjects(subjects: Iterable[Subject]): Unit =
    subjects.foreach(removeSubject)

  /** Courses */

  def courses: Iterable[Course] =
    courseDatabase.courses

  def getCourseByName(name: String): Option[Course] =
    courseDatabase.getCourseByName(name)

  def createCourse(): (ID, Course) =
    courseDatabase.createCourse()

  def createCourseFromDescriptor(descriptor: CourseDescriptor): (ID, Course) =
    courseDatabase.createCourseFromDescriptor(descriptor)

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

  def getResourceByName(name: String): Option[Resource] =
    resourceDatabase.getResourceByName(name)

  def createResource(): (ID, Resource) =
    resourceDatabase.createResource

  def createResourceFromDescriptor(descriptor: ResourceDescriptor): (ID, Resource) =
    resourceDatabase.createResourceFromDescriptor(descriptor)

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

  def unassignedEvents: Iterable[Event] =
    events.filter(_.isUnassigned)

  def getEvent(id: ID): Option[Event] =
    eventDatabase.getEvent(id)

  def createEvent(): (ID, Event) =
    eventDatabase.createEvent

  def createEventFromDescriptor(descriptor: ED): (ID, Event) =
    eventDatabase.createEventFromDescriptor(descriptor)

  def removeEvent(e: Event): Unit = {
    eventDatabase.removeEvent(e)
  }

  def removeEvents(events: Iterable[Event]): Unit =
    events.foreach(removeEvent)
}
