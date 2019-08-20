package app

import actors.Messages.{NoSolution, Solution, SolveRequest}
import actors.{MasterActor, Messages}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import service.AppDatabase
import solver.NewInstanceData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.{Failure, Success}
import scala.concurrent.duration._

object test extends App{
    def splitWith[A](list: List[A], predicate: (A,A) => Boolean) = {
        if(list.size < 2) list
        else {
            var result: List[List[A]] = List()
            val indices = list.indices.dropRight(1)
            var firstIndex = indices(0)
            for (i <- indices) {
                if (!predicate(list(i), list(i + 1))) {
                    result = result :+ list.slice(firstIndex, i+1)
                    firstIndex = i+1
                }
                else if(i == indices.last){
                    result = result :+ list.slice(firstIndex, i+2)
                }
            }
            result
        }
    }

    def foldSplit[A](list: List[A], predicate: (A,A) => Boolean) = {
        if(list.size < 2) list
        else {
            list
            .indices //get indices
            .dropRight(1) //remove last index
            .map(x => (list(x),list(x+1))) //map to pairs (i,i+1)
            .partition(predicate.tupled) //
        }
    }

    override def main(args: Array[String]): Unit = {
        println(splitWith(List(1,2), (x: Int, y: Int) => y == x+1))
    }
}

object MainApp extends App {

    private var actorSystem: ActorSystem = _
    private var master: ActorRef = _
    private var database: AppDatabase = _
    private var solving: Boolean = false

    def getDatabase: AppDatabase = database
    def setDatabase(appDatabase: AppDatabase): Unit = database = appDatabase

    override def main(args: Array[String]): Unit = {
        init()
        MainInterface.main(Array())
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
        val instanceData = NewInstanceData(
            AppSettings.days,
            AppSettings.timeSlotsPerDay,
            database.resourceDatabase.getElements.size,
            database.resourceDatabase.getElements.toList,
            database.eventDatabase.getElements.size,
            database.eventDatabase.getElements.toList
        )

        solving = true

        implicit val t: Timeout = new Timeout(timeout+1 seconds)
        val futureResponse = master ? SolveRequest(instanceData, timeout)

        futureResponse.onComplete{
            case Success(value) => value match{ //if master responds us
                case Some(Solution(assignments)) =>{
                    MainApp.notifySolution(Some(Solution(assignments)))
                }
                case Some(NoSolution) =>{
                    MainApp.notifySolution(None)
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

    def notifySolution(solution: Option[Solution]): Unit = {
        solution match {
            case Some(Solution(assignments)) =>{
                //TODO filter preassigned events and auxiliary events
                //TODO assign events
                MainInterface.promptAlert(
                    AppSettings.language.getItem("solver_noSolutionWindowTitle"),
                    AppSettings.language.getItem("solver_noSolutionText")
                )
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
