package model

import app.AppSettings

//invariant: quantity <= resource.quantity
case class CourseResource(val resource: Resource, var quantity: Int) extends QuantifiableResource{
    def getName: String = resource.getName
    override def getQuantity: Int = quantity
    override def getAvailableQuantity: Int = getQuantity //TODO: check resource availability
    def getResource: Resource = resource
    def incrementQuantity(incr: Int): Unit = quantity += incr
    def decrementQuantity(decr: Int): Unit = quantity -= decr
}

case class Quarter(var resources: Iterable[CourseResource])

//TODO: Add course resource list
//TODO: Add course assigned event list
case class Course(val name: String, var descriptionOption: Option[String] = None,
                  var firstQuarter: Quarter, var secondQuarter: Quarter) {

    def description: String = descriptionOption match{
        case Some(description) => description
        case None => AppSettings.language.getItem("course_emptyDescription")
    }
}
