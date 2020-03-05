package model

import app.AppSettings
import model.Weeks.Week
import service.{ID, Identifiable}

import scala.collection.mutable

@SerialVersionUID(1L)
class Resource(id: ID) extends Identifiable(id) with Serializable {

    private var name: String = ""
    private var quantity: Int = -1
    private val availability: ResourceSchedule = new ResourceSchedule(AppSettings.timeSlots)

    def getAvailability(week: Int, interval: Int): Int =
        availability.get(week, interval)
    def setAvailability(week: Int, interval: Int, amount: Int): Unit =
        availability.set(week, interval, amount)
    def incrementAvailability(week: Int, interval: Int, amount: Int): Unit =
        availability.increment(week, interval, amount)
    def decrementAvailability(week: Int, interval: Int, amount: Int): Unit =
        availability.decrement(week, interval, amount)

    def getName: String = name
    def setName(s: String): Unit = name = s

    //TODO remove all this methods
    def getQuantity: Int = quantity
    def setQuantity(q: Int): Unit = quantity = q
    def incrementQuantity(incr: Int): Unit = quantity += incr
    def decrementQuantity(decr: Int): Unit = if( quantity - decr >= AppSettings.minQuantityPerResource ) quantity -= decr

    def getMaxQuantity: Int = availability.getMax
    def getQuantityAt(week: Week, interval: Int): Int = availability.get(week.toWeekNumber, interval)

    def getAvailability: ResourceSchedule = availability
    def getIntervalsWithQuantityOrElse(week: Int, day: Int, quantity: Int, el: Int): Iterable[Int] =
        availability.getIntervalsWith(week, day, quantity, el)
    def getUnavailableIntervalsOrElse(week: Int, day: Int, el: Int): Iterable[Int] = availability.getUnavailableIntervalsOrElse(week, day, el)
}
