package file.imprt

import java.io.File

import app.AppSettings
import file.imprt.blueprint.{EventBlueprint, ResourceBlueprint, SubjectBlueprint}
import service.ReadOnlyAppDatabase

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/** MCF VERSION 1.1 */

object test extends App{
    override def main(args: Array[String]): Unit = {
        val reader = new MCFImportReader(new File("test.mcf"), null)
        reader.read
    }
}

case class LineImportJob(subject: SubjectBlueprint,
                         events: List[EventBlueprint],
                         resource: ResourceBlueprint,
                         errors: List[ImportError],
                         finished: Boolean)

class MCFImportReader(file: File, database: ReadOnlyAppDatabase) extends ImportReader {

    val MCFVersion: String = "1.1"
    val Separator: Char = ';'

    private var importJob: ImportJob = _

    override def read: MCFImportReader = {
        val source = Source.fromFile(file)

        try {
            val lines = source.getLines().zipWithIndex.map { case (s, i) => (i, s.split(Separator.toString, -1)) }.toList //all file lines
            val headers = lines.head //only headers line
            val lineImportJobs = for ((row, line) <- lines.tail) yield readLine(row + 1, line, headers._2)

            importJob = flattenImportJobs(lineImportJobs)
        }
        finally source.close()

        this
    }

    //this method basically imports a subject
    private def readLine(row: Int, values: Array[String], headers: Array[String]): LineImportJob ={
        val errors = new ArrayBuffer[ImportError]()

        val (shortName, shortNameErrors, emptyShortName) = getStringField(values.apply(0).trim, row, 1, headers.apply(0))
        if(shortNameErrors.nonEmpty) errors ++= shortNameErrors

        val (name, nameErrors, emptyName) = getStringField(values.apply(1).trim, row, 2, headers.apply(1))
        if(nameErrors.nonEmpty) errors ++= nameErrors

        val (course, courseErrors, emptyCourse) = getStringField(values.apply(2).trim, row, 3, headers.apply(2))
        if(courseErrors.nonEmpty) errors ++= courseErrors

        val (semester, semesterErrors, emptySemester) = getSemester(values.apply(3).trim, row, 4, headers.apply(3))
        if(semesterErrors.nonEmpty) errors ++= semesterErrors

        val (hgg, hggErrors, emptyHgg) = getHg(values.apply(4).trim, row, 5, headers.apply(4))
        if(hggErrors.nonEmpty) errors ++= hggErrors

        val (hgm, hgmErrors, emptyHgm) = getHg(values.apply(5).trim, row, 6, headers.apply(5))
        if(hgmErrors.nonEmpty) errors ++= hgmErrors

        val (hgp, hgpErrors, emptyHgp) = getHg(values.apply(6).trim, row, 7, headers.apply(6))
        if(hgpErrors.nonEmpty) errors ++= hgpErrors

        val (ngg, nggErrors, emptyNgg) = getIntegerField(values.apply(7).trim, row, 8, headers.apply(7))
        if(nggErrors.nonEmpty && !emptyHgg) errors ++= nggErrors

        val (ngm, ngmErrors, emptyNgm) = getIntegerField(values.apply(8).trim, row, 9, headers.apply(8))
        if(ngmErrors.nonEmpty && !emptyHgm) errors ++= ngmErrors

        val (ngp, ngpErrors, emptyNgp) = getIntegerField(values.apply(9).trim, row, 10, headers.apply(9))
        if(ngpErrors.nonEmpty && !emptyHgp) errors ++= ngpErrors

        val (resourceCapacity, rCErrors, emptyRC) = getIntegerField(values.apply(10).trim, row, 11, headers.apply(10)+"(1)")
        if(rCErrors.nonEmpty) errors ++= rCErrors

        val (resourceName, rNErrors, emptyRN) = getStringField(values.apply(11).trim, row, 12, headers.apply(11)+"(2)")
        if(rNErrors.nonEmpty) errors ++= rNErrors

        val students = getUncheckedIntegerField(values.apply(12).trim)
        val credits = getUncheckedIntegerField(values.apply(13).trim)
        val cgg = getUncheckedDoubleField(values.apply(14).trim)
        val cgm = getUncheckedDoubleField(values.apply(15).trim)
        val cgp = getUncheckedDoubleField(values.apply(16).trim)
        val shared = values.apply(17).trim

        println(
            List(shortName, name, course, semester, hgg, hgm, hgp, ngg, ngm, ngp,
                resourceCapacity, resourceName, students, credits, cgg, cgm, cgp, if(shared.isEmpty) "EMPTY" else shared).mkString(", ")
        )
        println("Errors: " + errors.length)
        errors.foreach(e => println(e.message))

        //TODO check semantic errors
        //TODO - check if course exists

        //TODO create entities

        LineImportJob(null,List(),null,List(), finished = false) //TODO finish this
    }

