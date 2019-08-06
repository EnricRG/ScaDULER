package model

import app.AppSettings

case class Quarter(var resources: Traversable[CourseResource])

//TODO: Add course resource list
//TODO: Add course assigned event list
case class Course(name: String, var descriptionOption: Option[String] = None,
                  var firstQuarter: Quarter, var secondQuarter: Quarter) {

    def description: String = descriptionOption match{
        case Some(description) => description
        case None => AppSettings.language.getItem("course_emptyDescription")
    }
}
