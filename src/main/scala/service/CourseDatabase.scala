package service

import model.{Course, CourseResource, Quarter, Resource}

class CourseDatabase extends Database[String,Course]{

    class Initializer{
        //TODO: initalizer
    }

    //TODO: initialize DB from initializer
    def this(initializer: CourseDatabase#Initializer) = this

    def addCourse(c: Course): Course = {
        println(c) //TODO remove this
        addElement(c.name, c)
    }

    //def addCourseBooleanResponse(c: Course): Boolean = c == addCourse(c)


    def getCourse(course_name: String): Option[Course] = getElement(course_name)


    def getCourseOrElse(course_name: String, c: => Course): Course = getElementOrElse(course_name,c)


    def removeCourse(course_name: String): Option[Course] = elements.remove(course_name)

    //FIXME: useless overload
    def createCourse(name: String, description: Option[String] = None,
                     firstQuarterResources: Iterable[CourseResource],
                     secondQuarterResources: Iterable[CourseResource]): Course =
            addCourse(
                new Course( name, description,
                    new Quarter(firstQuarterResources),
                    new Quarter(secondQuarterResources)
                )
            )

    def createCourse(name: String, description: String,
                     firstQuarterResources: Iterable[CourseResource], secondQuarterResources: Iterable[CourseResource]): Course =
            createCourse(name, Some(description), firstQuarterResources, secondQuarterResources)
}
