package misc

import model.{EventType, EventTypes}

class EventTypeIncompatibility(firstType: EventType, secondType: EventType) {
    def getFirstType: EventType = firstType
    def getSecondType: EventType = secondType

    override def toString: String = firstType.toString + " - " + secondType.toString
    def toShortString: String = firstType.toShortString + " - " + secondType.toShortString
}

object EventTypeIncompatibilities{
    val list: List[EventTypeIncompatibility] = EventTypes.commonEventTypes.combinations(2)
        .map{case x::y::_ => (x,y)}
        .map(x => new EventTypeIncompatibility(x._1,x._2))
        .toList
}
