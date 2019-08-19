package actors

import java.io.{File, FileWriter, PrintWriter}

import actors.Messages.MiniZincMessages.MiniZincSolveRequest
import actors.Messages.{NoSolution, Solution}
import akka.actor.Actor
import solver.{EventAssignment, MiniZincConstants}

import scala.sys.process.Process

class NewMiniZincInstanceSolver extends Actor{

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

            val minizinc_process = Process(minizinc_call)

            val assignments = parseMiniZincOutput(minizinc_process.lineStream)

            if(assignments.isDefined) sender ! Solution(assignments.get)
            else sender ! NoSolution
        }
        case _ =>
            println("Error: MiniZincInstanceSolver unknown message")
            sender ! None
    }

    def parseMiniZincOutput(lineStream: Stream[String]): Option[List[EventAssignment]] = {
        lineStream.take(2) match{
            case x if(x.head.contains("---")) => None
            case x => {
                val events = x.head.split(",").map(_.toInt).toList
                val starts = x.last.split(",").map(_.toInt).toList
                val assignmentList = events.zip(starts).map(x => EventAssignment(x._1, x._2))
                Some(assignmentList)
            }
            case _ => {
                println("Error")
                None
            }
        }
    }
}
