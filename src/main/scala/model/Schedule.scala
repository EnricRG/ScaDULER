package model

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.ContextualDeserializer

import scala.collection.mutable

class CustomTuple2[T1,T2](@JsonProperty("_1") val _1: T1, @JsonProperty("_2") val _2: T2)

class TimelineSerializer[T] extends JsonSerializer[mutable.Map[Int,T]]{
    def serialize(value: mutable.Map[Int,T], gen: JsonGenerator, serializers: SerializerProvider): Unit = {
        gen.writeStartArray()
        val objectMapper = new ObjectMapper()
        value.toArray.foreach(pair => {
            val x = new CustomTuple2[Int, T](pair._1, pair._2)
            gen.writeStartObject()
            //gen.writeObjectFieldStart("_1")
            gen.writeStringField("_1", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pair._1))
            gen.writeStringField("_2", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pair._2))
            //gen.writeObjectFieldStart("_2")
            //gen.writeObject(pair._2)
            gen.writeEndObject()
            //gen.writeObject(x)
        })
        gen.writeEndArray()
    }
}

class ScheduleDeserializer[T] extends JsonDeserializer[Schedule[T]] with ContextualDeserializer{

    private var valueType: JavaType = _

    def createContextual(ctxt: DeserializationContext, property: BeanProperty): JsonDeserializer[_] = {
        val propertyType: JavaType = property.getType
        val valueType: JavaType = propertyType.containedType(0)
        val deserializer = new ScheduleDeserializer
        deserializer.valueType = valueType
        deserializer
    }

    def deserialize(p: JsonParser, ctxt: DeserializationContext): Schedule[T] = {
        val jsonNode = p.getCodec.readTree(p).asInstanceOf[JsonNode]

        val intervals = jsonNode.get("i").asInt
        val schedule = new Schedule[T](intervals)

        val objectMapper = new ObjectMapper()

        val timelineNodes = objectMapper.readValue(jsonNode.get("t").toString, classOf[Array[JsonNode]])
        timelineNodes.foreach(node => {
            val interval = node.get("_1").asInt
            val quantity = objectMapper.readValue(node.get("_2").toString, valueType)
            schedule.updateInterval(interval, quantity)
        })

        schedule
    }
}

@SerialVersionUID(1L)
class Schedule[T](@JsonProperty("i") intervals: Int) extends Serializable {

    @JsonProperty("t")
    @JsonSerialize(using = classOf[TimelineSerializer[T]])
    private val timeline: mutable.Map[Int,T] = new mutable.HashMap


    @JsonIgnore
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

    override def toString: String = timeline.toString
}
