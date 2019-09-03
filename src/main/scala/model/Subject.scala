package model

import javafx.scene.paint
import javafx.scene.paint.Color
import service.Identifiable

import scala.collection.mutable

//TODO use case classes
@SerialVersionUID(1L)
class Color(r: Double, g: Double, b: Double, o: Double) extends Serializable{
    def this(color: paint.Color) = this(color.getRed, color.getGreen, color.getBlue, color.getOpacity)
    def toColor: paint.Color = new paint.Color(r,g,b,o)
}

@SerialVersionUID(1L)
class Subject extends Identifiable with Serializable {

    def DefaultColor: paint.Color = Color.WHITESMOKE

    //true when user has finished creating/editing this subject, false otherwise
    private var finished: Boolean = false
    def isFinished = finished
    def setAsUnfinished: Unit = finished = false
    def setAsFinished: Unit = finished = true

    var name: String = ""
    var shortName: String = ""
    var description: String = ""
    var events: mutable.Map[Long,Event] = new mutable.HashMap
    var color: Color = new model.Color(DefaultColor)
    //var linkedCourses: ListBuffer[Course] = new ListBuffer
    //var linkedCourseResources: ListBuffer[CourseResource] = new ListBuffer

    def getName: String = name
    def setName(n: String): Unit = name = n

    def getShortName: String = shortName
    def setShortName(sn: String): Unit = shortName = sn

    def getDescription: String = description
    def setDescription(desc: String): Unit = description = desc

    //this can be abstracted and use a generic type for color
    def getColor: paint.Color = color.toColor
    def setColor(c: paint.Color): paint.Color = {val oldColor = color.toColor; color = new Color(c); oldColor}

    def getEvents: Iterable[Event] = events.values
    def getEventIDs: Iterable[Long] = events.keys
    def getEventSummary: String =
        EventTypes.commonEventTypes.zip(
            EventTypes.commonEventTypes.map(
                evType => events.count(_._2.getEventType == evType)
            )
        ).map{ case (evType, n) => evType + ": " + n}.mkString("\n")

    //def setEvents(el: Iterable[NewEvent]): Unit = events = new ListBuffer() ++= el
    def addEvent(id: Long, e: Event): Unit = events.getOrElseUpdate(id, e)
    def removeEvent(id: Long): Unit = events.remove(id)

    /*def removeEventFromId(eid: Int): Unit = events.find(_.num == eid) match {
        case Some(e) => events -= e
        case None =>
    }*/

    //debug method
    def summary: String = List(name, shortName, description, events, color).mkString("\n")
}