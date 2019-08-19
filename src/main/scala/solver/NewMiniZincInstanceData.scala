package solver

case class NewMiniZincInstanceData(nDays: Int, //unused
                                   dayDuration: Int,
                                   nResources: Int,
                                   resourceQuantity: List[Int],
                                   nEvents: Int,
                                   eventDuration: List[Int],
                                   eventWeek: List[String],
                                   eventExclusions: List[List[Boolean]],
                                   resourceNeeded: List[List[Boolean]],
                                   nPreassignedEvents: Int,
                                   preassignedEventNumbers: List[Int],
                                   preassignedEventStarts: List[Int],
                                   nPrecedences: Int,
                                   predecessors: List[Int],
                                   successors: List[Int]){

    def asDZNString: String = {
        val SemiColon = MiniZincConstants.SemiColon
        val LineJump = "\n\n"

        "dayDuration = " + dayDuration + SemiColon + LineJump +
        "nResources = " + nResources + SemiColon + LineJump +
        "resourceQuantity = " + resourceQuantity.mkString("[", ",", "]") + SemiColon + LineJump +
        "nEvents = " + nEvents + SemiColon + LineJump +
        "eventDuration = " + eventDuration.mkString("[", ",", "]") + SemiColon + LineJump +
        "eventWeek = " + eventWeek.mkString("[", ",", "]") + SemiColon + LineJump +
        "eventExclusions = " + eventExclusions.map(_.mkString(",")).mkString("[|", "|", "|]") + SemiColon + LineJump + //This is wrong, one | missing at the end
        "resourceNeeded = " + resourceNeeded.map(_.mkString(",")).mkString("[|", "|", "|]") + SemiColon + LineJump + //This is wrong, one | missing at the end
        "nPreassignedEvents = " + nPreassignedEvents + SemiColon + LineJump +
        "preassignedEventNumbers = " + preassignedEventNumbers.mkString("[", ",", "]") + SemiColon + LineJump +
        "preassignedEventStarts = " + preassignedEventStarts.mkString("[", ",", "]") + SemiColon + LineJump +
        "nPrecedences = " + nPrecedences + SemiColon + LineJump +
        "predecessors = " + predecessors.mkString("[", ",", "]") + SemiColon + LineJump +
        "successors = " + successors.mkString("[", ",", "]") + SemiColon
    }
}

object MiniZincInstance{
    val ModelIndexDeviation = 1;

    def fromInstanceData(instance: NewInstanceData): NewMiniZincInstanceData = {
        val preassignedEventsAux = instance.events.filter(_.isAssigned)

        val nDays = instance.nDays
        val dayDuration = instance.dayDuration
        val nResources = instance.nResources
        val resourceQuantity = instance.resources.map(_.getQuantity)
        val nEvents = instance.nEvents
        val eventDuration = instance.events.map(_.getDuration)
        val eventWeek = instance.events.map(_.getWeek.toShortString)
        val eventExclusions = for(e1 <- instance.events) yield for(e2 <- instance.events) yield e1.getIncompatibilities.contains(e2)
        val resourceNeeded = for(e <- instance.events) yield for(r <- instance.resources) yield e.getNeededResource == r
        val preassignedEventNumbers = preassignedEventsAux.map(instance.events.indexOf(_) + ModelIndexDeviation)
        val preassignedEventStarts = preassignedEventsAux.map(_.getStartInterval + ModelIndexDeviation)
        val nPreassignedEvents = preassignedEventsAux.length
        val nPrecedences = 0
        val predecessors = List()
        val successors = List()

        NewMiniZincInstanceData(
            nDays, dayDuration, //general parameters
            nResources, resourceQuantity, //resource parameters
            nEvents, eventDuration, eventWeek, eventExclusions, resourceNeeded, //event parameters
            nPreassignedEvents, preassignedEventNumbers, preassignedEventStarts, //pre-assignation parameters
            nPrecedences, predecessors,successors //precedences parameters
        )
    }


}