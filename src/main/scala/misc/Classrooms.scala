package misc

import app.AppSettings

@Deprecated
object Classrooms{
    val roomTypes = List(LabRoom, ClassRoom, PcRoom)
}

@Deprecated
sealed abstract class ClassRoom

@Deprecated
case object LabRoom extends ClassRoom{
    override def toString = AppSettings.language.getItem("labRoom")
}
@Deprecated
case object ClassRoom extends ClassRoom {
    override def toString = AppSettings.language.getItem("classRoom")
}
@Deprecated
case object PcRoom extends ClassRoom {
    override def toString = AppSettings.language.getItem("pcRoom")
}
