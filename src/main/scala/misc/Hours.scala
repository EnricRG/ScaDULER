package misc

import app.AppSettings
import misc.Minutes.HalfHour

case class Duration(duration: Int){
    override def toString: String = Duration.asPrettyString(duration)
    def toInt: Int = duration
}

object Duration{
    def getDurations: List[Duration] = (for(i <- 1 to AppSettings.maxEventDuration) yield Duration(i)).toList
    def asPrettyString(duration: Int): String = {
        if(duration == 1) HalfHour.toString + "m"
        else{
            val hours = (duration/AppSettings.TimeSlotsPerHour).toString
            hours + "h " + ((duration % AppSettings.TimeSlotsPerHour)*AppSettings.TimeSlotDuration).toString + "m"
        }
    }
}

object Hours {
    def hourList: List[Int] = (AppSettings.dayStart to AppSettings.dayEnd).toList
}

object Minutes{
    def minuteList = List(OClock,HalfHour)

    case object OClock{
        override def toString = "00"
        def toMinutes: Int = 0
    }
    case object HalfHour{
        override def toString = "30"
        def toMinutes: Int = 30
    }
}
