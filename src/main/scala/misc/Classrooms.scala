package misc

import app.AppSettings

object Classrooms{
    val roomTypes = List(LabRoom, ClassRoom, PcRoom)
}

sealed abstract class ClassRoom

case object LabRoom extends ClassRoom{
    override def toString = AppSettings.language.getItem("labRoom")
}
case object ClassRoom extends ClassRoom {
    override def toString = AppSettings.language.getItem("classRoom")
}
case object PcRoom extends ClassRoom {
    override def toString = AppSettings.language.getItem("pcRoom")
}
