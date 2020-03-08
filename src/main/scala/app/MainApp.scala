package app

import actors.Messages.{NoSolution, Solution, SolveRequest}
import actors.{MasterActor, Messages}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import javafx.application.Application
import service.AppDatabase
import solver.InstanceData

import scala.collection.JavaConverters
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

object MainApp extends App {

    private var actorSystem: ActorSystem = _
    private var master: ActorRef = _
    private var database: AppDatabase = _
    private var solving: Boolean = false

    def getDatabase: AppDatabase = database
    def setDatabase(appDatabase: AppDatabase): Unit = database = appDatabase

    override def main(args: Array[String]): Unit = {
        println("Pre Init")
        init()
        println("Post Init")
        Application.launch(classOf[MainInterface], args: _*)
        exit()
    }

    private def init(): Unit = {
        database = new AppDatabase
        actorSystem = ActorSystem("System")
        master = actorSystem.actorOf(Props(new MasterActor))
    }

    private def exit(): Unit = {
        actorSystem.stop(master) //unnecessary and pops a warning
        actorSystem.terminate()
    }

    def solve(timeout: Double): Unit = {
        val assignableEvents = database.eventDatabase.getElements.filter(_.isAssignable)
        val instanceData = InstanceData(
            AppSettings.days,
            AppSettings.timeSlotsPerDay,
            database.resourceDatabase.getElements.size,
            database.resourceDatabase.getElements.toList,
            assignableEvents.size,
            assignableEvents.toList.sortBy(_.getID)
        )

        solving = true

        implicit val t: Timeout = new Timeout(timeout+1 seconds)
        val futureResponse = master ? SolveRequest(instanceData, timeout)

        futureResponse.onComplete{
            case Success(value) => value match{ //if master responds us
                case Some(Solution(assignments)) =>{
                    MainApp.notifySolution(instanceData, Some(Solution(assignments)))
                }
                case Some(NoSolution) =>{
                    MainApp.notifySolution(instanceData, None)
                }
                case Some(Failure(_)) =>{
                    MainApp.notifySolverError()
                }
                case None => { //timeout
                    MainApp.notifyTimeout()
                }
                case _ => println("Unknown response from Master Actor")
            }
            case Failure(exception) => { //if not
                println("Master Actor error")
                exception.printStackTrace()
            }
        }(JavaFXExecutionContext.javaFxExecutionContext)
    }

    def stopSolver(): Unit = {
        solving = false
        master ! Messages.Stop
    }

    def notifySolution(instanceData: InstanceData, solution: Option[Solution]): Unit = {
        solution match {
            case Some(Solution(assignments)) =>{

                val realAssignments = assignments.take(instanceData.nEvents)
                    .filter(x => {
                        val e = database.eventDatabase.getElement(x.eventID)
                        e.isDefined && !e.get.isAssigned
                    })

                if(realAssignments.isEmpty) MainInterface.promptAlert(
                    AppSettings.language.getItem("noAssignments_windowTitle"),
                    AppSettings.language.getItem("noAssignments_message")
                )
                else{
                    val accepted: Boolean = MainInterface.promptChoice(
                        AppSettings.language.getItem("solver_solutionFoundWindowTitle"),
                        AppSettings.language.getItem("solver_solutionFoundText")
                    )

                    if(accepted) MainInterface.processAssignments(JavaConverters.asJavaCollection(realAssignments))
                }
            }
            case None => {
                MainInterface.promptAlert(
                    AppSettings.language.getItem("solver_noSolutionWindowTitle"),
                    AppSettings.language.getItem("solver_noSolutionText")
                )
            }
        }
        solving = false
    }

    def notifySolverError(): Unit = {
        if(solving) {
            MainInterface.promptAlert(
                AppSettings.language.getItem("solverError_WindowTitle"),
                AppSettings.language.getItem("solverError_solverError")
            )
            solving = false
        }
    }

    def notifyTimeout(): Unit = {
        if(solving) {
            MainInterface.promptAlert(
                AppSettings.language.getItem("timeout_windowTitle"),
                AppSettings.language.getItem("timeout_timeoutMessage")
            )
            solving = false
        }
    }

}
