package app

import misc.{Warning, Weeks}
import misc.Weeks.{AWeek, BWeek, EveryWeek, Week}
import model.{Course, NewEvent, Quarter}

import scala.collection.mutable.ListBuffer

class AssignmentViabilityChecker(course: Course, quarter: Quarter, eventWeek: Week, droppedWeek: Int, interval: Int, event: NewEvent) {

    private val eventDatabase = MainApp.getDatabase.eventDatabase
    private val courseDatabase = MainApp.getDatabase.courseDatabase

    private var checked: Boolean = false
    private var viable: Boolean = false
    private var warning: Option[Warning] = None

    def isAViableAssignment: Boolean = checked match{
        case false => checkViability(); viable
        case _ => viable
    }

    def getWarning: Warning = warning.orNull

    def getQuarterEvents(course: Course, quarter: Quarter): Iterable[NewEvent] = {
        if (quarter == course.firstQuarter) courseDatabase.getElements.map(_.firstQuarter).flatMap(_.getSchedule.getEvents)
        else courseDatabase.getElements.map(_.secondQuarter).flatMap(_.getSchedule.getEvents)
    }

    def checkEventIncompatibilities(course: Course, quarter: Quarter, event: NewEvent, week: Week, interval: Int): Option[Warning] = {
        val quarterEvents = getQuarterEvents(course, quarter)

        val incompatibilityClashes = quarterEvents.
            filter(x => x != event && x.isAssigned && event.getIncompatibilities.contains(x) && weekOverlap(event.getWeek, x.getWeek) && overlap(x.getStartInterval,x.getDuration,interval,event.getDuration))

        if(incompatibilityClashes.isEmpty)
            None
        else {
            val eventShortName = event.getShortName
            val incompatibilityShortName = incompatibilityClashes.head.getShortName
            Some(new Warning(String.format(AppSettings.language.getItem("warning_incompatibleEvents"),
                if(eventShortName.isBlank) event.getName else eventShortName,
                if(incompatibilityShortName.isBlank) incompatibilityClashes.head.getName else incompatibilityShortName))
            )
        }
    }

    def noOverlap(start1: Int, duration1: Int, start2: Int, duration2: Int): Boolean = {
        start1 + duration1 <= start2 || start2 + duration2 <= start1
    }

    def overlap(start1: Int, duration1: Int, start2: Int, duration2: Int): Boolean = !noOverlap(start1,duration1,start2,duration2)

    def weekOverlap(week1: Week, week2: Week): Boolean = week1 == Weeks.EveryWeek || week2 == EveryWeek || week1 == week2

    //def eventOverlap(e1: NewEvent, e2: NewEvent): Boolean = weekOverlap(e1.getWeek, e2.getWeek) && overlap(e1.getStartInterval, e1.getDuration, e2.getStartInterval, e2.getDuration)

