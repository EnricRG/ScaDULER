package model.blueprint

import app.AppSettings
import model.{ResourceLike, ResourceSchedule}

class ResourceBlueprint extends ResourceLike{
  var name: String = ""
  var quantity: Int = _ //TODO remove resource quantity
  var capacity: Int = _
  var availability: ResourceSchedule = new ResourceSchedule(AppSettings.timeSlots)

  override def getName: String = name
  override def incrementCapacity(increment: Int): Unit = capacity += increment
  override def decrementCapacity(decrement: Int): Unit =
    if( capacity - decrement >= AppSettings.minCapacityPerResource ) capacity -= decrement

  def getAvailability: ResourceSchedule = availability
  def getAvailability(week: Int, interval: Int): Int =
    availability.get(week, interval)
}