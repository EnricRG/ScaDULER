package file.imprt.blueprint

import model.{EventType, Quarter}
import model.Weeks.Periodicity

class EventBlueprint{
    var name: String = ""
    var shortName: String = ""
    var neededResource: Option[ResourceBlueprint] = _
    var eventType: EventType = _
    var subject: Option[SubjectBlueprint] = _
    var periodicity: Periodicity = _
    var duration: Int = _
    var course: CourseBlueprint = _ //unused
    var quarter: Quarter = _ //unused
}
