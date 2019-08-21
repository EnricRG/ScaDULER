package solver

import app.AppSettings
import misc.Weeks.{AWeek, BWeek, Week}
import model.Resource

import scala.collection.mutable.ListBuffer
import annotation.tailrec
import scala.collection.immutable

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
        object resourceAvailabilityFillerAux {
            private val aWeekAuxEvents = new ListBuffer[(Int,Int,Resource)] //list containing auxiliary events' start, duration and resource needed
            private val bWeekAuxEvents = new ListBuffer[(Int,Int,Resource)] //list containing auxiliary events' start, duration and resource needed

            for(r <- instance.resources) {

                //source: https://stackoverflow.com/questions/28286089/scala-group-consecutive-elements-in-list-where-function-is-true
                def pack(xs: List[Int]): List[List[Int]] = xs match {
                    case Nil => Nil
                    case _ =>
                        val (first, rest) = xs.span(_ >= 0)
                        first :: pack(rest.dropWhile(_ < 0))
                }

                //post-condition of getAvailableIntervalsOrElse ensures that the list is sorted

                def generateEventTuples(r: Resource, week: Week): List[(Int, Int, Resource)] = {
                    val weekAuxEvents: ListBuffer[(Int,Int,Resource)] = new ListBuffer
                    for(d <- 0 until AppSettings.days) {
                        val packedWeekIntervals = pack(r.getUnavailableIntervalsOrElse(week.toWeekNumber, d, -1).toList)
                        for (consecutiveIntervals <- packedWeekIntervals if consecutiveIntervals.nonEmpty) {
                            weekAuxEvents ++= (for (i <- 1 to r.quantity) yield (consecutiveIntervals.head + ModelIndexDeviation, consecutiveIntervals.length, r)).toList
                        }
                    }
                    weekAuxEvents.toList
                }

                aWeekAuxEvents ++= generateEventTuples(r, AWeek)
                bWeekAuxEvents ++= generateEventTuples(r, BWeek)
            }

            private val eventDurationAuxAWeek = aWeekAuxEvents.map(_._2)
            private val eventWeekAuxAWeek = aWeekAuxEvents.map(_ => AWeek.toShortString)
            private val nPreassignedEventsAuxAWeek = aWeekAuxEvents.length
            private val nPreassignedEventNumbersAuxAWeek = aWeekAuxEvents.indices.map(_ + instance.nEvents + ModelIndexDeviation)
            private val nPreassignedEventStartsAuxAWeek = aWeekAuxEvents.map(_._1)
            private val resourceNeededAuxAWeek = for(rNeeded <- aWeekAuxEvents.map(_._3)) yield for(r <- instance.resources) yield rNeeded == r

            private val eventDurationAuxBWeek = bWeekAuxEvents.map(_._2)
            private val eventWeekAuxBWeek = bWeekAuxEvents.map(_ => BWeek.toShortString)
            private val nPreassignedEventsAuxBWeek = bWeekAuxEvents.length
            private val nPreassignedEventNumbersAuxBWeek = bWeekAuxEvents.indices.map(_ + instance.nEvents + ModelIndexDeviation + nPreassignedEventsAuxAWeek)
            private val nPreassignedEventStartsAuxBWeek = bWeekAuxEvents.map(_._1)
            private val resourceNeededAuxBWeek = for(rNeeded <- bWeekAuxEvents.map(_._3)) yield for(r <- instance.resources) yield rNeeded == r

            val eventDurationAux: ListBuffer[Int] = eventDurationAuxAWeek ++ eventDurationAuxBWeek
            val eventWeekAux: ListBuffer[String] = eventWeekAuxAWeek ++ eventWeekAuxBWeek
            val nPreassignedEventsAux: Int = nPreassignedEventsAuxAWeek + nPreassignedEventsAuxBWeek
            val nPreassignedEventNumbersAux: immutable.IndexedSeq[Int] = nPreassignedEventNumbersAuxAWeek ++ nPreassignedEventNumbersAuxBWeek
            val nPreassignedEventStartsAux: ListBuffer[Int] = nPreassignedEventStartsAuxAWeek ++ nPreassignedEventStartsAuxBWeek
            val resourceNeededAux: ListBuffer[List[Boolean]] = resourceNeededAuxAWeek ++ resourceNeededAuxBWeek
        }

        //FIXME the generated events cannot surpass day limit

        val nDays = instance.nDays
        val dayDuration = instance.dayDuration
        val nResources = instance.nResources
        val resourceQuantity = instance.resources.map(_.getQuantity)
        val nEvents = instance.nEvents + resourceAvailabilityFillerAux.nPreassignedEventsAux
        val eventDuration = instance.events.map(_.getDuration) ++ resourceAvailabilityFillerAux.eventDurationAux
        val eventWeek = instance.events.map(_.getWeek.toShortString) ++ resourceAvailabilityFillerAux.eventWeekAux
        val eventExclusions = for(e1 <- instance.events) yield for(e2 <- instance.events) yield e1.getIncompatibilities.contains(e2)
        val resourceNeeded = (for(e <- instance.events) yield for(r <- instance.resources) yield e.getNeededResource == r) ++ resourceAvailabilityFillerAux.resourceNeededAux
        val preassignedEventNumbers = preassignedEventsAux.map(instance.events.indexOf(_) + ModelIndexDeviation) ++ resourceAvailabilityFillerAux.nPreassignedEventNumbersAux
        val preassignedEventStarts = preassignedEventsAux.map(_.getStartInterval + ModelIndexDeviation) ++ resourceAvailabilityFillerAux.nPreassignedEventStartsAux
        val nPreassignedEvents = preassignedEventsAux.length + resourceAvailabilityFillerAux.nPreassignedEventsAux
        val nPrecedences = 0
        val predecessors = List()
        val successors = List()

        val totalEventExclusions = (for(e1 <- 0 until nEvents) yield (for(e2 <- 0 until nEvents) yield if(e1 < instance.nEvents && e2 < instance.nEvents) eventExclusions(e1)(e2) else false).toList).toList

        NewMiniZincInstanceData(
            nDays, dayDuration, //general parameters
            nResources, resourceQuantity, //resource parameters
            nEvents, eventDuration, eventWeek, totalEventExclusions, resourceNeeded, //event parameters
            nPreassignedEvents, preassignedEventNumbers, preassignedEventStarts, //pre-assignation parameters
            nPrecedences, predecessors,successors //precedences parameters
        )
    }


}