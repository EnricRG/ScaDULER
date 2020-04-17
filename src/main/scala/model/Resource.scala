package model

import app.AppSettings
import model.Weeks.Week
import model.blueprint.ResourceBlueprint
import service.{ID, Identifiable}

import scala.collection.mutable

@SerialVersionUID(1L)
class Resource(id: ID) extends Identifiable(id) with ResourceLike with Serializable {

  private var _name: String = ""
  private var _capacity: Int = 0
  private var _availability: ResourceSchedule = new ResourceSchedule(AppSettings.timeSlots)

  def availability_=(rs: ResourceSchedule): Unit = _availability = new ResourceSchedule(rs)

  def getAvailability(week: Int, interval: Int): Int =
    _availability.get(week, interval)
  def setAvailability(week: Int, interval: Int, amount: Int): Unit =
    _availability.set(week, interval, amount)
  def incrementAvailability(week: Int, interval: Int, amount: Int): Unit =
    _availability.increment(week, interval, amount)
  def decrementAvailability(week: Int, interval: Int, amount: Int): Unit =
    _availability.decrement(week, interval, amount)

  def name: String = _name
  def getName: String = _name
  def setName(s: String): Unit = _name = s

  override def capacity: Int = _capacity
  def getCapacity: Int = _capacity
  def setCapacity(c: Int): Unit = _capacity = c
  def incrementCapacity(increment: Int): Unit = _capacity += increment
  def decrementCapacity(decrement: Int): Unit = if( _capacity - decrement >= AppSettings.minCapacityPerResource ) _capacity -= decrement

  def getMaxQuantity: Int = _availability.getMax
  def getQuantityAt(week: Week, interval: Int): Int = _availability.get(week.toWeekNumber, interval)

  def getAvailability: ResourceSchedule = _availability
  def getIntervalsWithQuantityOrElse(week: Int, day: Int, quantity: Int, el: Int): Iterable[Int] =
    _availability.getIntervalsWith(week, day, quantity, el)
  def getUnavailableIntervalsOrElse(week: Int, day: Int, el: Int): Iterable[Int] = _availability.getUnavailableIntervalsOrElse(week, day, el)
}

object Resource{
  def setResourceFromBlueprint(r: Resource, rb: ResourceBlueprint): Unit = {
    r.setName(rb.name)
    r.setCapacity(rb.capacity)
    r.availability_=(rb.availability) //TODO update this
  }
}
