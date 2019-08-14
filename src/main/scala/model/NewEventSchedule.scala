package model

import scala.collection.mutable.ListBuffer

class NewEventSchedule(intervalsPerWeek: Int) extends DualWeekSchedule[ListBuffer[NewEvent]](intervalsPerWeek){


    //def getEventsAtInterval(week: Int = 0, interval: Int): Option[ListBuffer[NewEvent]] = getWeekSchedule(week).getValueAtInterval(interval)
    //def getEventsAtIntervalOrElse(week: Int = 0, interval: Int): ListBuffer[NewEvent] = getWeekSchedule(week).getValueAtIntervalOrElse(interval, new ListBuffer)
    def getEventsAtIntervalOrElseCreate(week: Int = 0, interval: Int): ListBuffer[NewEvent] =
        getWeekSchedule(week).getValueAtIntervalOrElseUpdate(interval, new ListBuffer)
    def addEvent(week: Int = 0, interval: Int, event: NewEvent): ListBuffer[NewEvent] =
        getEventsAtIntervalOrElseCreate(week,interval) += event
}
