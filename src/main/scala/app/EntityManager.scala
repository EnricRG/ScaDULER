package app

import control.MainController
import file.imprt.ImportJob
import model._
import model.blueprint.{CourseBlueprint, ResourceBlueprint, SubjectBlueprint}
import service._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object EntityManager {

  private val database: AppDatabase = MainApp.getDatabase
  private val courseDatabase: CourseDatabase = database.courseDatabase
  private val resourceDatabase: ResourceDatabase = database.resourceDatabase
  private val subjectDatabase: SubjectDatabase = database.subjectDatabase
  private val eventDatabase: EventDatabase = database.eventDatabase

  //progress stage initialized
  def importEntities(importJob: ImportJob, mc: MainController): Unit = {
    val courseMapper = new mutable.HashMap[CourseBlueprint, Course]
    val resourceMapper = new mutable.HashMap[ResourceBlueprint, Resource]
    val subjectMapper = new mutable.HashMap[SubjectBlueprint, Subject]

    importJob.courses.foreach(cb => {
      val existingCourse = courseDatabase.getCourseByName(cb.name)
      if(existingCourse.isEmpty) {
        val course = courseDatabase.createCourse()._2
        Course.setCourseFromBlueprint(course, cb)
        courseMapper.put(cb, course)
        mc.addCourseTab(course, false)
      }
      else if (!courseMapper.contains(cb)) courseMapper.put(cb, existingCourse.get)
    })

    importJob.resources.foreach(rb => {
      val existingResource = resourceDatabase.getResourceByName(rb.name)
      if(existingResource.isEmpty) {
        val resource = resourceDatabase.createResource._2
        Resource.setResourceFromBlueprint(resource, rb)
        resourceMapper.put(rb, resource)
      }
      else if (!resourceMapper.contains(rb)) resourceMapper.put(rb, existingResource.get)
    })

    importJob.subjects.foreach(sb => {
      val subject = subjectDatabase.createSubject._2
      val subjectCourse = if(sb.course.nonEmpty) Some(courseMapper(sb.course.get)) else None
      Subject.setSubjectFromBlueprint(subject, sb)
      subject.course = subjectCourse
      subjectMapper.put(sb, subject)
    })

    val eventsByType = new mutable.HashMap[EventType, ArrayBuffer[Event]]
    EventTypes.commonEventTypes.foreach(et => eventsByType.put(et, new ArrayBuffer))
    importJob.events.foreach(eb =>{
      val event = eventDatabase.createEvent._2

      Event.setEventFromBlueprint(event, eb)

      if(eb.course.nonEmpty) event.course = courseMapper.get(eb.course.get)
      if(eb.subject.nonEmpty) event.subject = subjectMapper.get(eb.subject.get)
      if(eb.neededResource.nonEmpty) event.neededResource = resourceMapper.get(eb.neededResource.get)

      eventsByType(event.eventType) += event
      mc.addUnassignedEvent(event)
    })
  }
}



