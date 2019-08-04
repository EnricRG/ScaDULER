package service

import java.util

import misc.Quarters.Quarter

import scala.collection.JavaConverters._
import model.{Course, CourseResource}

import scala.collection.mutable.ListBuffer
import scala.util.control.Exception.Described

class CourseDatabaseInitializer{

}

class CourseDatabase extends Database[String,Course]{
    def this(eventDBInitializer: CourseDatabaseInitializer) = this

    def addCourse(c: Course): Course = {
        println(c.toString)
        addElement(c.name, c)
    }

    def addCourseBooleanResponse(c: Course): Boolean = c == addCourse(c)


    def getCourse(course_name: String): Option[Course] = getElement(course_name)


    def getCourseOrElse(course_name: String, c: => Course): Course = getElementOrElse(course_name,c)


    def removeCourse(course_name: String): Option[Course] = elements.remove(course_name)


    def createCourse(name: String, description: Option[String] = None, quarter: Quarter, resources: Traversable[CourseResource] = ListBuffer()): Course =
            addCourse(new Course(name, description, quarter, resources))

    def createCourse(name: String, description: String, quarter: Quarter, resources: Traversable[CourseResource]): Course =
            createCourse(name, Some(description), quarter, resources)
}
