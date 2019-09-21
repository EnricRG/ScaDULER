package service

import model.{Course, Quarter}

class CourseDatabase extends Database[Course] {

    class Initializer{
        //TODO: initalizer
    }

    //TODO: initialize DB from initializer
    def this(initializer: CourseDatabase#Initializer) = this

    //TODO: update to pure DBID model
    def addCourse(c: Course): Course = {
        println(c) //TODO remove this
        val id = addElement(c)
        //c.setID(id)
        getElement(id).get //this should be secure because we just added the course
    }

    def removeCourse(c: Course): Option[Course] = removeElement(c)

    //FIXME: useless overload
    def createCourse(name: String, description: Option[String] = None): Course =
            addCourse(
                new Course( name, description,
                    new Quarter(),
                    new Quarter(),
                )
            )

    def createCourse(name: String, description: String): Course =
            createCourse(name, Some(description))
}
