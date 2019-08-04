package app

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import misc.Weeks._
import model.{Event, InstanceData}
import service.AppDatabase
import solver.MiniZincInstanceSolver

import scala.concurrent.Future.never

//import gui.EventForm

import scala.concurrent.Await
import scala.concurrent.duration._

object Instance {
    val labRooms = 1
    val classRooms = 2
    val pcRooms = 4

    val events = List(
        //Breaks
        /*new model.EventData(1, time_slots = 2, relativeStart = 9),
        new model.EventData(2, time_slots = 2, relativeStart = 31),
        new model.EventData(3, time_slots = 2, relativeStart = 53),
        new model.EventData(4, time_slots = 2, relativeStart = 75),
        new model.EventData(5, time_slots = 2, relativeStart = 97),*/
        new Event(1, time_slots = 4, relativeStart = 9+22*3),
        new Event(2, time_slots = 2),
        new Event(3, time_slots = 2),
        new Event(4, time_slots = 2),
        new Event(5, time_slots = 2),

        //First course

        //misc.Day 1
        new Event(6, time_slots = 4, classRoomsNeeded = 1),
        new Event(7, time_slots = 4, labRoomsNeeded = 1),
        new Event(8, time_slots = 4, pcRoomsNeeded = 1),
        new Event(9, time_slots = 4, pcRoomsNeeded = 1),

        //misc.Day 2
        new Event(10, time_slots = 4, classRoomsNeeded = 1),
        new Event(11, time_slots = 4, classRoomsNeeded = 1),

        new Event(12, time_slots = 4, classRoomsNeeded = 1),
        new Event(13, time_slots = 4, labRoomsNeeded = 1),
        new Event(14, time_slots = 4, pcRoomsNeeded = 1),
        new Event(15, time_slots = 4, classRoomsNeeded = 1),

        //misc.Day 3
        new Event(16, time_slots = 4, classRoomsNeeded = 1),
        new Event(17, time_slots = 2, classRoomsNeeded = 1),
        new Event(18, time_slots = 2, classRoomsNeeded = 1),
        new Event(19, time_slots = 2, classRoomsNeeded = 1),
        new Event(20, time_slots = 2, classRoomsNeeded = 1),
        new Event(21, time_slots = 4, labRoomsNeeded = 1),

        new Event(22, time_slots = 4, classRoomsNeeded = 1),
        new Event(23, time_slots = 4, classRoomsNeeded = 1),
        new Event(24, time_slots = 2, classRoomsNeeded = 1),

        //misc.Day 4
        new Event(25, time_slots = 2, classRoomsNeeded = 1),
        new Event(26, time_slots = 2, classRoomsNeeded = 1),
        new Event(27, time_slots = 4, classRoomsNeeded = 1),

        new Event(28, time_slots = 4, classRoomsNeeded = 1),
        new Event(29, time_slots = 2, classRoomsNeeded = 1),

        //misc.Day 5
        new Event(30, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
        new Event(31, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
        new Event(32, time_slots = 4, labRoomsNeeded = 1),
        new Event(33, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
        new Event(34, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
        new Event(35, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
        new Event(36, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
        new Event(37, time_slots = 4, pcRoomsNeeded = 1),
        new Event(38, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
        new Event(39, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
        new Event(40, time_slots = 4, labRoomsNeeded = 1),
        new Event(41, time_slots = 4, pcRoomsNeeded = 1),
        new Event(42, time_slots = 4, labRoomsNeeded = 1),
        new Event(43, time_slots = 4, pcRoomsNeeded = 1),

        new Event(44, time_slots = 2, classRoomsNeeded = 1),
        new Event(45, time_slots = 2, classRoomsNeeded = 1),
        new Event(46, time_slots = 4, labRoomsNeeded = 1),
        new Event(47, time_slots = 4, pcRoomsNeeded = 1),
        new Event(48, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
        new Event(49, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
        new Event(50, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
        new Event(51, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),

        //Second model.Course

        //misc.Day 1
        new Event(52, time_slots = 4, classRoomsNeeded = 1),
        new Event(53, time_slots = 4, pcRoomsNeeded = 1),
        new Event(54, time_slots = 4, pcRoomsNeeded = 1),
        new Event(55, time_slots = 4, week = BWeek, labRoomsNeeded = 1),

        //misc.Day 2
        new Event(56, time_slots = 4, pcRoomsNeeded = 1),
        new Event(57, time_slots = 4, week = AWeek, labRoomsNeeded = 1),
        new Event(58, time_slots = 4, week = BWeek, labRoomsNeeded = 1),
        new Event(59, time_slots = 4, classRoomsNeeded = 1),
        new Event(60, time_slots = 4, classRoomsNeeded = 1),

        new Event(61, time_slots = 4, pcRoomsNeeded = 1),

        //misc.Day 3
        new Event(62, time_slots = 4, classRoomsNeeded = 1),
        new Event(63, time_slots = 4, classRoomsNeeded = 1),
        new Event(64, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
        new Event(65, time_slots = 4, pcRoomsNeeded = 1),
        new Event(66, time_slots = 4, week = AWeek, labRoomsNeeded = 1),
        new Event(67, time_slots = 4, week = BWeek, labRoomsNeeded = 1),
        /*
                    //misc.Day 4
                    new model.EventData(68, time_slots = 4, pcRoomsNeeded = 1),
                    new model.EventData(69, time_slots = 4, week = misc.AWeek, pcRoomsNeeded = 1),
                    new model.EventData(70, time_slots = 4, week = misc.BWeek, pcRoomsNeeded = 1),
                    new model.EventData(71, time_slots = 4, pcRoomsNeeded = 1),
                    new model.EventData(72, time_slots = 4, pcRoomsNeeded = 1),
                    new model.EventData(73, time_slots = 4, pcRoomsNeeded = 1),

                    //misc.Day 5
                    new model.EventData(74, time_slots = 4, classRoomsNeeded = 1),
                    new model.EventData(75, time_slots = 2, classRoomsNeeded = 1),
                    new model.EventData(76, time_slots = 4, week = misc.AWeek, pcRoomsNeeded = 1),
                    new model.EventData(77, time_slots = 4, week = misc.BWeek, pcRoomsNeeded = 1),
                    new model.EventData(78, time_slots = 4, week = misc.AWeek, classRoomsNeeded = 1),
                    new model.EventData(79, time_slots = 4, week = misc.BWeek, classRoomsNeeded = 1),
                    new model.EventData(80, time_slots = 4, classRoomsNeeded = 1),
                    new model.EventData(81, time_slots = 2, classRoomsNeeded = 1),

                    new model.EventData(82, time_slots = 4, pcRoomsNeeded = 1),*/
    )

    val nEvents = 67

    val preassignedEvents = events.filter(_.relativeStart != 0)

    val precedences = List()

    //Event Exclusions

    def getTheoryIncompatibilities(eventList: List[Event], event: Event) = {
        eventList.filter( _.num != event.num )
    }

    //Teoria: 6,10,11,12,15,16,22,23,25,27,28,44,52,59,60,62,63
    val theory_event_numbers = List(6,10,11,12,15,16,22,23,25,27,28,44,52,59,60,62,63)
    val theory_events = events.filter(x => theory_event_numbers.contains(x.num))

    events(1-1).incompatibilities = events.drop(1)
    //events(2-1).incompatibilities = events.take(1) ++ events.drop(2)
    //events(3-1).incompatibilities = events.take(2) ++ events.drop(3)
    //events(4-1).incompatibilities = events.take(3) ++ events.drop(4)
    //events(5-1).incompatibilities = events.take(4) ++ events.drop(5)

    events(6-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(6))
    events(10-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(10))
    events(11-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(11))
    events(12-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(12))
    events(15-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(15))
    events(16-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(16))
    events(22-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(22))
    events(23-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(23))
    events(25-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(25))
    events(27-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(27))
    events(28-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(28))
    events(44-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(44))
    events(52-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(52))
    events(59-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(59))
    events(60-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(60))
    events(62-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(62))
    events(63-1).incompatibilities = getTheoryIncompatibilities(theory_events, events(63))


    def instance1 = InstanceData(22, labRooms, classRooms, pcRooms, nEvents, events, preassignedEvents, precedences)
}

object test extends App{
    override def main(args: Array[String]): Unit ={
        MainInterface.main(args)
    }
}

object MainApp extends App {

    lazy val database = new AppDatabase

    override def main(args: Array[String]): Unit = {
        MainInterface.main(Array())
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
