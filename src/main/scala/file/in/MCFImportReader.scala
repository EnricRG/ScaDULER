package file.in

import java.io.File

import app.AppSettings
import model.blueprint.{CourseBlueprint, EventBlueprint, ResourceBlueprint, SubjectBlueprint}
import model._
import service.ReadOnlyAppDatabase

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/** MCF VERSION 1.1 */

case class LineImportJob(subject: SubjectBlueprint,
                         events: List[EventBlueprint],
                         course: CourseBlueprint,
                         resource: Option[ResourceBlueprint],
                         errors: List[ImportError],
                         finished: Boolean)

object MCFImportReader{
    val MCFFileExtension: String = "mcf"
    val MCFVersion: String = "1.1"
    val Separator: Char = ';'
}

class MCFImportReader(file: File, database: ReadOnlyAppDatabase) extends ImportReader {

    lazy val createdResources: mutable.HashMap[String, ResourceBlueprint] = new mutable.HashMap
    lazy val createdCourses: mutable.HashMap[String, CourseBlueprint] = new mutable.HashMap

    private var importJob: ImportJob = _

    override def read: MCFImportReader = {
        val source = Source.fromFile(file)

        try {
            val filteredLines = source.getLines().filterNot(l => l.forall(_ == MCFImportReader.Separator) /*|| l.trim.isEmpty*/)
            val lines = filteredLines.zipWithIndex.map { case (s, i) => (i, s.split(MCFImportReader.Separator.toString, -1)) }.toList //all file lines
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
        if(rCErrors.nonEmpty && !emptyHgp) errors ++= rCErrors

        val (resourceName, rNErrors, emptyRN) = getStringField(values.apply(11).trim, row, 12, headers.apply(11)+"(2)")
        if(rNErrors.nonEmpty && !emptyHgp) errors ++= rNErrors

        val students = getUncheckedIntegerField(values.apply(12).trim)
        val credits = getUncheckedIntegerField(values.apply(13).trim)
        val cgg = getUncheckedDoubleField(values.apply(14).trim)
        val cgm = getUncheckedDoubleField(values.apply(15).trim)
        val cgp = getUncheckedDoubleField(values.apply(16).trim)
        val shared = values.apply(17).trim

        if(emptyShortName || emptyName || emptyCourse || emptySemester) {
            LineImportJob(null, List(), null, null, errors.toList, finished = false)
        }
        /*else if(database.subjectDatabase.getSubjectByName(name).isEmpty) {
            //TODO check if subject already exists. This shouldn't be done because application allows repeated subjects.
            LineImportJob(null, List(), null, null, errors.toList, finished = false)
        }*/
        else{
            val subjectEntity = new SubjectBlueprint
            subjectEntity.shortName = shortName
            subjectEntity.name = name

            val courseEntity = createdCourses.get(course) match {
                case Some(c) => c
                case None =>
                    val newCourse = new CourseBlueprint
                    newCourse.name = course
                    createdCourses.put(newCourse.name, newCourse)
                    newCourse
            }
            subjectEntity.course = courseEntity
            subjectEntity.quarter = semester

            subjectEntity.additionalInformation.update("students", students)
            subjectEntity.additionalInformation.update("credits", credits)
            subjectEntity.additionalInformation.update("cgg", cgg)
            subjectEntity.additionalInformation.update("cgm", cgm)
            subjectEntity.additionalInformation.update("cgp", cgp)
            subjectEntity.additionalInformation.update("shared", shared)

            val resourceEntity = if (emptyHgp) None else Some(createdResources.get(resourceName) match {
                case Some(r) => r
                case None =>
                    val newResource = new ResourceBlueprint
                    newResource.name = resourceName + "_" + values.apply(3)
                    newResource.quantity = 1 //TODO remove resource quantity
                    newResource.capacity = resourceCapacity
                    createdResources.put(newResource.name, newResource)
                    newResource
            })

            //TODO collapse all cases to single line (the one inside first if statement).
            /*val theoryEvent = (1 to ngg).flatMap(n => hgg.indices.flatMap(number =>
                generateEvents(subjectEntity, TheoryEvent, hgg.apply(number)._1, hgg.apply(number)._2, None, n, n))
            ).toList*/
            val theoryEvents: List[EventBlueprint] = if(hgg.length > 1){
                (1 to ngg).flatMap(n => hgg.indices.flatMap(number => generateEvents(subjectEntity, TheoryEvent,
                    hgg.apply(number)._1, hgg.apply(number)._2, None, n, n))).toList
            }
            else if (hgg.isEmpty) List()
            else generateEvents(subjectEntity, TheoryEvent, hgg.head._1, hgg.head._2, None, 1, ngg)

            val problemsEvents: List[EventBlueprint] = if(hgm.length > 1){
                (1 to ngm).flatMap(n => hgm.indices.flatMap(number => generateEvents(subjectEntity, ProblemsEvent,
                    hgm.apply(number)._1, hgm.apply(number)._2, None, number+1, number+1))).toList
            }
            else if (hgm.isEmpty) List()
            else generateEvents(subjectEntity, ProblemsEvent, hgm.head._1, hgm.head._2, None, 1, ngm)

            val labEvents: List[EventBlueprint] = if(hgp.length > 1){
                (1 to ngp).flatMap(n => hgp.indices.flatMap(number => generateEvents(subjectEntity, LaboratoryEvent,
                    hgp.apply(number)._1, hgp.apply(number)._2, resourceEntity, number+1, number+1))).toList
            }
            else if (hgp.isEmpty) List()
            else generateEvents(subjectEntity, LaboratoryEvent, hgp.head._1, hgp.head._2, resourceEntity, 1, ngp)

            subjectEntity.events ++= (theoryEvents ++ problemsEvents ++ labEvents)

            LineImportJob(subjectEntity, subjectEntity.events.toList, courseEntity, resourceEntity, errors.toList, finished = true)
        }
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
    def getSemester(rawSemester: String, row: Int, field: Int, header: String): (Quarter, ArrayBuffer[ImportError], Boolean) = {
        val errors = new ArrayBuffer[ImportError]()
        var isEmpty = false

        val semester = if (rawSemester.isEmpty) {
            errors += EmptyFieldError(row, field, header, rawSemester)
            isEmpty = true
            FirstQuarter
        }
        else{
            try{
                val intSemester = rawSemester.toInt
                intSemester match{
                    case 1 => FirstQuarter
                    case 2 => SecondQuarter
                    case _ =>
                        errors += WrongSemesterNumberError(row, field, header, rawSemester)
                        isEmpty = true
                        NoQuarter
                }
            }
            catch{
                case e: NumberFormatException => {
                    errors += NumberFormatError(row, field, header, rawSemester)
                    isEmpty = true
                }
                FirstQuarter
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
                val intDuration = (durationText.replace(",", ".").toDouble
                                    * AppSettings.TimeSlotsPerHour).toInt

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

    def generateEvents(subject: SubjectBlueprint, eventType: EventType, duration: Int, periodicityInt: Int,
                       resource: Option[ResourceBlueprint], start: Int, end: Int): List[EventBlueprint] = {
        if(start > 0 && end-start >= 0){
            val events = new ArrayBuffer[EventBlueprint]
            (start to end).foreach(number => {
                val event = new EventBlueprint

                val periodicity = Weeks.Periodicity.fromInt(periodicityInt)

                event.name = "%s (%s-%d) (%s)".format(subject.name, eventType.toString, number, periodicity.toShortString)
                event.shortName = "%s (%s %d) (%s)".format(subject.shortName, eventType.toShortString, number, periodicity.toShortString)
                event.neededResource = resource
                event.eventType = eventType
                event.subject = Some(subject)
                event.periodicity = periodicity
                event.duration = duration
                event.course = subject.course
                event.quarter = subject.quarter

                events += event
            })
            events.toList
        }
        else List()
    }

    def flattenImportJobs(lineImportJobs: List[LineImportJob]): ImportJob = {
        val subjects: ArrayBuffer[SubjectBlueprint] = new mutable.ArrayBuffer
        val events: ArrayBuffer[EventBlueprint] = new mutable.ArrayBuffer
        //val courses: ArrayBuffer[CourseBlueprint] = new mutable.ArrayBuffer
        val resources: ArrayBuffer[ResourceBlueprint] = new mutable.ArrayBuffer
        val errors: ArrayBuffer[ImportError] = new mutable.ArrayBuffer

        //TODO improvable, could be created at start and add all event when reading lines, improves performance.
        //Auxiliary resource to model theory room occupation
        val theoryResource = new ResourceBlueprint
        theoryResource.name = "Aula Teoria/Problemes"
        theoryResource.quantity = 1 //TODO temove resource quantity
        //TODO theoryResource.capacity = ?
        resources += theoryResource

        resources ++= createdResources.values

        lineImportJobs.foreach(lij => if (lij.finished) {
            subjects += lij.subject
            events ++= lij.events
            //if (courses.forall(!_.name.equals(lij.course.name))) courses += lij.course
            //if (resources.forall(!_.name.equals(lij.resource.name))) resources += lij.resource
            errors ++= lij.errors
        })

        events.foreach(e => if(e.eventType == TheoryEvent || e.eventType == ProblemsEvent) e.neededResource = Some(theoryResource))

        ImportJob(subjects.toList, events.toList,
            resources.toList, createdCourses.values.toList,
            errors.toList, finished = true)
    }

    override def getImportJob: ImportJob = importJob
}
