package service

class AppDatabase (eventDBInitializer: Option[EventDatabase#Initializer] = None,
                   subjectDBInitializer: Option[SubjectDatabase#Initializer] = None,
                   courseDBInitializer: Option[CourseDatabase#Initializer] = None,
                   courseResourceDBInitializer: Option[CourseResourceDatabase#Initializer] = None) {

    val eventDatabase: EventDatabase = eventDBInitializer match {
        case Some(eDBi) => new EventDatabase(eDBi)
        case None => new EventDatabase
    }

    val subjectDatabase: SubjectDatabase = subjectDBInitializer match {
        case Some(sDBi) => new SubjectDatabase(sDBi)
        case None => new SubjectDatabase
    }

    val courseDatabase: CourseDatabase = courseDBInitializer match {
        case Some(cDBi) => new CourseDatabase(cDBi)
        case None => new CourseDatabase
    }

    val courseResourceDatabase: CourseResourceDatabase = courseResourceDBInitializer match {
        case Some(crDBi) => new CourseResourceDatabase(crDBi)
        case None => new CourseResourceDatabase
    }

}
