package model

import app.AppSettings

object Weeks extends Serializable {

    @SerialVersionUID(1L)
    sealed abstract class Week extends Serializable {
        def toString: String
        def toShortString: String
        def toWeekNumber: Int
        def periodicity: Periodicity
    }

    @SerialVersionUID(1L)
    sealed abstract class Periodicity extends Serializable{
        def toString: String
        def toShortString: String
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
        override def toShortString: String = AppSettings.language.getItem("shortWeekly")
    }

    case object Biweekly extends Periodicity{
        override def toString: String = AppSettings.language.getItem("biweekly")
        override def toShortString: String = AppSettings.language.getItem("shortBiweekly")
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

    def fromWeekNumber(i: Int): Week =
        if (i == AWeek.toWeekNumber) AWeek
        else if (i == BWeek.toWeekNumber) BWeek
        else EveryWeek

    def periodicityList: List[Periodicity] = List(Weekly, Biweekly)
    def weekList: List[Week] = List(EveryWeek,AWeek,BWeek)

    def weekly: Periodicity = Weekly
    def biweekly: Periodicity = Biweekly

    def getEveryWeek: Week = EveryWeek
    def getAWeek: Week = AWeek
    def getBWeek: Week = BWeek
}
