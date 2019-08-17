package app

import misc.Warning
import misc.Weeks.Week
import model.{Course, NewEvent, Quarter}

import scala.collection.immutable
import scala.collection.mutable.ListBuffer

class AssignmentViabilityChecker(course: Course, quarter: Quarter, week: Week, interval: Int, event: NewEvent) {
    private var checked: Boolean = false
    private var viable: Boolean = false
    private var warning: Option[Warning] = None

    def isAViableAssignment: Boolean = checked match{
        case false => checkViability(); viable
        case _ => viable
    }

    def getWarning: Warning = warning.orNull

    def checkEventIncompatibilities(course: Course, quarter: Quarter, event: NewEvent, week: Week, interval: Int): Option[Warning] = {
        val incompatibilities = quarter.getSchedule.getIncompatibleEvents(event,week.toWeekNumber,interval)

        if(incompatibilities.isEmpty)
            None
        else
            Some(new Warning(String.format(AppSettings.language.getItem("warning_incompatibleEvents"),
                event.getShortName,
                incompatibilities.head.getShortName))
            )
    }

    def checkResourceAvailability(course: Course, quarter: Quarter, event: NewEvent, week: Week, interval: Int): Option[Warning] = {

        val availabilityMap = (
            for (i <- interval until interval + event.getDuration)
            yield (i, event.getNeededResource.availability.isAvailable(week.toWeekNumber, i))
            ).toMap

        if(!availabilityMap.values.toList.contains(true))
            return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceNeverUnavailable"), event.getNeededResource.getName)))
        else if(availabilityMap.values.toList.contains(false))
            return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceUnavailable"), event.getNeededResource.getName)))


        val inFirstQuarter = course.firstQuarter == quarter
        val quarters = MainApp.getDatabase.courseDatabase.getElements.map(x => if(inFirstQuarter) x.firstQuarter else x.secondQuarter)

        //map of all events at each interval that would run the new event that use the same resource as our new event
        val concurrentEventsFromInterval: Map[Int, Iterable[NewEvent]] = (for(i <- interval until interval + event.getDuration) yield
            (i, quarters.flatMap(_.schedule.getEventsAtIntervalOrElseCreate(week.toWeekNumber, i).filter(x => x != event && x.getNeededResource == event.getNeededResource)))).toMap

        val concurrentEventsBeforeInterval = (for(i <- interval-AppSettings.maxEventDuration+1 until interval) yield {
            (i, quarters.flatMap(_.schedule.getEventsAtIntervalOrElseCreate(week.toWeekNumber, i).filter(x => x != event && x.getDuration+i-1>=interval && x.getNeededResource == event.getNeededResource)))
        }).toMap

/*
        for(i <- interval-AppSettings.maxEventDuration+1 until interval){
            val eventsAtInterval = quarters.flatMap(_.schedule.getEventsAtIntervalOrElseCreate(week.toWeekNumber, i)
                .filter(x => x.getDuration+i-1 >= interval && x.getNeededResource == event.getNeededResource))
            for(e <- eventsAtInterval) yield {

            }
        }*/

        val addedEvents = (for(i <- interval until interval + event.getDuration) yield{
            (i, concurrentEventsBeforeInterval.values.flatten.filter(x => x.getStartInterval <= i && x.getStartInterval+x.getDuration-1 >= i))
        }).toMap

        val allConcurrentEvents: Map[Int, List[NewEvent]] = concurrentEventsFromInterval.map{case (k,l) => (k,l.toList++addedEvents.getOrElse(k,ListBuffer()).toList)}

        val consumedResources: Map[Int, Int] = allConcurrentEvents.map{case (i,l)=> (i,l.size)}

        for(i <- interval until interval + event.getDuration) if(event.getNeededResource.getAvailableQuantity - consumedResources.getOrElse(i,0) <= 0) {
            val relativeMinutes = (AppSettings.TimeSlotDuration*(interval-i)).toString
            return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceWillBeUnavailable"), event.getNeededResource.getName, relativeMinutes)))
        }

        viable = true
        None
    }

    def checkBorders(course: Course, quarter: Quarter, event: NewEvent, week: Week, interval: Int): Option[Warning] = {
        val dayInterval = interval % AppSettings.timeSlotsPerDay

        if(dayInterval+event.getDuration > AppSettings.timeSlotsPerDay)
            Some(new Warning(AppSettings.language.getItem("warning_borderLimit")))
        else None
    }

    def checkViability():Unit = {

        //if (warning != null) warning = checkEventPrecedences(course, quarter, event, week, interval)

        if (warning.isEmpty) warning = checkBorders(course, quarter, event, week, interval)

        if (warning.isEmpty) warning = checkEventIncompatibilities(course, quarter, event, week, interval)

        if (warning.isEmpty) warning = checkResourceAvailability(course, quarter, event, week, interval)

        checked = true
    }
}
