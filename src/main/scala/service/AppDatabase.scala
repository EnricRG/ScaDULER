package service

class AppDatabase (eventDBInitializer: Option[EventDatabaseInitializer] = None,
                   subjectDBInitializer: Option[SubjectDatabaseInitializer] = None,
                   courseDBInitializer: Option[CourseDatabaseInitializer] = None) {

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

}
