package file.imprt

import java.io.File
import java.util.regex.Pattern

import app.AppSettings
import file.imprt.blueprint.{EventBlueprint, ResourceBlueprint, SubjectBlueprint}
import service.ReadOnlyAppDatabase

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/** MCF VERSION 1.1 */

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
        val rawLines: Iterator[String] = try source.getLines() finally source.close()

        val lines = rawLines.zipWithIndex.map{ case (s, i) => (i, s.split(Separator)) }.toList //all file lines
        val headers = lines.head //only headers line

        val lineImportJobs = for ((row, line) <- lines.tail) yield readLine(row+1, line, headers._2)

        importJob = flattenImportJobs(lineImportJobs)

        this
    }

    //this method basically imports a subject
    private def readLine(row: Int, values: Array[String], headers: Array[String]): LineImportJob ={
        val errors = new ArrayBuffer[ImportError]()

        val (shortName, shortNameErrors, emptyShortName) = getShortName(values.apply(0).trim, row, 1, headers.apply(0))
        if(shortNameErrors.nonEmpty) errors ++= shortNameErrors

        val (name, nameErrors, emptyName) = getName(values.apply(1).trim, row, 2, headers.apply(1))
        if(nameErrors.nonEmpty) errors ++= nameErrors

        val (course, courseErrors, emptyCourse) = getCourse(values.apply(2).trim, row, 3, headers.apply(2))
        if(courseErrors.nonEmpty) errors ++= courseErrors

        val (semester, semesterErrors, emptySemester) = getSemester(values.apply(3).trim, row, 4, headers.apply(3))
        if(semesterErrors.nonEmpty) errors ++= semesterErrors

        val (hgg, hggErrors, emptyHgg) = getHg(values.apply(4).trim, row, 5, headers.apply(4))
        if(hggErrors.nonEmpty) errors ++= hggErrors

        val (hgm, hgmErrors, emptyHgm) = getHg(values.apply(5).trim, row, 6, headers.apply(5))
        if(hgmErrors.nonEmpty) errors ++= hgmErrors

        val (hgp, hgpErrors, emptyHgp) = getHg(values.apply(6).trim, row, 7, headers.apply(6))
        if(hgpErrors.nonEmpty) errors ++= hgpErrors

        //TODO ngg
        //TODO ngm
        //TODO ngp
        //TODO labs._1
        //TODO labs._2
        //TODO additional info

        //TODO create entities

        LineImportJob(null,List(),null,List(), finished = false) //TODO finish this
    }

    //returns (shortName, errors, shortName.isEmpty)
    def getShortName(rawShortName: String, row: Int, field: Int, header: String): (String, ArrayBuffer[ImportError], Boolean) = {
        val errors = new ArrayBuffer[ImportError]()
        var isEmpty = false

        if (rawShortName.isEmpty) {
            errors += EmptyShortNameError(row, field, header, rawShortName)
            isEmpty = true
        }

        (rawShortName, errors, isEmpty) //no processing on the field is required.
    }

    //returns (name, errors, name.isEmpty)
    def getName(rawName: String, row: Int, field: Int, header: String): (String, ArrayBuffer[ImportError], Boolean) = {
        val errors = new ArrayBuffer[ImportError]()
        var isEmpty = false

        if (rawName.isEmpty) {
            errors += EmptyNameError(row, field, header, rawName)
            isEmpty = true
        }

        (rawName, errors, isEmpty) //no processing on the field is required.
    }

    //returns (course, errors, course.isEmpty)
    def getCourse(rawCourse: String, row: Int, field: Int, header: String): (String, ArrayBuffer[ImportError], Boolean) = {
        val errors = new ArrayBuffer[ImportError]()
        var isEmpty = false

        if (rawCourse.isEmpty) {
            errors += EmptyCourseError(row, field, header, rawCourse)
            isEmpty = true
        }
        //TODO check if course exists

        (rawCourse, errors, isEmpty) //no processing on the field is required.
    }

    //returns (semester, errors, semester.isEmpty)
    def getSemester(rawSemester: String, row: Int, field: Int, header: String): (Int, ArrayBuffer[ImportError], Boolean) = {
        val errors = new ArrayBuffer[ImportError]()
        var isEmpty = false

        val semester = if (rawSemester.isEmpty) {
            errors += EmptySemesterError(row, field, header, rawSemester)
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
