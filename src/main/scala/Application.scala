import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.Await

object Instance {
    val labRooms = 1
    val classRooms = 2
    val pcRooms = 4

    val events = List(
        //Breaks
        /*new EventData(1, time_slots = 2, relativeStart = 9),
        new EventData(2, time_slots = 2, relativeStart = 31),
        new EventData(3, time_slots = 2, relativeStart = 53),
        new EventData(4, time_slots = 2, relativeStart = 75),
        new EventData(5, time_slots = 2, relativeStart = 97),*/
        new EventData(1, time_slots = 4, relativeStart = 9+22*3),
        new EventData(2, time_slots = 2),
        new EventData(3, time_slots = 2),
        new EventData(4, time_slots = 2),
        new EventData(5, time_slots = 2),

        //First course

        //Day 1
        new EventData(6, time_slots = 4, classRoomsNeeded = 1),
        new EventData(7, time_slots = 4, labRoomsNeeded = 1),
        new EventData(8, time_slots = 4, pcRoomsNeeded = 1),
        new EventData(9, time_slots = 4, pcRoomsNeeded = 1),

        //Day 2
        new EventData(10, time_slots = 4, classRoomsNeeded = 1),
        new EventData(11, time_slots = 4, classRoomsNeeded = 1),

        new EventData(12, time_slots = 4, classRoomsNeeded = 1),
        new EventData(13, time_slots = 4, labRoomsNeeded = 1),
        new EventData(14, time_slots = 4, pcRoomsNeeded = 1),
        new EventData(15, time_slots = 4, classRoomsNeeded = 1),

        //Day 3
        new EventData(16, time_slots = 4, classRoomsNeeded = 1),
        new EventData(17, time_slots = 2, classRoomsNeeded = 1),
        new EventData(18, time_slots = 2, classRoomsNeeded = 1),
        new EventData(19, time_slots = 2, classRoomsNeeded = 1),
        new EventData(20, time_slots = 2, classRoomsNeeded = 1),
        new EventData(21, time_slots = 4, labRoomsNeeded = 1),

        new EventData(22, time_slots = 4, classRoomsNeeded = 1),
        new EventData(23, time_slots = 4, classRoomsNeeded = 1),
        new EventData(24, time_slots = 2, classRoomsNeeded = 1),

        //Day 4
        new EventData(25, time_slots = 2, classRoomsNeeded = 1),
        new EventData(26, time_slots = 2, classRoomsNeeded = 1),
        new EventData(27, time_slots = 4, classRoomsNeeded = 1),

        new EventData(28, time_slots = 4, classRoomsNeeded = 1),
        new EventData(29, time_slots = 2, classRoomsNeeded = 1),

        //Day 5
        new EventData(30, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
        new EventData(31, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
        new EventData(32, time_slots = 4, labRoomsNeeded = 1),
        new EventData(33, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
        new EventData(34, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
        new EventData(35, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
        new EventData(36, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
        new EventData(37, time_slots = 4, pcRoomsNeeded = 1),
        new EventData(38, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
        new EventData(39, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
        new EventData(40, time_slots = 4, labRoomsNeeded = 1),
        new EventData(41, time_slots = 4, pcRoomsNeeded = 1),
        new EventData(42, time_slots = 4, labRoomsNeeded = 1),
        new EventData(43, time_slots = 4, pcRoomsNeeded = 1),

        new EventData(44, time_slots = 2, classRoomsNeeded = 1),
        new EventData(45, time_slots = 2, classRoomsNeeded = 1),
        new EventData(46, time_slots = 4, labRoomsNeeded = 1),
        new EventData(47, time_slots = 4, pcRoomsNeeded = 1),
        new EventData(48, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
        new EventData(49, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
        new EventData(50, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
        new EventData(51, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),

        //Second Course

        //Day 1
        new EventData(52, time_slots = 4, classRoomsNeeded = 1),
        new EventData(53, time_slots = 4, pcRoomsNeeded = 1),
        new EventData(54, time_slots = 4, pcRoomsNeeded = 1),
        new EventData(55, time_slots = 4, week = BWeek, labRoomsNeeded = 1),

        //Day 2
        new EventData(56, time_slots = 4, pcRoomsNeeded = 1),
        new EventData(57, time_slots = 4, week = AWeek, labRoomsNeeded = 1),
        new EventData(58, time_slots = 4, week = BWeek, labRoomsNeeded = 1),
        new EventData(59, time_slots = 4, classRoomsNeeded = 1),
        new EventData(60, time_slots = 4, classRoomsNeeded = 1),

        new EventData(61, time_slots = 4, pcRoomsNeeded = 1),

        //Day 3
        new EventData(62, time_slots = 4, classRoomsNeeded = 1),
        new EventData(63, time_slots = 4, classRoomsNeeded = 1),
        new EventData(64, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
        new EventData(65, time_slots = 4, pcRoomsNeeded = 1),
        new EventData(66, time_slots = 4, week = AWeek, labRoomsNeeded = 1),
        new EventData(67, time_slots = 4, week = BWeek, labRoomsNeeded = 1),
        /*
                    //Day 4
                    new EventData(68, time_slots = 4, pcRoomsNeeded = 1),
                    new EventData(69, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
                    new EventData(70, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
                    new EventData(71, time_slots = 4, pcRoomsNeeded = 1),
                    new EventData(72, time_slots = 4, pcRoomsNeeded = 1),
                    new EventData(73, time_slots = 4, pcRoomsNeeded = 1),

                    //Day 5
                    new EventData(74, time_slots = 4, classRoomsNeeded = 1),
                    new EventData(75, time_slots = 2, classRoomsNeeded = 1),
                    new EventData(76, time_slots = 4, week = AWeek, pcRoomsNeeded = 1),
                    new EventData(77, time_slots = 4, week = BWeek, pcRoomsNeeded = 1),
                    new EventData(78, time_slots = 4, week = AWeek, classRoomsNeeded = 1),
                    new EventData(79, time_slots = 4, week = BWeek, classRoomsNeeded = 1),
                    new EventData(80, time_slots = 4, classRoomsNeeded = 1),
                    new EventData(81, time_slots = 2, classRoomsNeeded = 1),

                    new EventData(82, time_slots = 4, pcRoomsNeeded = 1),*/
    )

    val nEvents = 67

    val preassignedEvents = events.filter(_.relativeStart != 0)

    val precedences = List()

    //Event Exclusions

    def getTheoryIncompatibilities(eventList: List[EventData], event: EventData) = {
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

object Application extends App{

    val TimeSlotDuration = 30 //This shouldn't be here
    var Language: Language = DefaultLanguage

    override def main(args: Array[String]): Unit = {

        //
        // Instance 1: 1er_s1 (2019-2020)
        //

        GUI.gui

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
