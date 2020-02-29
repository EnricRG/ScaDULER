package file.imprt.blueprint

import scala.collection.mutable

class SubjectBlueprint{
    var name: String = _
    var shortName: String = _
    var desiredCourse: CourseBlueprint = _
    var desiredQuarter: Int = _
    val events: mutable.Set[EventBlueprint] = new mutable.HashSet
}
