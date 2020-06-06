package solver

import app.AppSettings
import model.Resource
import model.Weeks._
import service.ID

import scala.collection.immutable
import scala.collection.mutable.ListBuffer

case class MiniZincInstanceData(nDays: Int, //unused
                                dayDuration: Int,
                                nResources: Int,
                                resourceQuantity: List[Int],
                                nEvents: Int,
                                eventDuration: List[Int],
                                eventPeriodicity: List[String],
                                //eventWeek: List[String],
                                eventExclusions: List[List[Boolean]],
                                resourceNeeded: List[List[Boolean]],
                                nPredefinedEventWeeks: Int,
                                predefinedWeekEventNumbers: List[Int],
                                predefinedEventWeek: List[String],
                                nPreassignedEvents: Int,
                                preassignedEventNumbers: List[Int],
                                preassignedEventStarts: List[Int],
                                nPrecedences: Int,
                                predecessors: List[Int],
                                successors: List[Int],
                                eventMapping: Map[Int, ID]){

    def asDZNString: String = {
        val SemiColon = MiniZincConstants.SemiColon
        val LineJump = "\n\n"

        "nDays = " + nDays + SemiColon + LineJump +
        "dayDuration = " + dayDuration + SemiColon + LineJump +
        "nResources = " + nResources + SemiColon + LineJump +
        "resourceQuantity = " + resourceQuantity.mkString("[", ",", "]") + SemiColon + LineJump +
        "nEvents = " + nEvents + SemiColon + LineJump +
        "eventDuration = " + eventDuration.mkString("[", ",", "]") + SemiColon + LineJump +
        //"eventWeek = " + eventWeek.mkString("[", ",", "]") + SemiColon + LineJump +
        "eventPeriodicity = " + eventPeriodicity.mkString("[", "," ,"]") + SemiColon + LineJump +
        "eventExclusions = " + eventExclusions.map(_.mkString(",")).mkString("[|", "|", "|]") + SemiColon + LineJump +
        "resourceNeeded = " + (if(resourceNeeded.isEmpty) "[]" else resourceNeeded.map(_.mkString(",")).mkString("[|", "|", "|]")) + SemiColon + LineJump +
        "nPredefinedEventWeeks = " + nPredefinedEventWeeks + SemiColon + LineJump +
        "predefinedWeekEventNumbers = " + predefinedWeekEventNumbers.mkString("[", ",", "]") + SemiColon + LineJump +
        "predefinedEventWeek = " + predefinedEventWeek.mkString("[", ",", "]") + SemiColon + LineJump +
        "nPreassignedEvents = " + nPreassignedEvents + SemiColon + LineJump +
        "preassignedEventNumbers = " + preassignedEventNumbers.mkString("[", ",", "]") + SemiColon + LineJump +
        "preassignedEventStarts = " + preassignedEventStarts.mkString("[", ",", "]") + SemiColon + LineJump +
        "nPrecedences = " + nPrecedences + SemiColon + LineJump +
        "predecessors = " + predecessors.mkString("[", ",", "]") + SemiColon + LineJump +
        "successors = " + successors.mkString("[", ",", "]") + SemiColon
    }
}

object MiniZincInstance{
    val ModelIndexDeviation = 1

    def parsePeriodicity(periodicity: Periodicity): String = periodicity match{
        case Weekly => "WE"
        case Biweekly => "BI"
        case _ => "ERROR"
    }

    def parseWeek(week: Week): String = week match{
        case EveryWeek => "W"
        case AWeek => "A"
        case BWeek => "B"
        case _ => "ERROR"
    }

    def reverseParse(week: String): Week = week match{
        case "W" => EveryWeek
        case "A" => AWeek
        case "B" => BWeek
        case _ => null //should never get here
    }

