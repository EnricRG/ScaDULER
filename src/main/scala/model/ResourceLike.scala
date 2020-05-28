package model

import app.AppSettings

trait ResourceLike {
  def name: String
  def name_=(name: String): Unit

  def capacity: Int
  def capacity_=(capacity: Int): Unit
  def incrementCapacity(increment: Int)
  def decrementCapacity(decrement: Int)

  def availability: ResourceSchedule
  def availability_=(schedule: ResourceSchedule): Unit

  def availabilityAt(week: Int, interval: Int): Int
  def updateAvailabilityAt(week: Int, interval: Int, amount: Int): Unit
  def incrementAvailabilityAt(week: Int, interval: Int, amount: Int): Unit
  def decrementAvailabilityAt(week: Int, interval: Int, amount: Int): Unit
}

trait ResourceLikeImpl extends ResourceLike {
  private var _name: String = ""
  private var _capacity: Int = 0
  private var _availability: ResourceSchedule = new ResourceSchedule(AppSettings.timeSlots)

  def name: String = _name
  def name_=(name: String): Unit = _name = name

  def capacity: Int = _capacity
  def capacity_=(capacity: Int): Unit =
    if( capacity >= AppSettings.minCapacityPerResource ) _capacity = capacity
  def incrementCapacity(increment: Int): Unit =
    if( _capacity + increment >= AppSettings.minCapacityPerResource ) _capacity += increment
  def decrementCapacity(decrement: Int): Unit =
    if( _capacity - decrement >= AppSettings.minCapacityPerResource ) _capacity -= decrement

  def availability: ResourceSchedule = _availability
  def availability_=(schedule: ResourceSchedule): Unit = _availability = new ResourceSchedule(schedule)

  def availabilityAt(week: Int, interval: Int): Int =
    _availability.get(week, interval)
  def updateAvailabilityAt(week: Int, interval: Int, amount: Int): Unit =
    _availability.set(week, interval, amount)
  def incrementAvailabilityAt(week: Int, interval: Int, amount: Int): Unit =
    _availability.increment(week, interval, amount)
  def decrementAvailabilityAt(week: Int, interval: Int, amount: Int): Unit =
    _availability.decrement(week, interval, amount)
}
