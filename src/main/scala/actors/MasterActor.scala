package actors

import actors.Messages.MiniZincMessages.MiniZincSolveRequest
import actors.Messages.{NoSolution, Solution, SolveRequest, Stop}
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import solver.MiniZincInstance

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class MasterActor extends Actor{

    var childSolver: ActorRef = null

    override def receive: PartialFunction[Any, Unit] = {
        case SolveRequest(data, time) => {
            childSolver = context.system.actorOf(Props(new NewMiniZincInstanceSolver))

            implicit val timeout: Timeout = new Timeout(time seconds)
            val future = childSolver ? MiniZincSolveRequest(MiniZincInstance.fromInstanceData(data))

            val response: Option[Try[_]] = Await.ready(future, timeout.duration).value //master blocks here

            response match {
                case Some(Success(Solution(assignments))) =>{
                    println(assignments)
                    //TODO report to main
                }
                case Some(Success(NoSolution)) =>{
                    println("no solution")
                    //TODO report to main
                }
                case Some(Failure(_)) => {
                    println("Failure")
                    //TODO timeout expired report to main
                }
                case None => println("Master: unknown error") //unknown error
            }
        }
        case Stop => {
            if(childSolver != null) context.stop(childSolver)
        }
        case _ =>
    }
}