    //TODO improve performance of this call. A lot of parts are improvable.
    def fromInstanceData(instance: InstanceData): MiniZincInstanceData = {
        val preassignedEventsAux = instance.events.filter(_.isAssigned)
        object resourceAvailabilityFillerAux {
            private val aWeekAuxEvents = new ListBuffer[(Int,Int,Resource)] //list containing auxiliary events' start, duration and resource needed
            private val bWeekAuxEvents = new ListBuffer[(Int,Int,Resource)] //list containing auxiliary events' start, duration and resource needed

            for(r <- instance.resources) {

                //source: https://stackoverflow.com/questions/28286089/scala-group-consecutive-elements-in-list-where-function-is-true
                def pack[T](xs: List[T], f: T => Boolean): List[List[T]] = xs match {
                    case Nil => Nil
                    case _ =>
                        val (first, rest) = xs.span(f(_))
                        first :: pack(rest.dropWhile(!f(_)),f)
                }

                //post-condition of getAvailableIntervalsOrElse ensures that the list is sorted

                def generateEventTuples(r: Resource, week: Week): List[(Int, Int, Resource)] = {
                    val weekAuxEvents: ListBuffer[(Int,Int,Resource)] = new ListBuffer
                    val max = r.getMaxQuantity
                    for(d <- 0 until AppSettings.days) {
                        /*val packedWeekIntervals = pack(r.getUnavailableIntervalsOrElse(week.toWeekNumber, d, -1).toList, ((x: Int) => x>=0))
                        for (consecutiveIntervals <- packedWeekIntervals if consecutiveIntervals.nonEmpty) {
                            weekAuxEvents ++= (for (i <- 1 to r.getMaxQuantity) yield (consecutiveIntervals.head + ModelIndexDeviation, consecutiveIntervals.length, r)).toList
                        }*/

                        for(quantity <- 0 until max) {
                            val packedIntervalsWithQuantity = pack(r.getIntervalsWithQuantityOrElse(week.toWeekNumber, d, quantity, -1).toList, (x: Int) => x >= 0)
                            for(consecutiveIntervals <- packedIntervalsWithQuantity if consecutiveIntervals.nonEmpty){
                                weekAuxEvents ++= (1 to max-quantity).map(_ => (consecutiveIntervals.head + ModelIndexDeviation, consecutiveIntervals.length, r))
                            }
                        }
                    }
                    weekAuxEvents.toList
                }

                aWeekAuxEvents ++= generateEventTuples(r, AWeek)
                bWeekAuxEvents ++= generateEventTuples(r, BWeek)
            }

            private val eventDurationAuxAWeek = aWeekAuxEvents.map(_._2)
            private val eventWeekAuxAWeek = aWeekAuxEvents.map(_ => parseWeek(AWeek))
            private val nPreassignedEventsAuxAWeek = aWeekAuxEvents.length
            private val preassignedEventNumbersAuxAWeek = aWeekAuxEvents.indices.map(_ + instance.nEvents + ModelIndexDeviation)
            private val preassignedEventStartsAuxAWeek = aWeekAuxEvents.map(_._1)
            private val resourceNeededAuxAWeek = for(rNeeded <- aWeekAuxEvents.map(_._3)) yield for(r <- instance.resources) yield rNeeded == r

            private val eventDurationAuxBWeek = bWeekAuxEvents.map(_._2)
            private val eventWeekAuxBWeek = bWeekAuxEvents.map(_ => parseWeek(BWeek))
            private val nPreassignedEventsAuxBWeek = bWeekAuxEvents.length
            private val preassignedEventNumbersAuxBWeek = bWeekAuxEvents.indices.map(_ + instance.nEvents + ModelIndexDeviation + nPreassignedEventsAuxAWeek)
            private val preassignedEventStartsAuxBWeek = bWeekAuxEvents.map(_._1)
            private val resourceNeededAuxBWeek = for(rNeeded <- bWeekAuxEvents.map(_._3)) yield for(r <- instance.resources) yield rNeeded == r

            val eventDurationAux: ListBuffer[Int] = eventDurationAuxAWeek ++ eventDurationAuxBWeek
            //val eventWeekAux: ListBuffer[String] = eventWeekAuxAWeek ++ eventWeekAuxBWeek
            val predefinedEventWeek: List[String] = List.fill(nPreassignedEventsAuxAWeek)(parseWeek(AWeek)) ++ List.fill(nPreassignedEventsAuxBWeek)(parseWeek(BWeek))
            val nPreassignedEventsAux: Int = nPreassignedEventsAuxAWeek + nPreassignedEventsAuxBWeek
            val preassignedEventNumbersAux: immutable.IndexedSeq[Int] = preassignedEventNumbersAuxAWeek ++ preassignedEventNumbersAuxBWeek
            val preassignedEventStartsAux: ListBuffer[Int] = preassignedEventStartsAuxAWeek ++ preassignedEventStartsAuxBWeek
            val resourceNeededAux: ListBuffer[List[Boolean]] = resourceNeededAuxAWeek ++ resourceNeededAuxBWeek
        }

        //FIXME the generated events cannot surpass day limit

        val nDays = instance.nDays
        val dayDuration = instance.dayDuration
        val nResources = instance.nResources
        val resourceQuantity = instance.resources.map(_.getMaxQuantity)
        val nEvents = instance.nEvents + resourceAvailabilityFillerAux.nPreassignedEventsAux
        val eventDuration = instance.events.map(_.duration) ++ resourceAvailabilityFillerAux.eventDurationAux
        val eventPeriodicity = instance.events.map(e => parsePeriodicity(e.periodicity)) ++ List.fill(resourceAvailabilityFillerAux.nPreassignedEventsAux)(parsePeriodicity(Biweekly))
        //val eventWeek = instance.events.map(_.getWeek.toShortString) ++ resourceAvailabilityFillerAux.eventWeekAux
        val eventExclusions = for(e1 <- instance.events) yield for(e2 <- instance.events) yield e1.incompatibilities.contains(e2)
        val resourceNeeded = (for(e <- instance.events) yield for(r <- instance.resources) yield e.neededResource.orNull == r) ++ resourceAvailabilityFillerAux.resourceNeededAux
        val preassignedEventNumbers = preassignedEventsAux.map(instance.events.indexOf(_) + ModelIndexDeviation) ++ resourceAvailabilityFillerAux.preassignedEventNumbersAux
        val preassignedEventStarts = preassignedEventsAux.map(_.getStartInterval + ModelIndexDeviation) ++ resourceAvailabilityFillerAux.preassignedEventStartsAux
        val nPreassignedEvents = preassignedEventsAux.length + resourceAvailabilityFillerAux.nPreassignedEventsAux
        val nPredefinedEventWeeks = nPreassignedEvents
        val predefinedWeekEventNumbers = preassignedEventNumbers
        val predefinedEventWeek = preassignedEventsAux.map(e => parseWeek(e.week.orNull)) ++ resourceAvailabilityFillerAux.predefinedEventWeek
        val nPrecedences = 0
        val predecessors = List()
        val successors = List()

        val totalEventExclusions = (for(e1 <- 0 until nEvents) yield (for(e2 <- 0 until nEvents) yield if(e1 < instance.nEvents && e2 < instance.nEvents) eventExclusions(e1)(e2) else false).toList).toList

        MiniZincInstanceData(
            nDays, dayDuration, //general parameters
            nResources, resourceQuantity, //resource parameters
            nEvents, eventDuration /*,eventWeek*/, eventPeriodicity, totalEventExclusions, resourceNeeded, //event parameters
            nPredefinedEventWeeks, predefinedWeekEventNumbers, predefinedEventWeek, //week pre-assignation parameters
            nPreassignedEvents, preassignedEventNumbers, preassignedEventStarts, //start pre-assignation parameters
            nPrecedences, predecessors,successors, //precedences parameters
            instance.events.indices.map(index => (index+ModelIndexDeviation, instance.events.apply(index).getID)).toMap
        )
    }


}