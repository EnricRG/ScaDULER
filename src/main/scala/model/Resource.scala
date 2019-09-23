package model

import app.AppSettings
import service.{ID, Identifiable}

import scala.collection.mutable

@SerialVersionUID(1L)
class Resource(id: ID) extends Identifiable(id) with Serializable {

    private var name: String = ""
    private var quantity: Int = -1
    private val courses: mutable.Set[Course] = new mutable.HashSet
    private val availability: ResourceSchedule = new ResourceSchedule(AppSettings.timeSlots)

    def getName: String = name
    def setName(s: String): Unit = name = s

    def getQuantity: Int = quantity
    def setQuantity(q: Int): Unit = quantity = q
    def incrementQuantity(incr: Int): Unit = quantity += incr
    def decrementQuantity(decr: Int): Unit = if( quantity - decr >= AppSettings.minQuantityPerResource ) quantity -= decr

    def linkCourse(c: Course): Unit = courses += c
    def unlinkCourse(c: Course): Unit = courses -= c
    def getCourseList: Set[Course] = courses.toSet

    def getAvailability: ResourceSchedule = availability
    def getUnavailableIntervalsOrElse(week: Int, day: Int, el: Int): Iterable[Int] = availability.getUnavailableIntervalsOrElse(week, day, el)
}
