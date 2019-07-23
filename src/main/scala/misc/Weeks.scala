package misc

import app.AppSettings

object Weeks{

    sealed abstract class Week {
        override def toString: String
        def toShortString: String
    }

    case object AWeek extends Week{
        override def toString = AppSettings.Language.getItem("aWeek")
        override def toShortString: String = AppSettings.Language.getItem("shortAWeek")
    }

    case object BWeek extends Week{
        override def toString = AppSettings.Language.getItem("bWeek")
        override def toShortString: String = AppSettings.Language.getItem("shortBWeek")
    }

    case object EveryWeek extends Week{
        override def toString = AppSettings.Language.getItem("everyWeek")
        override def toShortString: String = AppSettings.Language.getItem("shortEveryWeek")
    }

    def weekList: List[Week] = List(EveryWeek,AWeek,BWeek)
    def weekStringList = weekList.map(_.toString)
}