    /*def checkResourceAvailability(course: Course, quarter: Quarter, event: NewEvent, week: Week, interval: Int): Option[Warning] = {

        if(event.needsResource) {

            val availabilityMap = (
                for (i <- interval until interval + event.getDuration)
                    yield (i, event.getNeededResource.availability.isAvailable(week.toWeekNumber, i))
                ).toMap

            if (!availabilityMap.values.toList.contains(true))
                return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceNeverUnavailable"), event.getNeededResource.getName)))
            else if (availabilityMap.values.toList.contains(false))
                return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceUnavailable"), event.getNeededResource.getName)))


            val inFirstQuarter = course.firstQuarter == quarter
            val quarters = MainApp.getDatabase.courseDatabase.getElements.map(x => if (inFirstQuarter) x.firstQuarter else x.secondQuarter)


            //map of all events at each interval that would run the new event that use the same resource as our new event
            val concurrentEventsFromInterval: Map[Int, Iterable[NewEvent]] = (for (i <- interval until interval + event.getDuration) yield
                (i, quarters.flatMap(_.schedule.getEventsAtIntervalOrElseCreate(week.toWeekNumber, i).filter(x => x != event && x.getNeededResource == event.getNeededResource)))).toMap


            val concurrentEventsBeforeInterval = (for (i <- interval - AppSettings.maxEventDuration + 1 until interval) yield {
                (i, quarters.flatMap(_.schedule.getEventsAtIntervalOrElseCreate(week.toWeekNumber, i).filter(x => x != event && x.getDuration + i - 1 >= interval && x.getNeededResource == event.getNeededResource)))
            }).toMap

            val addedEvents = (for (i <- interval until interval + event.getDuration) yield {
                (i, concurrentEventsBeforeInterval.values.flatten.filter(x => x.getStartInterval <= i && x.getStartInterval + x.getDuration - 1 >= i))
            }).toMap


            val allConcurrentEvents: Map[Int, List[NewEvent]] = concurrentEventsFromInterval.map { case (k, l) => (k, l.toList ++ addedEvents.getOrElse(k, ListBuffer()).toList) }


            val consumedResources: Map[Int, Int] = allConcurrentEvents.map { case (i, l) => (i, l.size) }


            for (i <- interval until interval + event.getDuration) if (event.getNeededResource.getAvailableQuantity - consumedResources.getOrElse(i, 0) < 0) {
                val relativeMinutes = (AppSettings.TimeSlotDuration * (interval - i)).toString
                return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceWillBeUnavailable"), event.getNeededResource.getName, relativeMinutes)))
            }

        }

        viable = true
        None
    }*/

    def checkResourceAvailability2(course: Course, quarter: Quarter, event: NewEvent, week: Week, interval: Int): Option[Warning] = {
        if(event.needsResource) {

            val availabilityMap = for (i <- interval until interval + event.getDuration) yield event.getNeededResource.availability.isAvailable(week.toWeekNumber, i)

            if (!availabilityMap.toList.contains(true))
                return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceNeverUnavailable"), event.getNeededResource.getName)))
            else if (availabilityMap.toList.contains(false))
                return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceUnavailable"), event.getNeededResource.getName)))


            val quarterEvents =  getQuarterEvents(course, quarter)

            def checkWeeklyAvailability(week: Week): Option[Warning] = {
                val concurrentEvents = quarterEvents.filter(x => x != event && x.isAssigned && weekOverlap(week, x.getWeek) && overlap(x.getStartInterval,x.getDuration,interval,event.getDuration))

                val resourceAvailability = for(i <- interval until interval + event.getDuration) yield (i,event.getNeededResource.quantity - concurrentEvents.count(x => overlap(i, 1, x.getStartInterval, x.getDuration)))

                for((inter, resourceAvailableQuantity) <- resourceAvailability) {
                    if(resourceAvailableQuantity == 0) {
                        val relativeMinutes = (if (interval < inter) AppSettings.TimeSlotDuration * (inter - interval) else 0).toString
                        return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceWillBeUnavailable"), event.getNeededResource.getName, relativeMinutes)))
                    }
                }
                None
            }

            checkWeeklyAvailability(AWeek) match {
                case Some(x) => return Some(x)
                case None => checkWeeklyAvailability(BWeek) match{
                    case Some(x) => return Some(x)
                    case _ =>
                }
            }

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

    def checkWeeks(eventWeek: Week, droppedWeek: Int): Option[Warning] = {
        if(eventWeek != Weeks.EveryWeek && event.getWeek.toWeekNumber != droppedWeek)
            Some(new Warning(AppSettings.language.getItem("warning_weekMatchError")))
        else
            None
    }

    def checkViability():Unit = {

        //TODO
        //if (warning != null) warning = checkEventPrecedences(course, quarter, event, week, interval)

        if (warning.isEmpty) warning = checkWeeks(eventWeek, droppedWeek)

        if (warning.isEmpty) warning = checkBorders(course, quarter, event, eventWeek, interval)

        if (warning.isEmpty) warning = checkEventIncompatibilities(course, quarter, event, eventWeek, interval)

        //if (warning.isEmpty) warning = checkResourceAvailability(course, quarter, event, eventWeek, interval)
        if (warning.isEmpty) warning = checkResourceAvailability2(course, quarter, event, eventWeek, interval)

        checked = true
    }
}
