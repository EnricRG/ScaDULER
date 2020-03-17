package file.imprt
import java.io.File

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonNode, JsonSerializer, ObjectMapper, SerializerProvider}
import model.blueprint.ResourceBlueprint

import scala.util.{Success, Try}

@JsonSerialize(using = classOf[ResourceAvailabilityIntervalSerializer])
case class ResourceAvailabilityInterval(@JsonProperty("w") week: Int,
                                        @JsonProperty("d") day: Int,
                                        @JsonProperty("s") start: Int,
                                        @JsonProperty("e") end: Int,
                                        @JsonProperty("q") quantity: Int) extends Serializable

class ResourceAvailabilityIntervalSerializer extends JsonSerializer[ResourceAvailabilityInterval]{
    override def serialize(value: ResourceAvailabilityInterval, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
        gen.writeStartObject()
        gen.writeNumberField("w", value.week)
        gen.writeNumberField("d", value.day)
        gen.writeNumberField("s", value.start)
        gen.writeNumberField("e", value.end)
        gen.writeNumberField("q", value.quantity)
        gen.writeEndObject()
    }
}

class SRFResource() extends Serializable {

    @JsonProperty("n")
    var name: String = _

    @JsonProperty("c")
    var capacity: Int = _

    @JsonProperty("a")
    var availabilityIntervals: Array[ResourceAvailabilityInterval] = _
}

object test extends App{
    override def main(args: Array[String]): Unit = {
        val mapper = new ObjectMapper()
        val x = new SRFResource
        x.name = "jaja"
        x.capacity = 3
        x.availabilityIntervals = Array(ResourceAvailabilityInterval(1,1,4,5,2), ResourceAvailabilityInterval(1,2,1,2,2))
        //println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(x))

        val y = mapper.readValue(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(x), classOf[SRFResource])
        println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(y))
    }
}

class SRFImporter(file: File) extends ResourceImporter {

    override def getResourceBlueprints: Try[Iterable[ResourceBlueprint]] = {
        Try(new ObjectMapper().readValue(file, classOf[Array[ResourceBlueprint]]))
    }

    /*override def getResourceBlueprints: Try[Iterable[ResourceBlueprint]] = {
        var scanner: Scanner = null

        try {
            scanner = new Scanner(file)
        } catch { case e: FileNotFoundException => return Failure(e) }

        if (!scanner.hasNextInt)
            Failure(new FileFormatException("Badly formatted SRF file."))

        val nResources = scanner.nextInt

        if(nResources <= 0)
            Failure(new FileFormatException("SRF file: number of resources \"" + nResources + "\" not allowed."))

        val resources = Try {
            (1 to nResources).foreach { r =>
                val name = if (scanner.hasNextLine) scanner.nextLine else
                    return Failure(new FileFormatException("SRF file: resource " + r + " name is missing"))
                val capacity = if (scanner.hasNextInt) scanner.nextInt else
                    return Failure(new FileFormatException("SRF file: resource " + r + " quantity is missing"))

                val intervals = new ArrayBuffer[ResourceAvailabilityInterval]

                while (scanner.hasNextInt){
                    val week = scanner.nextInt
                    val day = scanner.nextInt
                    val start = scanner.nextInt
                    val end = scanner.nextInt
                    val quantity = scanner.nextInt

                    intervals += ResourceAvailabilityInterval(week, day, start, end, quantity)
                }

                if (intervals.isEmpty)
                    return Failure(new FileFormatException("SRF file: error at resource " + r +
                        ". Resources must have at least one availability interval."))

            }
        }

        Success(List())
    }*/
}
