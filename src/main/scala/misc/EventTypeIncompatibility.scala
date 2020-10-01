package misc

import model.{EventType, EventTypes}

class EventTypeIncompatibility(firstType: EventType, secondType: EventType) {
  def getFirstType: EventType = firstType

  def getSecondType: EventType = secondType

  override def toString: String = firstType.toString + " - " + secondType.toString

  def toShortString: String = firstType.toShortString + " - " + secondType.toShortString
}

object EventTypeIncompatibilities {
  val list: List[EventTypeIncompatibility] = eventTypeIncompatibilities.toList

  val set: Set[EventTypeIncompatibility] = eventTypeIncompatibilities.toSet

  private def eventTypeIncompatibilities: Iterator[EventTypeIncompatibility] =
    EventTypes.commonEventTypes.combinations(2)
      .collect { case x :: y :: _ => new EventTypeIncompatibility(x, y) }

}
