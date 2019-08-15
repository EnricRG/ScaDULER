package model

import app.AppSettings
import service.Identifiable

//invariant: quantity <= resource.quantity
case class CourseResource(val resource: Resource, var quantity: Int) extends QuantifiableResource with Serializable {
    override def getQuantity: Int = quantity
    override def getAvailableQuantity: Int = getQuantity //TODO: truly check resource availability

    def getName: String = resource.getName
    def getResource: Resource = resource

    def incrementQuantity(incr: Int): Unit = quantity += incr
    def decrementQuantity(decr: Int): Unit = quantity -= decr
}

//TODO finish schedule adoption
case class Quarter(var resources: Iterable[CourseResource], var schedule: NewEventSchedule = new NewEventSchedule(AppSettings.timeSlots)) extends Serializable {
    def resourceTypeCount: Int = resources.size
    def resourceAmount: Int = resources.map(_.getQuantity).sum
}

//TODO: update to new database model
//TODO: Add course assigned event list
case class Course(val name: String, var descriptionOption: Option[String] = None,
                  var firstQuarter: Quarter, var secondQuarter: Quarter) extends Identifiable with Serializable {

    def description: String = descriptionOption match{
        case Some(description) => description
        case None => AppSettings.language.getItem("course_emptyDescription")
    }
}
