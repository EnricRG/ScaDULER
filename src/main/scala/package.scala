package object service {
  type ID = Long
}

package object model{
  trait SubjectLike{
    def getName: String
  }
  trait CourseLike{
    def getName: String
  }
  trait ResourceLike{
    def getName: String
  }
  trait EventLike{
    def getName: String
  }
}
