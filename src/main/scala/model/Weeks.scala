package model

import app.AppSettings

object Weeks extends Serializable {

    sealed abstract class Week extends Serializable {
        def toString: String
        def toShortString: String
        def toWeekNumber: Int
        def periodicity: Periodicity
    }

    sealed abstract class Periodicity extends Serializable{
        def toString: String
    }
    object Periodicity{
        def fromInt(n: Int): Periodicity =  n match {
            case 2 => Biweekly
            case _ => Weekly
        }

        //not used because java doesn't like inner objects
        def weekly: Periodicity = Weekly
        def biweekly: Periodicity = Biweekly
    }
    case object Weekly extends Periodicity{
        override def toString: String = AppSettings.language.getItem("weekly")
    }
    case object Biweekly extends Periodicity{
        override def toString: String = AppSettings.language.getItem("biweekly")
    }

    case object AWeek extends Week{
        override def toString: String = AppSettings.language.getItem("aWeek")
        override def toShortString: String = AppSettings.language.getItem("shortAWeek")
        override def toWeekNumber: Int = 0
        override def periodicity: Periodicity = Biweekly
    }

    case object BWeek extends Week{
        override def toString: String = AppSettings.language.getItem("bWeek")
        override def toShortString: String = AppSettings.language.getItem("shortBWeek")
        override def toWeekNumber: Int = 1
        override def periodicity: Periodicity = Biweekly
    }

    case object EveryWeek extends Week{
        override def toString: String = AppSettings.language.getItem("everyWeek")
        override def toShortString: String = AppSettings.language.getItem("shortEveryWeek")
        override def toWeekNumber: Int = 2
        override def periodicity: Periodicity = Weekly
    }

    def periodicityList: List[Periodicity] = List(Weekly, Biweekly)
    def weekList: List[Week] = List(EveryWeek,AWeek,BWeek)

    def weekly: Periodicity = Weekly
    def biweekly: Periodicity = Biweekly

    def getEveryWeek: Week = EveryWeek
    def getAWeek: Week = AWeek
    def getBWeek: Week = BWeek
}
