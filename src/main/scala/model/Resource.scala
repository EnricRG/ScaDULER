package model

import app.AppSettings
import service.Identifiable

import scala.collection.mutable.ListBuffer

abstract class QuantifiableResource{
    def getQuantity: Int
    def getAvailableQuantity: Int
}

class Resource(val name: String, var quantity: Int) extends QuantifiableResource with Identifiable {

    val courses: ListBuffer[Course] = new ListBuffer
    val courseResources: ListBuffer[CourseResource] = new ListBuffer
    var availability: ResourceSchedule = new ResourceSchedule(AppSettings.timeSlots)

    def getName: String = name
    def getQuantity: Int = quantity
    def incrementQuantity(incr: Int): Unit = quantity += incr
    def decrementQuantity(decr: Int): Unit = if( quantity - decr >= AppSettings.minQuantityPerResource ) quantity -= decr

    def linkCourse(c: Course): Unit = courses += c
    def unlinkCourse(c: Course): Unit = courses -= c

    def linkCourseResource(cr: CourseResource): Unit = courseResources += cr
    def unlinkCourseResource(cr: CourseResource): Unit = courseResources -= cr

    def getAvailableQuantity: Int = quantity - courseResources.map(_.quantity).sum
}
