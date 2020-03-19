package file.imprt
import java.io.File

import com.fasterxml.jackson.databind.ObjectMapper
import model.ResourceSchedule
import model.blueprint.ResourceBlueprint

import scala.util.Try

object test extends App{
    override def main(args: Array[String]): Unit = {
        val writer = new SRFImporter(new File("jajayes.txt"))
        val x = new ResourceBlueprint
        val y = new ResourceBlueprint
        x.name = "111"
        y.name = "222"
        x.capacity = 4
        y.capacity = 2
        x.availability = new ResourceSchedule(110)
        y.availability = new ResourceSchedule(110)
        x.availability.set(0, 5, 2)
        x.availability.set(1, 5, 3)
        y.availability.set(0, 2, 5)
        y.availability.set(1, 4, 3)

        writer.writeResourceBlueprints(List(x,y))

        val reader = new SRFImporter(new File("jajayes.txt"))

        reader.getResourceBlueprints.get.foreach(r => println(r.name + " " + r.capacity + " " + r.availability.getMax))
    }
}

class SRFImporter(file: File) extends ResourceImporter {

    override def getResourceBlueprints: Try[Iterable[ResourceBlueprint]] = {
        Try(new ObjectMapper().readValue(file, classOf[Array[ResourceBlueprint]]))
    }

    def writeResourceBlueprints(rbi: Iterable[ResourceBlueprint]): Try[_] = {
        Try(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(file, rbi.toArray))
    }
}
