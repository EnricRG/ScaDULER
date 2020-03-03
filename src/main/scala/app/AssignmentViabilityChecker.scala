package app

import model.Weeks.{AWeek, BWeek, EveryWeek, Week}
import misc.Warning
import model.{Course, Event, QuarterData, Weeks}

class AssignmentViabilityChecker(course: Course, quarter: QuarterData, eventWeek: Week, droppedWeek: Int, interval: Int, event: Event) {

    private val courseDatabase = MainApp.getDatabase.courseDatabase

    private var checked: Boolean = false
    private var viable: Boolean = false
    private var warning: Option[Warning] = None

    def isAViableAssignment: Boolean = if (checked) {
        viable
    } else {
        checkViability();
        viable
    }

    def getWarning: Warning = warning.orNull

    def getQuarterEvents(course: Course, quarter: QuarterData): Iterable[Event] = {
        if (quarter == course.firstQuarter) courseDatabase.getElements.map(_.firstQuarter).flatMap(_.getSchedule.getEvents)
        else courseDatabase.getElements.map(_.secondQuarter).flatMap(_.getSchedule.getEvents)
    }

    def checkEventIncompatibilities(course: Course, quarter: QuarterData, event: Event, interval: Int): Option[Warning] = {
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

    def checkResourceAvailability(course: Course, quarter: QuarterData, event: Event, week: Week, interval: Int): Option[Warning] = {
        if(event.needsResource) {

            val availabilityMap = for (i <- interval until interval + event.getDuration) yield event.getNeededResource.getAvailability.isAvailable(week.toWeekNumber, i)

            if (!availabilityMap.toList.contains(true))
                return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceNeverUnavailable"), event.getNeededResource.getName)))
            else if (availabilityMap.toList.contains(false))
                return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceUnavailable"), event.getNeededResource.getName)))


            val quarterEvents = getQuarterEvents(course, quarter).filter(_.getSafeNeededResource == event.getSafeNeededResource)

            def checkWeeklyAvailability(week: Week): Option[Warning] = {
                val concurrentEvents = quarterEvents.filter(x => x != event && x.isAssigned && weekOverlap(week, x.getWeek) && overlap(x.getStartInterval,x.getDuration,interval,event.getDuration))

                val resourceAvailability = for(i <- interval until interval + event.getDuration) yield (i,event.getNeededResource.getQuantity - concurrentEvents.count(x => overlap(i, 1, x.getStartInterval, x.getDuration)))

                for((inter, resourceAvailableQuantity) <- resourceAvailability) {
                    if(resourceAvailableQuantity == 0) {
                        val relativeMinutes = (if (interval < inter) AppSettings.TimeSlotDuration * (inter - interval) else 0).toString
                        return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceWillBeUnavailable"), event.getNeededResource.getName, relativeMinutes)))
                    }
                }
                None
            }

            if(event.getWeek != EveryWeek) checkWeeklyAvailability(event.getWeek) match{
                case Some(x) => return Some(x)
                case _ =>
            }
            else checkWeeklyAvailability(AWeek) match {
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

    def checkBorders(event: Event, interval: Int): Option[Warning] = {
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

        if (warning.isEmpty) warning = checkBorders(event, interval)

        if (warning.isEmpty) warning = checkEventIncompatibilities(course, quarter, event, interval)

        if (warning.isEmpty) warning = checkResourceAvailability(course, quarter, event, eventWeek, interval)

        checked = true
    }
}
