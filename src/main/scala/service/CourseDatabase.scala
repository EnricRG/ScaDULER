package service

import model.{Course, Quarter}

import scala.collection.mutable

class CourseDatabase extends Database[Course] {

    class Initializer{

    }

    def this(initializer: CourseDatabase#Initializer) = this

    private val indexByName: mutable.Map[String, Course] = new mutable.HashMap

    def index(c: Course): Unit = {
        indexByName.put(c.name, c)
    }

    def deindex(c: Course): Unit ={
        if(c != null) indexByName.remove(c.name)
    }

    def createCourse: (ID, Course) = {
        val id = reserveNextId
        val course = new Course(id)
        index(course)
        addElement(id, course)
    }

    def removeCourse(cid: ID): Unit = { removeElement(cid); deindex(getElement(cid).orNull) }
    def removeCourse(c: Course): Unit = { removeElement(c); deindex(c) }

    def deleteCourse(cid: ID): Unit = { deleteElement(cid); deindex(getElement(cid).orNull) }
    def deleteCourse(c: Course): Unit = { deleteElement(c); deindex(c) }

    def getCourseByName(n: String): Option[Course] = indexByName.get(n)
    def getCourses: Iterable[Course] = getElements
}

class ReadOnlyCourseDatabase(courseDatabase: CourseDatabase){

}
