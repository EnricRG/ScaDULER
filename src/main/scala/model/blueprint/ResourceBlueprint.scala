package model.blueprint

import com.fasterxml.jackson.annotation.JsonProperty
import model.{Resource, ResourceSchedule}
/*
class SRFResourceScheduleSerializer extends JsonSerializer[ResourceSchedule]{
    def serialize(value: ResourceSchedule, gen: JsonGenerator, serializers: SerializerProvider): Unit = {

        val x = value.

        gen.writeStartObject()

        gen.writeEndObject()
    }
}*/

@SerialVersionUID(1L)
class ResourceBlueprint extends Serializable {

    @JsonProperty("n")
    var name: String = ""

    var quantity: Int = _ //TODO remove resource quantity

    @JsonProperty("c")
    var capacity: Int = _

    @JsonProperty("a")
    var availability: ResourceSchedule = _
}

object ResourceBlueprint{
    def fromResource(r: Resource): ResourceBlueprint = {
        val blueprint = new ResourceBlueprint
        blueprint.name = r.getName
        blueprint.capacity = r.getCapacity
        blueprint.availability = new ResourceSchedule(r.getAvailability)
        blueprint
    }
}