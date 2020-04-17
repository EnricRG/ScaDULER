package object service {
  type ID = Long
}

package object model{
  trait SubjectLike{
    @deprecated
    def getName: String
  }
  trait CourseLike{
    @deprecated
    def getName: String
  }
  trait ResourceLike{
    @deprecated
    def getName: String
    def name: String
    def capacity: Int
    def incrementCapacity(increment: Int)
    def decrementCapacity(decrement: Int)
    def getAvailability(week: Int, interval: Int): Int
    def getAvailability: ResourceSchedule
  }
  trait EventLike{
    @deprecated
    def getName: String
  }
}
