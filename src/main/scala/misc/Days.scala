package misc

import app.AppSettings

@Deprecated
object Days {
    val dayList: List[Day] = List(Day1,Day2,Day3,Day4,Day5)
    def dayNumberList: List[Int] = dayList.map(_.toInt)
}

@Deprecated
sealed abstract class Day{
    override def toString: String
    def toInt: Int
}

@Deprecated
case object Day1 extends Day{
    override def toString = AppSettings.language.getItem("day1")
    override def toInt = 1
}

@Deprecated
case object Day2 extends Day{
    override def toString = AppSettings.language.getItem("day2")
    override def toInt = 2
}

@Deprecated
case object Day3 extends Day{
    override def toString = AppSettings.language.getItem("day3")
    override def toInt = 3
}

@Deprecated
case object Day4 extends Day{
    override def toString = AppSettings.language.getItem("day4")
    override def toInt = 4
}

@Deprecated
case object Day5 extends Day{
    override def toString = AppSettings.language.getItem("day5")
    override def toInt = 5
}