    //returns (processedString, errors, string.isEmpty)
    def getStringField(rawString: String, row: Int, field: Int, header: String): (String, ArrayBuffer[ImportError], Boolean) = {
        val errors = new ArrayBuffer[ImportError]()
        var isEmpty = false

        if (rawString.isEmpty) {
            errors += EmptyFieldError(row, field, header, rawString)
            isEmpty = true
        }

        (rawString, errors, isEmpty) //no processing on the field is required.
    }

    //returns (semester, errors, semester.isEmpty)
    def getSemester(rawSemester: String, row: Int, field: Int, header: String): (Int, ArrayBuffer[ImportError], Boolean) = {
        val errors = new ArrayBuffer[ImportError]()
        var isEmpty = false

        val semester = if (rawSemester.isEmpty) {
            errors += EmptyFieldError(row, field, header, rawSemester)
            isEmpty = true
            1
        }
        else{
            try{
                val intSemester = rawSemester.toInt
                if(intSemester < 1 || intSemester > 2) {
                    errors += WrongSemesterNumberError(row, field, header, rawSemester)
                    isEmpty = true
                    1
                }
                else intSemester
            }
            catch{
                case e: NumberFormatException => {
                    errors += NumberFormatError(row, field, header, rawSemester)
                    isEmpty = true
                }
                1
            }
        }

        (semester, errors, isEmpty) //no processing on the field is required.
    }

    //returns (hg values, errors, hgg.isEffectivelyEmpty)
    def getHg(rawHg: String, row: Int, fieldNumber: Int, header: String): (List[(Int, Int)], List[ImportError], Boolean) = {
        if (rawHg.isEmpty)
            (List(), List(), true)
        else{
            val parsedHgg = parseHGField(rawHg, row, 5, header)
            if(parsedHgg._2.nonEmpty)
                (List(), parsedHgg._2, true)
            else
                (parsedHgg._1, List(), false)
        }
    }

    // returns pairs of (eventDuration, eventPeriodicity) where
    // - eventDuration is in intervals
    // - eventPeriodicity can be 1 (weekly) or 2 (every two weeks)
    def parseHGField(hg: String, row: Int, field: Int, header: String): (List[(Int, Int)], List[ImportError]) = {
        val errors = new ArrayBuffer[ImportError]
        val temporalSplits = hg.split('/').map(s => (s.take(s.length-1), s.last))

        val splits = for((durationText, periodicityChar) <- temporalSplits) yield{
            val periodicity = periodicityChar match {
                case 's' | 'S' => 1
                case 'q' | 'Q' => 2
                case _ =>
                    errors += UnexpectedCharacterError(row, field, header, periodicityChar, "s S q Q")
                    1
            }

            val duration = try{
                val intDuration = (durationText.toDouble*AppSettings.TimeSlotsPerHour).toInt

                if(intDuration < 1 || intDuration > AppSettings.maxEventDuration) {
                    errors += OutOfRangeEventDurationError(row, field, header, durationText)
                    1
                }
                else intDuration
            }
            catch {
                case e: NumberFormatException => {
                    errors += NumberFormatError(row, field, header, hg)
                }
                1
            }

            (duration, periodicity)
        }

        (splits.toList, errors.toList)
    }

    def getIntegerField(rawNumber: String, row: Int, field: Int, header: String): (Int, List[ImportError], Boolean) = {
        val errors = new ArrayBuffer[ImportError]
        var isEmpty = false

        val number = try{
            val intNumber = rawNumber.toInt
            if(intNumber < 1) {
                errors += NumberOutOfRangeError(row, field, header, rawNumber, 1)
                isEmpty = true
                1
            }
            else intNumber
        }
        catch{
            case e: NumberFormatException => {
                errors += NumberFormatError(row, field, header, rawNumber)
                isEmpty = true
            }
            1
        }

        (number, errors.toList, isEmpty)
    }

    def getUncheckedIntegerField(rawNumber: String): Int = {
        getUncheckedDoubleField(rawNumber).toInt
    }

    def getUncheckedDoubleField(rawNumber: String): Double = {
        try{
            val intNumber = rawNumber.toDouble
            if(intNumber < 0) -1
            else intNumber
        }
        catch{
            case e: NumberFormatException => -2
            case _: Throwable => -3
        }
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

        ImportJob(subjects.toList, events.toList, resources.toList, List(), errors.toList, finished = true)
    }

    override def getImportJob: ImportJob = importJob
}
