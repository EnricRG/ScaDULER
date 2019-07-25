package misc

import app.AppSettings

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
