import scala.sys.process.Process

object MiniZincConstants {
    val MiniZincCommandLineDataOption = "-D"
    val EqualsSign = "="
    val SemiColon = ";"
}

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
            instance.events.map(_.duration), instance.events.map(_.week),
            for(e1 <- instance.events) yield for(e2 <- instance.events) yield e1.incompatibilities.contains(e2),
            instance.precedences.length, instance.precedences.map(_._1.num), instance.precedences.map(_._2.num),
            instance.events.map(_.labRoomsNeeded), instance.events.map(_.classRoomsNeeded), instance.events.map(_.pcRoomsNeeded))

    def toCommandLine: String = {

        MiniZincConstants.MiniZincCommandLineDataOption +
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

class MiniZincInstanceSolver(val instance: MiniZincInstanceData) extends InstanceSolver{

    def provisionalSolve = {

        val minizinc_call = "./bin/minizinc/minizinc -s --compiler-statistics --solver Chuffed minizinc/firstModel_bmee.mzn " + instance.toCommandLine

        //val minizinc_call = "./bin/minizinc/minizinc"

        val minizinc_process = Process(minizinc_call)

        minizinc_process.lineStream.toList
    }

    def solve: EventSchedule = ???

    def optimize: EventSchedule = solve
}
