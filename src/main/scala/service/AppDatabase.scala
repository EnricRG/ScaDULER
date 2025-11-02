package service

class AppDatabase (eventDBInitializer: Option[EventDatabase] = None,
                   subjectDBInitializer: Option[SubjectDatabase] = None,
                   courseDBInitializer: Option[CourseDatabase] = None,
                   resourceDBInitializer: Option[ResourceDatabase] = None) extends Serializable {

    def this(eventDBInitializer: EventDatabase,
             subjectDBInitializer: SubjectDatabase,
             courseDBInitializer: CourseDatabase,
             resourceDBInitializer: ResourceDatabase) = {
      this(Some(eventDBInitializer), Some(subjectDBInitializer), Some(courseDBInitializer), Some(resourceDBInitializer))
    }

    lazy val eventDatabase: EventDatabase = eventDBInitializer match {
        case Some(eDB) => eDB
        case None => new EventDatabase
    }

    lazy val subjectDatabase: SubjectDatabase = subjectDBInitializer match {
        case Some(sDB) => sDB
        case None => new SubjectDatabase
    }

    lazy val courseDatabase: CourseDatabase = courseDBInitializer match {
        case Some(cDB) => cDB
        case None => new CourseDatabase
    }

    lazy val resourceDatabase: ResourceDatabase = resourceDBInitializer match {
        case Some(rDB) => rDB
        case None => new ResourceDatabase
    }

    def getReadOnlyDatabase: ReadOnlyAppDatabase = null

}

class ReadOnlyAppDatabase(val eventDatabase: ReadOnlyEventDatabase,
                          val subjectDatabase: ReadOnlySubjectDatabase,
                          val courseDatabase: ReadOnlyCourseDatabase,
                          val resourceDatabase: ReadOnlyResourceDatabase)
