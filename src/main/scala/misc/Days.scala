package misc

import app.AppSettings

object Days {
    val dayList: List[Day] = List(Day1,Day2,Day3,Day4,Day5)
    def dayStringList: List[String] = dayList.map(_.toString)
    def dayIntList: List[Int] = dayList.map(_.toInt)
}

sealed abstract class Day{
    override def toString: String
    def toInt: Int
}

case object Day1 extends Day{
    override def toString = AppSettings.Language.getItem("day1")
    override def toInt = 1
}

case object Day2 extends Day{
    override def toString = AppSettings.Language.getItem("day2")
    override def toInt = 2
}

case object Day3 extends Day{
    override def toString = AppSettings.Language.getItem("day3")
    override def toInt = 3
}

case object Day4 extends Day{
    override def toString = AppSettings.Language.getItem("day4")
    override def toInt = 4
}

case object Day5 extends Day{
    override def toString = AppSettings.Language.getItem("day5")
    override def toInt = 5
}
