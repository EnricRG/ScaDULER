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
