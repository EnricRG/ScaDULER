sealed abstract class Week {
    override def toString: String
}
/*
object AWeek extends Week{ override def toString = Application.Language.getItem("aweek") }
object BWeek extends Week{ override def toString = Application.Language.getItem("bweek") }
object EveryWeek extends Week{ override def toString = Application.Language.getItem("everyweek") }
*/
object AWeek extends Week{ override def toString = "A" }
object BWeek extends Week{ override def toString = "B" }
object EveryWeek extends Week{ override def toString = "W" }