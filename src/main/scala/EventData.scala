//package data

import java.time.LocalTime
import java.time.temporal.ChronoUnit

class EventData(val num: Int, var start: LocalTime = null, var end: LocalTime = null,
                var relativeStart: Int = 0, time_slots: Int = 0, var week: Week = EveryWeek,
                var name: String = null, var short_name: String = null, var description: String = null,
                var labRoomsNeeded: Int = 0, var classRoomsNeeded: Int = 0, var pcRoomsNeeded: Int = 0,
                var incompatibilities: List[EventData] = List()) {


    //Maybe this is unnecessary.
    val duration: Int = if (time_slots == 0)
                        if(start == null) 0
                        else if (end == null) 1
                        else 0 //(start.until(end, ChronoUnit.MINUTES)/Application.TimeSlotDuration).toInt
                   else time_slots


}
