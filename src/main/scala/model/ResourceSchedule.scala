package model

class ResourceSchedule(val intervals: Int) extends Schedule[Boolean](intervals){

    def set(interval: Int): Unit = updateInterval(interval, true)
    def unset(interval: Int): Unit = updateInterval(interval, false)

    //If the value did not exist, it's created with true as its state.
    def flip(interval: Int): Unit = updateInterval(interval, !getValueAtIntervalOrElse(interval, false))

    def getState(interval: Int): Option[Boolean] = getValueAtInterval(interval)
    def isAvailable(interval: Int): Boolean = getState(interval) match {
        case Some(state) => state
        case _ => false
    }
}
