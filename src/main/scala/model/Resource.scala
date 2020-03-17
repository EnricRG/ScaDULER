package model

import app.AppSettings
import model.Weeks.Week
import model.blueprint.ResourceBlueprint
import service.{ID, Identifiable}

@SerialVersionUID(1L)
class Resource(id: ID) extends Identifiable(id) with Serializable {

    private var name: String = ""
    private var quantity: Int = -1 //TODO delete this when proper serialization is implemented.
    private var capacity: Int = 0
    private var availability: ResourceSchedule = new ResourceSchedule(AppSettings.timeSlots)

    def getAvailability(week: Int, interval: Int): Int =
        availability.get(week, interval)
    def setAvailability(week: Int, interval: Int, amount: Int): Unit =
        availability.set(week, interval, amount)
    def setAvailability(availability: ResourceSchedule): Unit =
        this.availability = availability
    def incrementAvailability(week: Int, interval: Int, amount: Int): Unit =
        availability.increment(week, interval, amount)
    def decrementAvailability(week: Int, interval: Int, amount: Int): Unit =
        availability.decrement(week, interval, amount)

    def getName: String = name
    def setName(s: String): Unit = name = s

    def getCapacity: Int = capacity
    def setCapacity(c: Int): Unit = capacity = c
    def incrementCapacity(incr: Int): Unit = capacity += incr
    def decrementCapacity(decr: Int): Unit = if( capacity - decr >= AppSettings.minCapacityPerResource ) capacity -= decr

    def getMaxQuantity: Int = availability.getMax
    def getQuantityAt(week: Week, interval: Int): Int = availability.get(week.toWeekNumber, interval)

    def getAvailability: ResourceSchedule = availability
    def getIntervalsWithQuantityOrElse(week: Int, day: Int, quantity: Int, el: Int): Iterable[Int] =
        availability.getIntervalsWith(week, day, quantity, el)
    def getUnavailableIntervalsOrElse(week: Int, day: Int, el: Int): Iterable[Int] = availability.getUnavailableIntervalsOrElse(week, day, el)
}

object Resource{
    def setResourceFromBlueprint(r: Resource, rb: ResourceBlueprint): Unit = {
        r.setName(rb.name)
        r.setCapacity(rb.capacity)
        if(rb.availability != null) r.setAvailability(rb.availability)
    }
}
