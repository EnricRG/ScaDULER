package model.blueprint

import model.ResourceSchedule

class ResourceBlueprint{
    var name: String = ""
    var quantity: Int = _ //TODO remove resource quantity
    var capacity: Int = _
    var availability: ResourceSchedule = _
}