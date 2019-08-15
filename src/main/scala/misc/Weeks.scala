package misc

import app.AppSettings

object Weeks extends Serializable {

    sealed abstract class Week extends Serializable {
        def toString: String
        def toShortString: String
    }

    case object AWeek extends Week{
        override def toString = AppSettings.language.getItem("aWeek")
        override def toShortString: String = AppSettings.language.getItem("shortAWeek")
    }

    case object BWeek extends Week{
        override def toString = AppSettings.language.getItem("bWeek")
        override def toShortString: String = AppSettings.language.getItem("shortBWeek")
    }

    case object EveryWeek extends Week{
        override def toString = AppSettings.language.getItem("everyWeek")
        override def toShortString: String = AppSettings.language.getItem("shortEveryWeek")
    }

    def weekList: List[Week] = List(EveryWeek,AWeek,BWeek)
}



