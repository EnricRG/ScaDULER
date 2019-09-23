package model

import javafx.scene.paint
import service.{ID, Identifiable}

import scala.collection.mutable

@SerialVersionUID(1L)
class Subject(id: ID) extends Identifiable(id) with Serializable {

    def DefaultColor: paint.Color = paint.Color.WHITESMOKE

    //true when user has finished creating/editing this subject, false otherwise
    private var finished: Boolean = false

    private var name: String = ""
    private var shortName: String = ""
    private var description: String = ""
    private val events: mutable.Map[ID,Event] = new mutable.HashMap
    private var color: Color = new Color(DefaultColor)
    //var linkedCourses: ListBuffer[Course] = new ListBuffer
    //var linkedCourseResources: ListBuffer[CourseResource] = new ListBuffer

    //TODO abstract this to DatabaseElement
    def isFinished: Boolean = finished
    def setAsUnfinished(): Unit = finished = false
    def setAsFinished(): Unit = finished = true

    def getName: String = name
    def setName(n: String): Unit = name = n

    def getShortName: String = shortName
    def setShortName(sn: String): Unit = shortName = sn

    def getDescription: String = description
    def setDescription(desc: String): Unit = description = desc

    //this can be abstracted and use a generic type for color
    def getColor: paint.Color = color.toColor
    def setColor(c: paint.Color): paint.Color = {
        val oldColor = color.toColor
        color = new Color(c)
        oldColor
    }

    def getEvents: Iterable[Event] = events.values
    def getEventIDs: Iterable[ID] = events.keys
    def getEventSummary: String =
        EventTypes.commonEventTypes.zip(
            EventTypes.commonEventTypes.map(
                evType => events.count(_._2.getEventType == evType)
            )
        ).map{ case (evType, n) => evType + ": " + n}.mkString("\n")

    //def setEvents(el: Iterable[NewEvent]): Unit = events = new ListBuffer() ++= el
    def addEvent(id: ID, e: Event): Unit = events.getOrElseUpdate(id, e)
    def removeEvent(id: ID): Unit = events.remove(id)

    /*def removeEventFromId(eid: Int): Unit = events.find(_.num == eid) match {
        case Some(e) => events -= e
        case None =>
    }*/

    //debug method
    def summary: String = List(name, shortName, description, events, color).mkString("\n")
}