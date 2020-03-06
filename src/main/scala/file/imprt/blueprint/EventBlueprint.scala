package file.imprt.blueprint

import model.EventType
import model.Weeks.Periodicity

class EventBlueprint{
    var name: String = ""
    var shortName: String = ""
    var neededResource: Option[ResourceBlueprint] = _
    var eventType: EventType = _
    var subject: Option[SubjectBlueprint] = _
    var periodicity: Periodicity = _
    var duration: Int = _
}