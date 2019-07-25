package model

import scala.collection.mutable.ListBuffer

class Subject(var name: String, var short_name: String, var description: Option[String] = None,
              var events: ListBuffer[EventData] = ListBuffer()) {

    def addEvent(e: EventData): Unit = events += e
    def removeEvent(e: EventData): Unit = events -= e
    def removeEventFromId(eid: Int): Unit = events.filter(_.num != eid)
}

object test1 extends App{

    val e = new EventData(2)
    val sub = new Subject("haha", "yes", events = ListBuffer(e))

    //sub.removeEvent(e)

    for(e <- sub.events) println(e.num)
}