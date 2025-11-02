package actors

import actors.Messages.MiniZincMessages.MiniZincSolveRequest
import actors.Messages.{NoSolution, Solution}
import akka.actor.Actor
import model.Weeks.Week
import solver.{EventAssignment, MiniZincConstants, MiniZincInstance}

import java.io.{File, PrintWriter}
import scala.sys.process.Process

case class MiniZincEventAssignment(eventID: Int, week: Week, interval: Int)

class MiniZincInstanceSolver extends Actor{

    private def generateMiniZincCall(dataFilePath: String): String = {
        MiniZincConstants.MiniZincPath + " " +
        MiniZincConstants.ChuffedSolver + " " +
        MiniZincConstants.MiniZincModel + " " +
        dataFilePath
    }

    override def receive = {
        case MiniZincSolveRequest(instanceData, append) => {

            val file = new File(MiniZincConstants.predefinedDZNFilePath(append))
            file.getParentFile.mkdirs()

            val writer = new PrintWriter(file)
            try{
                writer.write(instanceData.asDZNString)
            } catch {
                case ioe: java.io.IOException => ioe.printStackTrace()
            } finally {
                writer.close()
            }

            val minizinc_call = generateMiniZincCall(MiniZincConstants.predefinedDZNFilePath(append))

            var minizinc_process: scala.sys.process.ProcessBuilder = null

            var success = true

            try{
                minizinc_process = Process(minizinc_call)
            } catch {
                case e: Exception => {
                    System.out.println("Error occurred during MiniZinc execution: " + e.getMessage + "\n")
                    e.printStackTrace()
                    success = false
                    sender ! None
                }
            }

            if(success) try {
                val minizincAssignments = parseMiniZincOutput(minizinc_process.lineStream)

                if(minizincAssignments.isDefined) {
                    val indexDeviation = MiniZincInstance.ModelIndexDeviation

                    //TODO this take() shouldn't be handled here, minizinc should only output assigned values.
                    val assignments = minizincAssignments.get.take(instanceData.nEvents-instanceData.nPreassignedEvents)
                        .map(x => EventAssignment(instanceData.eventMapping(x.eventID), x.week, x.interval-indexDeviation))

                    sender ! Solution(assignments)
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

    def parseMiniZincOutput(lineStream: Stream[String]): Option[List[MiniZincEventAssignment]] = {
        lineStream.head match{
            case line if line.contains("UNSATISFIABLE") => { //no solution
              System.out.println("'UNSATISFIABLE' returned from MiniZinc")
              None
            }
            case line if line.contains("---") => { //unexpected minizinc output
              System.out.println("Unexpected output from MiniZinc: " + line)
              None
            }
            case line if line.trim.isEmpty => { //no minizinc output
              System.out.println("No output from MiniZinc")
              None
            }
            case line if line.contains("SOLUTION") =>
                val solution = lineStream.takeWhile(!_.contains("ENDSOLUTION")).drop(2) //drop 'SOLUTION' and length lines
                val assignments = solution.map(line => line.slice(1, line.length-1).split(","))
                  .map( list => MiniZincEventAssignment(
                            list.apply(0).toInt,
                            MiniZincInstance.reverseParse(list.apply(1)),
                            list.apply(2).toInt
                        )
                  ).toList

                Some(assignments)
            case x => { //unknown error
              System.out.println(x)
              None
            }
        }
    }
}
