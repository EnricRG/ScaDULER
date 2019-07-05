import java.time.LocalTime
import java.time.temporal.ChronoUnit

object Weeks{
    val AWeek = "A"
    val BWeek = "B"
    val EveryWeek = "W"
}

class EventData(val num: Int, val start: LocalTime = null, val end: LocalTime = null,
                val relativeStart: Int = 0, time_slots: Int = 0, val week: String = Weeks.EveryWeek,
                val name: String = null, val description: String = null,
                val labRoomsNeeded: Int = 0, val classRoomsNeeded: Int = 0, val pcRoomsNeeded: Int = 0,
                var incompatibilities: List[EventData] = List()) {


    //Maybe this si unnecessary.
    val duration: Int = if (time_slots == 0)
                        if(start == null) 0
                        else if (end == null) 1
                        else (start.until(end, ChronoUnit.MINUTES)/MainTest.TimeSlotDuration).toInt
                   else time_slots


}
