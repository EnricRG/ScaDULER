package file.imprt

import java.io.File

import file.imprt.blueprint.{EventBlueprint, ResourceBlueprint, SubjectBlueprint}

import scala.io.Source

/** MCF VERSION 1.1 */

case class LineImportJob(subject: SubjectBlueprint,
                         events: List[EventBlueprint],
                         resource: ResourceBlueprint,
                         errors: List[ImportError])

class MCFReader(file: File) extends ImportReader {

    val MCFVersion: String = "1.1"
    val Separator: Char = ';'

    private var importJob: ImportJob = _

    override def read: MCFReader = {
        val source = Source.fromFile(file)
        val rawLines: Iterator[String] = try source.getLines() finally source.close()

        val lines = rawLines.zipWithIndex.map{ case (s, i) => (i, s.split(Separator)) }.toList
        val headers = lines.head

        for((index, line) <- lines.drop(1)){ //skipping headers line
            val lineImportJob = readLine(index, line, headers._2)
        }

        this
    }

    private def readLine(index: Int, values: Array[String], headers: Array[String]): LineImportJob ={
        //for every value/header
        //  check errors and set variables

        LineImportJob(null,List(),null,List()) //TODO finish this
    }

    override def getImportJob: ImportJob = importJob
}
