package model.blueprint

import model.Quarter

import scala.collection.mutable

class SubjectBlueprint{
    var name: String = ""
    var shortName: String = ""
    var description: String = ""
    var course: CourseBlueprint = _
    var quarter: Quarter = _
    val events: mutable.Set[EventBlueprint] = new mutable.HashSet
    val additionalInformation: mutable.Map[String, Any] = new mutable.HashMap
}
