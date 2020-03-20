package file.out
import java.io.{File, IOException}

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import model.blueprint.ResourceBlueprint

class SRFExporter(file: File) extends ResourceExporter {

    @throws(classOf[IOException])
    override def writeResourceBlueprints(blueprints: Iterable[ResourceBlueprint]): Unit = {
        try{
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(file, blueprints.toArray)
        } catch {
            //This line should never be reached, because catching that exception means an error in ResourceBlueprint code
            case _: JsonProcessingException => //throw new RuntimeException("Resource serialization failed.")
        }
    }
}
