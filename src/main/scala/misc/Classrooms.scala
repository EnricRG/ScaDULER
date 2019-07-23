package misc

import app.AppSettings

object Classrooms{
    val roomTypes = List(LabRoom, ClassRoom, PcRoom)
    val stringRoomTypes = roomTypes.map(_.toString)
}

sealed abstract class ClassRoom

case object LabRoom extends ClassRoom{
    override def toString = AppSettings.Language.getItem("labRoom")
}
case object ClassRoom extends ClassRoom {
    override def toString = AppSettings.Language.getItem("classRoom")
}
case object PcRoom extends ClassRoom {
    override def toString = AppSettings.Language.getItem("pcRoom")
}
