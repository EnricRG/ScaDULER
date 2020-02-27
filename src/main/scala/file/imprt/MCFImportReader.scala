package file.imprt

import java.io.File
import java.util.regex.Pattern

import app.AppSettings
import file.imprt.blueprint.{EventBlueprint, ResourceBlueprint, SubjectBlueprint}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/** MCF VERSION 1.1 */

case class LineImportJob(subject: SubjectBlueprint,
                         events: List[EventBlueprint],
                         resource: ResourceBlueprint,
                         errors: List[ImportError],
                         finished: Boolean)

class MCFImportReader(file: File) extends ImportReader {

    val MCFVersion: String = "1.1"
    val Separator: Char = ';'
    //private val HgPattern = Pattern.compile("")

    private var importJob: ImportJob = _

    override def read: MCFImportReader = {
        val source = Source.fromFile(file)
        val rawLines: Iterator[String] = try source.getLines() finally source.close()

        val lines = rawLines.zipWithIndex.map{ case (s, i) => (i, s.split(Separator)) }.toList //all file lines
        val headers = lines.head //only headers line

        val lineImportJobs = for ((index, line) <- lines.tail) yield readLine(index, line, headers._2)

        importJob = flattenImportJobs(lineImportJobs)

        this
    }

    //this method basically imports a subject
    private def readLine(row: Int, values: Array[String], headers: Array[String]): LineImportJob ={
        val errors = new ArrayBuffer[ImportError]()

        //var hasShortName: Boolean = _

        val shortName = values.apply(0).trim
        if (shortName.isEmpty) errors += EmptyShortNameError(row+1, 1, headers.apply(0), shortName)
        //else hasShortName = true

        val name = values.apply(1).trim
        if (shortName.isEmpty) errors += EmptyNameError(row+1, 2, headers.apply(1), name)

        val course = values.apply(2).trim
        if (course.isEmpty) errors += EmptyCourseError(row+1, 3, headers.apply(2), course)

        val semester = values.apply(3).trim
        if (semester.isEmpty) errors += EmptySemesterError(row+1, 4, headers.apply(3), semester)

        val hgg = values.apply(4).trim
        var emptyHgg = false

        if (hgg.isEmpty) emptyHgg = true
        else{

        }

        LineImportJob(null,List(),null,List(), finished = false) //TODO finish this
    }

    // returns pairs of (eventDuration, eventPeriodicity) where
    // - eventDuration is in intervals
    // - eventPeriodicity can be 1 (weekly) or 2 (every two weeks)
    def parseHGField(fieldNumber: Int, hg: String, row: Int, headers: List[String]): (List[(Int, Int)], List[ImportError]) = {
        val errors = new ArrayBuffer[ImportError]
        val temporalSplits = hg.split('/').map(s => (s.take(s.length-1), s.last))

        val splits = for((durationText, periodicityChar) <- temporalSplits) yield{
            val periodicity = periodicityChar match {
                case 's' || 'S' => 1
                case 'q' || 'Q' => 2
                case _ =>
                    errors += UnexpectedCharacterError(row + 1, fieldNumber, headers.apply(fieldNumber-1),
                        periodicityChar, "s S q Q")
                    1
            }

            val duration = try{
                (durationText.toDouble*AppSettings.TimeSlotsPerHour).toInt
            } catch {
                case e: NumberFormatException => {
                    errors += NumberFormatError(row + 1, fieldNumber, headers.apply(fieldNumber-1), hg)
                }
                1
            }

            (duration, periodicity)
        }

        (splits.toList, errors.toList)
    }

    def flattenImportJobs(lineImportJobs: List[LineImportJob]): ImportJob = {
        val subjects: ArrayBuffer[SubjectBlueprint] = new mutable.ArrayBuffer
        val events: ArrayBuffer[EventBlueprint] = new mutable.ArrayBuffer
        val resources: ArrayBuffer[ResourceBlueprint] = new mutable.ArrayBuffer
        val errors: ArrayBuffer[ImportError] = new mutable.ArrayBuffer

        lineImportJobs.foreach(lij => {
            subjects += lij.subject
            events ++= lij.events
            resources += lij.resource
            errors ++= lij.errors
        })

        ImportJob(subjects.toList, events.toList, resources.toList, List(), errors.toList)
    }

    override def getImportJob: ImportJob = importJob
}
