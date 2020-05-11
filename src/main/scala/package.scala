package object service {
  type ID = Long
}

package object model{
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
}
