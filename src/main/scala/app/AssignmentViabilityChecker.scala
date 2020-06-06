package app

import misc.Warning
import model.Weeks._
import model.{Course, Event, QuarterData, Weeks}

class AssignmentViabilityChecker(course: Course, quarter: QuarterData, droppedWeek: Int, interval: Int, event: Event) {

    private val courseDatabase = MainApp.getDatabase.courseDatabase

    private val eventWeek = if(event.periodicity == Weekly) EveryWeek
                            else if(droppedWeek == AWeek.toWeekNumber) AWeek
                            else BWeek

    private var checked: Boolean = false
    private var viable: Boolean = false
    private var warning: Option[Warning] = None

    def isAViableAssignment: Boolean = if (checked) {
        viable
    } else {
        checkViability()
        viable
    }

    def getWarning: Warning = warning.orNull

    def getQuarterEvents(course: Course, quarter: QuarterData): Iterable[Event] = {
        if (quarter == course.firstQuarterData) courseDatabase.getElements.map(_.firstQuarterData).flatMap(_.getSchedule.getEvents)
        else courseDatabase.getElements.map(_.secondQuarterData).flatMap(_.getSchedule.getEvents)
    }

    def checkEventIncompatibilities(course: Course, quarter: QuarterData, event: Event, targetWeek: Week, interval: Int): Option[Warning] = {
        val quarterEvents = getQuarterEvents(course, quarter)

        val incompatibilityClashes = quarterEvents.
            filter(x => x != event && x.isAssigned && event.incompatibilities.contains(x) && weekOverlap(targetWeek, x.week.get) && overlap(x.getStartInterval,x.duration,interval,event.duration))

        if(incompatibilityClashes.isEmpty)
            None
        else {
            val eventShortName = event.shortName
            val incompatibilityShortName = incompatibilityClashes.head.shortName
            Some(new Warning(String.format(AppSettings.language.getItem("warning_incompatibleEvents"),
                if(eventShortName.trim.isEmpty) event.name else eventShortName,
                if(incompatibilityShortName.trim.isEmpty) incompatibilityClashes.head.name else incompatibilityShortName))
            )
        }
    }

    def noOverlap(start1: Int, duration1: Int, start2: Int, duration2: Int): Boolean = {
        start1 + duration1 <= start2 || start2 + duration2 <= start1
    }

    def overlap(start1: Int, duration1: Int, start2: Int, duration2: Int): Boolean = !noOverlap(start1,duration1,start2,duration2)

    def weekOverlap(week1: Week, week2: Week): Boolean = week1 == Weeks.EveryWeek || week2 == EveryWeek || week1 == week2

    def checkResourceAvailability(course: Course, quarter: QuarterData, event: Event, targetWeek: Week, interval: Int): Option[Warning] = {
        if(event.needsResource) {

            val availabilityMap = for (i <- interval until interval + event.duration) yield event.neededResource.get.availability.isAvailable(targetWeek.toWeekNumber, i)

            if (!availabilityMap.toList.contains(true))
                return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceNeverUnavailable"), event.neededResource.get.name)))
            else if (availabilityMap.toList.contains(false))
                return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceUnavailable"), event.neededResource.get.name)))


            val quarterEvents = getQuarterEvents(course, quarter).filter(_.neededResource.orNull == event.neededResource.get)

            def checkWeeklyAvailability(week: Week): Option[Warning] = {
                val concurrentEvents = quarterEvents.filter(x => x != event && x.isAssigned && weekOverlap(week, x.week.get) && overlap(x.getStartInterval,x.duration,interval,event.duration))

                val resourceAvailability = for(i <- interval until interval + event.duration) yield (i,event.neededResource.get.getQuantityAt(week, interval) - concurrentEvents.count(x => overlap(i, 1, x.getStartInterval, x.duration)))

                for((inter, resourceAvailableQuantity) <- resourceAvailability) {
                    if(resourceAvailableQuantity <= 0) {
                        val relativeMinutes = (if (interval < inter) AppSettings.TimeSlotDuration * (inter - interval) else 0).toString
                        return Some(new Warning(String.format(AppSettings.language.getItem("warning_resourceWillBeUnavailable"), event.neededResource.get.name, relativeMinutes)))
                    }
                }
                None
            }

            if(targetWeek != EveryWeek) checkWeeklyAvailability(targetWeek) match{
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

        if(dayInterval+event.duration > AppSettings.timeSlotsPerDay)
            Some(new Warning(AppSettings.language.getItem("warning_borderLimit")))
        else None
    }

    def checkCourseAndQuarter(course: Course, quarter: QuarterData, event: Event): Option[Warning] = {
        if(event.course.orNull != course)
            Some(new Warning(AppSettings.language.getItem("warning_courseNotMatching")))
        else if (quarter.getQuarter != event.quarter.orNull)
            Some(new Warning(AppSettings.language.getItem("warning_quarterNotMatching")))
        else None
    }

    def checkViability():Unit = {

        //TODO check precedences
        //if (warning != null) warning = checkEventPrecedences(course, quarter, event, week, interval)

        warning = checkCourseAndQuarter(course, quarter, event)

        if (warning.isEmpty) warning = checkBorders(event, interval)

        if (warning.isEmpty) warning = checkEventIncompatibilities(course, quarter, event, eventWeek, interval)

        if (warning.isEmpty) warning = checkResourceAvailability(course, quarter, event, eventWeek, interval)

        checked = true
    }
}
