package actors

import solver.{EventSchedule, InstanceData, MiniZincConstants}

import scala.sys.process.Process



case class MiniZincInstanceData(dayDuration: Int,
                                labRooms: Int, classRooms: Int, pcRooms: Int,
                                nEvents: Int, nPreassignedEvents: Int,
                                preassignedEventNum: List[Int], preassignedEventStart: List[Int],
                                eventDuration: List[Int], eventWeek: List[String], eventExclusions: List[List[Boolean]],
                                nPrecedences: Int, predecessors: List[Int], successors: List[Int],
                                needsLabRoom: List[Int], needsClassRoom: List[Int], needsPcRoom: List[Int]){

    def this(instance: InstanceData) =
        this(instance.dayDuration,
            instance.labRooms, instance.classRooms, instance.pcRooms,
            instance.events.length, instance.preassignedEvents.length,
            instance.preassignedEvents.map(_.num), instance.preassignedEvents.map(_.relativeStart),
            instance.events.map(_.duration), instance.events.map(_.week.toShortString),
            for(e1 <- instance.events) yield for(e2 <- instance.events) yield e1.incompatibilities.contains(e2),
            instance.precedences.length, instance.precedences.map(_._1.num), instance.precedences.map(_._2.num),
            instance.events.map(_.labRoomsNeeded), instance.events.map(_.classRoomsNeeded), instance.events.map(_.pcRoomsNeeded))

    def toCommandLine: String = {

        MiniZincConstants.CommandLineDataOption +
          "\"" +
          "dayDuration=" + dayDuration + MiniZincConstants.SemiColon +
          "labRooms=" + labRooms + MiniZincConstants.SemiColon +
          "classRooms=" + classRooms + MiniZincConstants.SemiColon +
          "pcRooms=" + pcRooms + MiniZincConstants.SemiColon +
          "nEvents=" + nEvents + MiniZincConstants.SemiColon +
          "nPreassignedEvents=" + nPreassignedEvents + MiniZincConstants.SemiColon +
          "preassignedEventNumbers=" + preassignedEventNum.mkString("[", ",", "]") + MiniZincConstants.SemiColon +
          "preassignedEventStarts=" + preassignedEventStart.mkString("[", ",", "]") + MiniZincConstants.SemiColon +
          "eventDuration=" + eventDuration.mkString("[", ",", "]") + MiniZincConstants.SemiColon +
          "eventWeek=" + eventWeek.mkString("[", ",", "]") + MiniZincConstants.SemiColon +
          "eventExclusions=" + eventExclusions.map(_.mkString(",")).mkString("[|", "|", "|]") + MiniZincConstants.SemiColon + //This is wrong, one | missing at the end
          "nPrecedences=" + nPrecedences + MiniZincConstants.SemiColon +
          "predecessors=" + predecessors.mkString("[", ",", "]") + MiniZincConstants.SemiColon +
          "successors=" + successors.mkString("[", ",", "]") + MiniZincConstants.SemiColon +
          "needsLabRoom=" + needsLabRoom.mkString("[", ",", "]") + MiniZincConstants.SemiColon +
          "needsClassRoom=" + needsClassRoom.mkString("[", ",", "]") + MiniZincConstants.SemiColon +
          "needsPcRoom=" + needsPcRoom.mkString("[", ",", "]") + MiniZincConstants.SemiColon +
          "\""
    }

    def toDZNFile: Unit = ???
}

class MiniZincInstanceSolver extends InstanceSolver{

    def receive = {
        case data: InstanceData => sender ! provisionalSolve(new MiniZincInstanceData(data))
        case data: MiniZincInstanceData => sender ! provisionalSolve(data)
        case _ => List("ERROR")
    }

    def provisionalSolve(instance: MiniZincInstanceData) = {

        val minizinc_call = MiniZincConstants.MiniZincPath + " " +
                            MiniZincConstants.CommandLineStatisticsOption + " " +
                            MiniZincConstants.ChuffedSolver + " " +
                            " minizinc/firstModel_bmee.mzn " +
                            instance.toCommandLine

        println(minizinc_call)

        val minizinc_process = Process(minizinc_call)

        minizinc_process.lineStream.toList
    }

    def solve: EventSchedule = ???

    def optimize: EventSchedule = solve
}
