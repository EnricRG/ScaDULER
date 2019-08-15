package service

import model.{Course, CourseResource, Quarter}

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

    def removeCourse(c: Course) = removeElement(c)

    //FIXME: useless overload
    def createCourse(name: String, description: Option[String] = None,
                     courseResources: Iterable[CourseResource]): Course =
            addCourse(
                new Course( name, description,
                    new Quarter(courseResources),
                    new Quarter(courseResources),
                )
            )

    def createCourse(name: String, description: String,
                     courseResources: Iterable[CourseResource]): Course =
            createCourse(name, Some(description), courseResources)
}
