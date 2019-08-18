package misc

import app.AppSettings

object Weeks extends Serializable {

    sealed abstract class Week extends Serializable {
        def toString: String
        def toShortString: String
        def toWeekNumber: Int
    }

    case object AWeek extends Week{
        override def toString: String = AppSettings.language.getItem("aWeek")
        override def toShortString: String = AppSettings.language.getItem("shortAWeek")
        override def toWeekNumber: Int = 0
    }

    case object BWeek extends Week{
        override def toString: String = AppSettings.language.getItem("bWeek")
        override def toShortString: String = AppSettings.language.getItem("shortBWeek")
        override def toWeekNumber: Int = 1
    }

    case object EveryWeek extends Week{
        override def toString: String = AppSettings.language.getItem("everyWeek")
        override def toShortString: String = AppSettings.language.getItem("shortEveryWeek")
        override def toWeekNumber: Int = 2
    }

    def weekList: List[Week] = List(EveryWeek,AWeek,BWeek)

    def getEveryWeek: Week = EveryWeek
    def getAWeek: Week = AWeek
    def getBWeek: Week = BWeek
}



