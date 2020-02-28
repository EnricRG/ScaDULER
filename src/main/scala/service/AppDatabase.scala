package service

class AppDatabase (eventDBInitializer: Option[EventDatabase#Initializer] = None,
                   subjectDBInitializer: Option[SubjectDatabase#Initializer] = None,
                   courseDBInitializer: Option[CourseDatabase#Initializer] = None,
                   resourceDBInitializer: Option[ResourceDatabase#Initializer] = None) extends Serializable {

    lazy val eventDatabase: EventDatabase = eventDBInitializer match {
        case Some(eDBi) => new EventDatabase(eDBi)
        case None => new EventDatabase
    }

    lazy val subjectDatabase: SubjectDatabase = subjectDBInitializer match {
        case Some(sDBi) => new SubjectDatabase(sDBi)
        case None => new SubjectDatabase
    }

    lazy val courseDatabase: CourseDatabase = courseDBInitializer match {
        case Some(cDBi) => new CourseDatabase(cDBi)
        case None => new CourseDatabase
    }

    lazy val resourceDatabase: ResourceDatabase = resourceDBInitializer match {
        case Some(crDBi) => new ResourceDatabase(crDBi)
        case None => new ResourceDatabase
    }

}

class ReadOnlyAppDatabase(val eventDatabase: ReadOnlyEventDatabase,
                          val subjectDatabase: ReadOnlySubjectDatabase,
                          val courseDatabase: ReadOnlyCourseDatabase,
                          val resourceDatabase: ReadOnlyResourceDatabase)
