package file.imprt.blueprint

import scala.collection.mutable

class SubjectBlueprint{
    var name: String = ""
    var shortName: String = ""
    var description: String = ""
    var desiredCourse: CourseBlueprint = _
    var desiredQuarter: Int = _
    val events: mutable.Set[EventBlueprint] = new mutable.HashSet
}
