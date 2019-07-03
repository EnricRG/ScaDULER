class MiniZincInstanceSolver extends InstanceSolver{

    val MiniZincCommandLineDataOption = "-D"
    val EqualsSign = "="
    val SemiColon = ";"

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
                instance.events.map(_.lab_rooms_needed), instance.events.map(_.class_rooms_needed), instance.events.map(_.pc_rooms_needed))

        def toCommandLine(): String = {

            MiniZincCommandLineDataOption +
            "\"" +
            "dayDuration=" + dayDuration + SemiColon +
            "labRooms=" + labRooms + SemiColon +
            "classRooms=" + classRooms + SemiColon +
            "pcRooms=" + pcRooms + SemiColon +
            "nEvents=" + nEvents + SemiColon +
            "nPreassignedEvents=" + nPreassignedEvents + SemiColon +
            "PreassignedEventNumbers=" + preassignedEventNum.mkString("[", ",", "]") + SemiColon +
            "PreassignedEventStarts=" + preassignedEventStart.mkString("[", ",", "]") + SemiColon +
            "eventDuration=" + eventDuration.mkString("[", ",", "]") + SemiColon +
            "eventWeek=" + eventWeek.mkString("[", ",", "]") + SemiColon +
            "eventExclusions" + eventExclusions.map(_.mkString(",")).mkString("[", "|", "]") + SemiColon + //This is wrong, one | missing at the end
            "nPrecedences=" + nPrecedences + SemiColon +
            "predecessors=" + predecessors.mkString("[", ",", "]") + SemiColon +
            "successors=" + successors.mkString("[", ",", "]") + SemiColon +
            "needsLabRoom=" + needsLabRoom.mkString("[", ",", "]") + SemiColon +
            "needsClassRoom=" + needsClassRoom.mkString("[", ",", "]") + SemiColon +
            "needsPcRoom=" + needsPcRoom.mkString("[", ",", "]") + SemiColon +
            "\""
        }

        def toDZNFile(): Unit = ???
    }

    def init(instance: InstanceData): Unit = {

    }

    def solve(): EventSchedule = ???

    def optimize(): EventSchedule = ???
}
