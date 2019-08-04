package model

import app.AppSettings
import misc.Quarters.Quarter

//TODO: Add course resource list
//TODO: Add course assigned event list
case class Course(val name: String, var descriptionOption: Option[String] = None,
             var quarter: Quarter, var resources: Traversable[CourseResource]) {

    def description: String = descriptionOption match{
        case Some(description) => description
        case None => AppSettings.language.getItem("course_emptyDescription")
    }
}
