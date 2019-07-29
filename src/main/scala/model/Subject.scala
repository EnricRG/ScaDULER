package model

import scala.collection.mutable.ListBuffer

class Subject(var name: String, var short_name: String, var description: Option[String] = None,
              var events: ListBuffer[Event] = ListBuffer()) {

    def addEvent(e: Event): Unit = events += e
    def removeEvent(e: Event): Unit = events -= e
    def removeEventFromId(eid: Int): Unit = events.find(_.num == eid) match {
        case Some(e) => events -= e
        case None =>
    }
}

object test1 extends App{

    val e = new Event(2)
    val sub = new Subject("haha", "yes", events = ListBuffer(e))

    sub.removeEventFromId(3)

    for(e <- sub.events) println(e.num)
}