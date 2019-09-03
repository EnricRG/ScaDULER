package actors

import actors.Messages.MiniZincMessages.MiniZincSolveRequest
import actors.Messages.{NoSolution, Solution, SolveRequest, Stop}
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import solver.MiniZincInstance

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class MasterActor extends Actor{

    var childSolver: ActorRef = null

    override def receive: PartialFunction[Any, Unit] = {
        case SolveRequest(data, time) => {
            childSolver = context.system.actorOf(Props(new MiniZincInstanceSolver))

            implicit val timeout: Timeout = new Timeout(time seconds)
            val future = childSolver ? MiniZincSolveRequest(MiniZincInstance.fromInstanceData(data))

            var response: Option[Try[_]] = None

            try{
                response = Await.ready(future, timeout.duration).value //master blocks here
            } catch{
                case te: java.util.concurrent.TimeoutException => {
                    response = None
                }
            }

            response match {
                case Some(Success(Solution(assignments))) =>{ //solution found
                    //println(assignments)
                    sender ! Some(Solution(assignments))
                }
                case Some(Success(NoSolution)) =>{ //no solution
                    println("no solution")
                    sender ! Some(NoSolution)
                }
                case Some(Success(None)) =>{
                    println("Solver Actor error")
                    sender ! Some(Failure(new Exception))
                }
                case Some(Failure(x)) => { //process failed
                    println("Failure")
                    sender ! Some(Failure(x))
                }
                case None => //timeout
                    println("Timeout") //unknown error
                    sender ! None
            }

            context.stop(childSolver) //danger
        }
        case Stop => {
            //TODO improve solver stop
            if(childSolver != null) context.stop(childSolver)
        }
        case _ =>
    }
}
