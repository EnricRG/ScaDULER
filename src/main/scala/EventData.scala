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
                val lab_rooms_needed: Int = 0, val class_rooms_needed: Int = 0, val pc_rooms_needed: Int = 0,
                val incompatibilities: List[EventData] = null) {


    //Maybe this si unnecessary.
    val duration: Int = if (time_slots == 0)
                        if(start == null) 0
                        else if (end == null) 1
                        else (start.until(end, ChronoUnit.MINUTES)/MainTest.TimeSlotDuration).toInt
                   else time_slots


}
