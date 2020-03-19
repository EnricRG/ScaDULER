package model

import java.util

import com.fasterxml.jackson.annotation.{JsonGetter, JsonIgnore, JsonProperty, JsonSetter}
import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers.IntegerDeserializer
import com.fasterxml.jackson.databind.deser.std.{StdKeyDeserializer, StdKeyDeserializers}
import com.fasterxml.jackson.databind.ser.std.NumberSerializers.IntegerSerializer
import com.fasterxml.jackson.databind.ser.std.{StdKeySerializer, StdKeySerializers}

import scala.collection.{JavaConverters, mutable}

@SerialVersionUID(1L)
class Schedule[T](@JsonProperty("i") intervals: Int) extends Serializable {

    private var timeline: mutable.Map[Int,T] = new mutable.HashMap


    def getValueAtInterval(interval: Int): Option[T] = if(interval < intervals) timeline.get(interval) else None
    def getValueAtIntervalOrElse(interval: Int, el: => T): T = if(interval < intervals) timeline.getOrElse(interval, el) else el
    //Danger, if interval exceeds limit caller will not know that this happened. So...
    //pre: interval < intervals
    def getValueAtIntervalOrElseUpdate(interval: Int, el: => T): T = if(interval < intervals) timeline.getOrElseUpdate(interval, el) else el

    def updateInterval(interval: Int, element: T): Unit = if(interval < intervals) timeline.update(interval, element)
    def removeInterval(interval: Int): Unit = timeline.remove(interval)

    //I did this because I could.
    def applyToInterval(interval: Int, function: => (T => Any)): Option[Any] = getValueAtInterval(interval) match{
        case Some(t) => Some(function.apply(t))
        case _ => None
    }

    @JsonIgnore
    def getAllElements: Iterable[T] = timeline.toSeq.sortBy(_._1).map(_._2)

    @JsonIgnore
    def getAllPairs: Iterable[(Int, T)] = timeline.toSeq.sortBy(_._1)

    override def toString: String = timeline.toString

    /*** JSON Serialization and Deserialization ***/

    @JsonProperty("i")
    @JsonGetter
    private def getIntervals: Int = intervals

    @JsonProperty("t")
    @JsonGetter
    @JsonDeserialize(keyAs = classOf[Int])
    private def getJavaTimeline: util.Map[Int, T] = JavaConverters.mapAsJavaMap(timeline)

    @JsonProperty("t")
    @JsonSetter
    private def setJavaTimeline(javaTimeline: util.Map[Int, T]): Unit = {
        timeline = JavaConverters.mapAsScalaMap(javaTimeline) //this creates a HashMap
    }
}
