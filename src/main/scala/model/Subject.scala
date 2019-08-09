package model

import javafx.scene.paint.Color

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Subject {

    def DefaultColor: Color = Color.WHITESMOKE

    //true when user has finished creating/editing this subject, false otherwise
    private var finished: Boolean = false
    def setAsUnfinished: Unit = finished = false
    def setAsFinished: Unit = {
        finished = true
        println(this) //TODO remove this
    }

    var name: String = ""
    var shortName: String = ""
    var description: String = ""
    var events: mutable.Map[Long,NewEvent] = new mutable.HashMap
    var color: Color = DefaultColor
    //var linkedCourses: ListBuffer[Course] = new ListBuffer
    //var linkedCourseResources: ListBuffer[CourseResource] = new ListBuffer

    def getName: String = name
    def setName(n: String): Unit = name = n

    def getShortName: String = shortName
    def setShortName(sn: String): Unit = shortName = sn

    def getDescription: String = description
    def setDescription(desc: String): Unit = description = desc

    //this can be abstracted and use a generic type for color
    def getColor: Color = color
    def setColor(c: Color): Color = {val oldColor = color; color = c; oldColor}

    def getEvents: Iterable[NewEvent] = events.values
    def getEventIDs: Iterable[Long] = events.keys
    //def setEvents(el: Iterable[NewEvent]): Unit = events = new ListBuffer() ++= el
    def addEvent(id: Long, e: NewEvent): Unit = events.getOrElseUpdate(id, e)
    def removeEvent(id: Long): Unit = events.remove(id)

    /*def removeEventFromId(eid: Int): Unit = events.find(_.num == eid) match {
        case Some(e) => events -= e
        case None =>
    }*/

    //debug method
    override def toString: String = List(name, shortName, description, events, color).mkString("\n")
}