package actors

import actors.Messages.{SolveRequest, Stop}
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class MasterActor extends Actor{

    var childSolver: ActorRef = null

    override def receive: PartialFunction[Any, Unit] = {
        case SolveRequest(data, time) => {
            childSolver = context.system.actorOf(Props(new MiniZincInstanceSolver))

            implicit val timeout = new Timeout(time seconds)
            val future = childSolver ? SolveRequest(data, time)

            val response: Option[Try[_]] = Await.ready(future, timeout.duration).value //master blocks here

            response match {
                case Some(Success(result)) =>{
                    //TODO process result solution/none
                }
                case Some(Failure(_)) => {
                    //TODO timeout expired
                }
                case None => //unknown error
            }
        }
        case Stop => {
            if(childSolver != null) context.stop(childSolver)
            println("Stopped")
        }
        case _ =>
    }
}
