package file.in
import java.io.{File, IOException}

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import exception.FileFormatException
import model.blueprint.ResourceBlueprint

class SRFImporter(file: File) extends ResourceImporter {

    @throws(classOf[IOException])
    @throws(classOf[FileFormatException])
    override def readResourceBlueprints: Iterable[ResourceBlueprint] = {
        try{
            new ObjectMapper().readValue(file, classOf[Array[ResourceBlueprint]])
        } catch {
            case _ : JsonProcessingException => throw new FileFormatException("SRF file format error.")
        }
    }
}
