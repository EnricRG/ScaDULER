package app

import actors.{MasterActor, Messages}
import akka.actor.{ActorRef, ActorSystem, Props}
import service.AppDatabase

object MainApp extends App {

    private var actorSystem: ActorSystem = _
    private var master: ActorRef = _
    private var database: AppDatabase = _

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
        actorSystem.stop(master) //FIXME maybe error
        actorSystem.terminate()
    }

    def solve(timeout: Double) = {
        //TODO prepare instance data
        //TODO send SolveRequest to master
        //TODO wait for response
        //TODO notify MainController if the solution was found
    }
    def stopSolver() = {
        master ! Messages.Stop
    }
}

/*
    override def main(args: Array[String]): Unit = {

        //
        // app.Instance 1: 1er_s1 (2019-2020)
        //

        //gui.GUI.gui

        val system = ActorSystem("System")

        val minizinc_solver = system.actorOf(Props[MiniZincInstanceSolver])

        implicit val timeout = Timeout(10 seconds)

        val awaited_response = minizinc_solver ? Instance.instance1

        val result: List[String] = Await.result(awaited_response, timeout.duration).asInstanceOf[List[String]]

        for(line <- result) println(line)

        system.stop(minizinc_solver)
        system.terminate

    }


}
*/
