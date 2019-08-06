package service

import model.CourseResource

class CourseResourceDatabase extends Database[String,CourseResource] {

    class Initializer{

    }

    def this(initializer: CourseResourceDatabase#Initializer) = this

    def createCourseResource(c: CourseResource): CourseResource = addElement(c.name, c)
    def createCourseResource(name: String, quantity: Int): CourseResource = createCourseResource(new CourseResource(name, quantity))

    def createCourseResourceOrElseIncrement(name: String, quantity: Int): CourseResource = {
        val c = new CourseResource(name, quantity)
        val c2 = createCourseResource(c)
        if(c != c2) c2.incrementQuantity(quantity)
        c2
    }
}
