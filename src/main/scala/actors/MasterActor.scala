package actors

import actors.Messages.MiniZincMessages.MiniZincSolveRequest
import actors.Messages.{NoSolution, Solution, SolveRequest, Stop}
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import model.{FirstQuarter, SecondQuarter}
import solver.{InstanceData, MiniZincInstance}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class MasterActor extends Actor{

    var childSolver: ActorRef = _

    def getResponseFromFuture(future: Future[Any], timeout: Timeout): Option[Try[_]] ={
        var response: Option[Try[_]] = None

        try{
            response = Await.ready(future, timeout.duration).value //thread blocks here
        } catch{
            case te: java.util.concurrent.TimeoutException => {
                response = None
            }
        }

        response
    }

    override def receive: PartialFunction[Any, Unit] = {
        case SolveRequest(data, time) => {

            //FIXME this should be done outside master or modeled on MiniZinc
            val firstQuarterEvents = data.events.filter(e => e.quarter.nonEmpty && e.quarter.get == FirstQuarter)
            val secondQuarterEvents = data.events.filter(e => e.quarter.nonEmpty && e.quarter.get == SecondQuarter)

            val firstQuarterInstance = InstanceData(
                data.nDays, data.dayDuration,
                data.nResources, data.resources,
                firstQuarterEvents.length, firstQuarterEvents)
            val secondQuarterInstance = InstanceData(
                data.nDays, data.dayDuration,
                data.nResources, data.resources,
                secondQuarterEvents.length, secondQuarterEvents)

            val firstQuarterSolver = context.system.actorOf(Props(new MiniZincInstanceSolver))
            val secondQuarterSolver = context.system.actorOf(Props(new MiniZincInstanceSolver))

            implicit val timeout: Timeout = new Timeout(time seconds)

            val firstQuarterFuture = firstQuarterSolver ? MiniZincSolveRequest(MiniZincInstance.fromInstanceData(firstQuarterInstance),"1")
            //master thread stops here
            val response1 = getResponseFromFuture(firstQuarterFuture, timeout)
            //Fixed race condition making the calls synchronous. TODO redesign this code
            val secondQuarterFuture = secondQuarterSolver ? MiniZincSolveRequest(MiniZincInstance.fromInstanceData(secondQuarterInstance),"2")
            //master thread stops here
            val response2 = getResponseFromFuture(secondQuarterFuture, timeout)

            if(response1.get.isSuccess && response2.get.isSuccess){
                val solution = response1.get match{
                    case Success(Solution(assignments1)) =>
                        response2.get match {
                            case Success(Solution(assignments2)) =>
                                Solution(assignments1 ++ assignments2)
                            case Success(NoSolution) => NoSolution
                            case _ =>
                        }
                    case Success(NoSolution) => NoSolution
                    case _ =>
                }

                solution match{
                    case NoSolution =>
                        println("no solution")
                    case _ =>
                        //println(assignments)
                }

                sender ! Some(solution)
            } else {
                //FIXME horrendous code, delete this pls. Future me: don't worry, I'll do.
                if(response1.get.isFailure) response1 match {
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
                else response2 match {
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
            }
            /*
            response1 match {
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
            }*/

            context.stop(firstQuarterSolver) //danger
            context.stop(secondQuarterSolver) //danger

            /************* ALTERNATIVES *************/

            /*
            future.onComplete{
                case Success(result: Solution) => sender ! Some(result)
                case Success(_) =>
                    println("Solver Actor error")
                    sender ! Some(Failure(new Exception))
                case Failure(error: TimeoutException) =>
                    println("Timeout")
                    sender ! None
                case Failure(x) =>
                    println("Failure")
                    sender ! Some(Failure(x))
                case x: Any =>
                    println("Unknown Error" + x.toString)
            }*/

            /*
            val timer = after(duration = time seconds, using = context.system.scheduler)(
                Future.failed(new TimeoutException("Solver didn't finish on time"))
            )

            val r = Future.firstCompletedOf(Seq(future, timer))

            //TODO improve messages to master actor
            r.onComplete{
                case Success(result: Solution) => sender ! Some(result)
                case Success(_) =>
                    println("Solver Actor error")
                    sender ! Some(Failure(new Exception))
                case Failure(error: TimeoutException) =>
                    println("Timeout")
                    sender ! None
                case Failure(x) =>
                    println("Failure")
                    sender ! Some(Failure(x))
                case x: Any =>
                    println("Unknown Error" + x.toString)
            }
            */


        }
        case Stop => {
            //TODO improve solver stop
            if(childSolver != null) context.stop(childSolver)
        }
        case _ =>
    }
}
