object Hours {

}

object Minutes{
    def minuteList = List(OClock,HalfHour)
    def minuteStringList = minuteList.map(_.toString)

    case object OClock{ override def toString = "00" }
    case object HalfHour{ override def toString = "30" }
}
