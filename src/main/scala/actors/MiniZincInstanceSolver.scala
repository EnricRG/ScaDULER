package actors

import java.io.{File, PrintWriter}

import actors.Messages.MiniZincMessages.MiniZincSolveRequest
import actors.Messages.{NoSolution, Solution}
import akka.actor.Actor
import solver.{EventAssignment, MiniZincConstants, MiniZincInstance}

import scala.sys.process.Process

class MiniZincInstanceSolver extends Actor{

    private def generateMiniZincCall(dataFilePath: String): String = {
        MiniZincConstants.MiniZincPath + " " +
        MiniZincConstants.CommandLineStatisticsOption + " " +
        MiniZincConstants.ChuffedSolver + " " +
        MiniZincConstants.MiniZincModel + " " +
        dataFilePath
    }

    override def receive = {
        case MiniZincSolveRequest(instanceData) => {

            val file = new File(MiniZincConstants.PredefinedDZNFilePath)
            file.getParentFile.mkdirs()

            val writer = new PrintWriter(file)
            try{
                writer.write(instanceData.asDZNString)
            } catch {
                case ioe: java.io.IOException => ioe.printStackTrace()
            } finally {
                writer.close()
            }

            val minizinc_call = generateMiniZincCall(MiniZincConstants.PredefinedDZNFilePath)

            var minizinc_process: scala.sys.process.ProcessBuilder = null

            var success = true

            try{
                minizinc_process = Process(minizinc_call)
            } catch {
                case e: Exception => {
                    success = false
                    sender ! None
                }
            }

            if(success) try {
                val assignments = parseMiniZincOutput(minizinc_process.lineStream)

                if(assignments.isDefined) {
                    val indexDeviation = MiniZincInstance.ModelIndexDeviation

                    //adapt to application indexing
                    val adaptedAssignments = assignments.get
                        .map(x => EventAssignment(x.eventID-indexDeviation, x.interval-indexDeviation))

                    sender ! Solution(adaptedAssignments)
                }
                else sender ! NoSolution
            } catch{
                case e: RuntimeException => sender ! None
            }
        }
        case _ =>
            println("Error: MiniZincInstanceSolver unknown message")
            sender ! None
    }

    def parseMiniZincOutput(lineStream: Stream[String]): Option[List[EventAssignment]] = {
        lineStream.take(2).toList match{
            case x: List[String] if x.head.contains("UNSATISFIABLE") => None
            case x: List[String] if x.head.contains("---") => None
            case x: List[String] => {
                if(x.head.isBlank) None
                else {
                    val events = x.head.split(",").map(_.toInt).toList
                    val starts = x.last.split(",").map(_.toInt).toList
                    val assignmentList = events.zip(starts).map(x => EventAssignment(x._1, x._2))
                    Some(assignmentList)
                }
            }
            case _ => {
                println("Error")
                None
            }
        }
    }
}
