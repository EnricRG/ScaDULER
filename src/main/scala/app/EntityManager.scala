package app

import control.MainController
import file.imprt.ImportJob
import file.imprt.blueprint.{CourseBlueprint, EventBlueprint, ResourceBlueprint, SubjectBlueprint}
import model.{Course, Event, EventType, EventTypes, Resource, Subject}
import service.{AppDatabase, CourseDatabase, EventDatabase, ResourceDatabase, SubjectDatabase}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object EntityManager {

    private val database: AppDatabase = MainApp.getDatabase
    private val courseDatabase: CourseDatabase = database.courseDatabase
    private val resourceDatabase: ResourceDatabase = database.resourceDatabase
    private val subjectDatabase: SubjectDatabase = database.subjectDatabase
    private val eventDatabase: EventDatabase = database.eventDatabase

    def importEntities(importJob: ImportJob, mc: MainController): Unit = {
        val courseMapper = new mutable.HashMap[CourseBlueprint, Course]
        val resourceMapper = new mutable.HashMap[ResourceBlueprint, Resource]
        val subjectMapper = new mutable.HashMap[SubjectBlueprint, Subject]

        importJob.courses.foreach(cb => {
            val existingCourse = courseDatabase.getCourseByName(cb.name)
            if(existingCourse.isEmpty) {
                val course = courseDatabase.createCourse._2
                setCourseFromBlueprint(course, cb)
                courseMapper.put(cb, course)
                mc.addCourseTab(course, false)
            }
            else if (!courseMapper.contains(cb)) courseMapper.put(cb, existingCourse.get)
        })

        importJob.resources.foreach(rb => {
            val existingResource = resourceDatabase.getResourceByName(rb.name)
            if(existingResource.isEmpty) {
                val resource = resourceDatabase.createResource._2
                setResourceFromBlueprint(resource, rb)
                resourceMapper.put(rb, resource)
            }
            else if (!resourceMapper.contains(rb)) resourceMapper.put(rb, existingResource.get)
        })

        importJob.subjects.foreach(sb => {
            val subject = subjectDatabase.createSubject._2
            setSubjectFromBlueprint(subject, sb, courseMapper(sb.course))
            subjectDatabase.setAsFinished(subject.getID)
            subjectMapper.put(sb, subject)
        })

        val eventsByType = new mutable.HashMap[EventType, ArrayBuffer[Event]]
        EventTypes.commonEventTypes.foreach(et => eventsByType.put(et, new ArrayBuffer))
        importJob.events.foreach(eb =>{
            val event = eventDatabase.createEvent._2
            setEventFromBlueprint(event, eb,
                subjectMapper(eb.subject.get),
                resourceMapper.get(eb.neededResource.orNull))
            eventsByType(event.getEventType) += event
            mc.addUnassignedEvent(event)
        })

        //TODO manage event incompatibilities
        println(importJob.subjects.length)
    }

    private def setCourseFromBlueprint(c: Course, cb: CourseBlueprint): Unit = {
        c.name = cb.name
    }

    private def setResourceFromBlueprint(r: Resource, rb: ResourceBlueprint): Unit = {
        r.setName(rb.name)
        r.setQuantity(rb.quantity)
    }

    private def setSubjectFromBlueprint(s: Subject, sb: SubjectBlueprint, c: Course): Unit = {
        s.setName(sb.name)
        s.setShortName(sb.shortName)
        s.setCourse(c)
        s.setQuarter(sb.quarter)
        sb.additionalInformation.foreach(pair => s.setAdditionalField(pair._1, pair._2))
    }

    private def setEventFromBlueprint(e: Event, eb: EventBlueprint, s: Subject, r: Option[Resource]): Unit = {
        e.setName(eb.name)
        e.setShortName(eb.shortName)
        e.setEventType(eb.eventType)
        e.setDuration(eb.duration)
        e.setSubject(s)
        if(r.nonEmpty) e.setNeededResource(r.get)
        //TODO complete copy
        //e.setPeriodicity(eb.periodicity)
    }
}



