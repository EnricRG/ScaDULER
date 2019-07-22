sealed abstract class Week {
    override def toString: String
    def toShortString: String
}
/*
object AWeek extends Week{ override def toString = Application.Language.getItem("aweek") }
object BWeek extends Week{ override def toString = Application.Language.getItem("bweek") }
object EveryWeek extends Week{ override def toString = Application.Language.getItem("everyweek") }
*/
object AWeek extends Week{
    override def toString = AppSettings.Language.getItem("aWeek")
    override def toShortString: String = AppSettings.Language.getItem("shortAWeek")
}

object BWeek extends Week{
    override def toString = AppSettings.Language.getItem("bWeek")
    override def toShortString: String = AppSettings.Language.getItem("shortBWeek")
}

object EveryWeek extends Week{
    override def toString = AppSettings.Language.getItem("everyWeek")
    override def toShortString: String = AppSettings.Language.getItem("shortEveryWeek")
}

object Weeks{
    def weekList: List[Week] = List(EveryWeek,AWeek,BWeek)
    def weekStringList = weekList.map(_.toString)
}