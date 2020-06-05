package model

import model.Weeks.Week
import model.blueprint.ResourceBlueprint
import model.descriptor.ResourceDescriptor
import service.{ID, Identifiable}

@SerialVersionUID(1L)
class Resource(id: ID) extends Identifiable(id) with ResourceLikeImpl with Serializable {
  def getMaxQuantity: Int = availability.getMax
  def getQuantityAt(week: Week, interval: Int): Int = availability.get(week.toWeekNumber, interval)

  def getIntervalsWithQuantityOrElse(week: Int, day: Int, quantity: Int, el: Int): Iterable[Int] =
    availability.getIntervalsWith(week, day, quantity, el)

  def getUnavailableIntervalsOrElse(week: Int, day: Int, el: Int): Iterable[Int] =
    availability.getUnavailableIntervalsOrElse(week, day, el)
}

object Resource{
  def setResourceFromDescriptor(r: Resource, rd: ResourceDescriptor): Unit = {
    r.name = rd.name
    r.capacity = rd.capacity
    r.availability = rd.availability //this makes a new copy of rd availability, no aliasing here.
  }

  def setResourceFromBlueprint(r: Resource, rb: ResourceBlueprint): Unit = {
    r.name = rb.name
    r.capacity = rb.capacity
    r.availability = rb.availability //this makes a new copy of rb availability, no aliasing here.
  }
}
