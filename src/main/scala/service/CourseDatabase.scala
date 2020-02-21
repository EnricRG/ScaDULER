package service

import model.{Course, Quarter}

class CourseDatabase extends Database[Course] {

    class Initializer{

    }

    def this(initializer: CourseDatabase#Initializer) = this

    def createCourse(): (ID, Course) = {
        val id = reserveNextId
        val course = new Course(id)
        addElement(id, course)
    }

    def removeCourse(cid: ID): Unit = removeElement(cid)
    def removeCourse(c: Course): Unit = removeElement(c)

    def deleteCourse(cid: ID): Unit = deleteElement(cid)
    def deleteCourse(c: Course): Unit = deleteElement(c)

    def getCourses: Iterable[Course] = getElements
}
