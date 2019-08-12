package model

class ResourceSchedule(intervalsPerWeek: Int) extends DualWeekSchedule[Boolean](intervalsPerWeek){

    def set(week: Int, interval: Int): Unit = week match{
        case 0 => getFirstWeekSchedule.updateInterval(interval, true)
        case 1 => getSecondWeekSchedule.updateInterval(interval, true)
        case _ =>
    }
    def unset(week: Int, interval: Int): Unit = week match{
        case 0 => getFirstWeekSchedule.updateInterval(interval, false)
        case 1 => getSecondWeekSchedule.updateInterval(interval, false)
        case _ =>
    }

    //If the value did not exist, it's created with true as its state.
    def flip(week: Int, interval: Int): Unit = week match{
        case 0 => getFirstWeekSchedule.updateInterval(interval, !getFirstWeekSchedule.getValueAtIntervalOrElse(interval, false))
        case 1 => getSecondWeekSchedule.updateInterval(interval, !getSecondWeekSchedule.getValueAtIntervalOrElse(interval, false))
        case _ =>
    }

    def getState(week: Int, interval: Int): Option[Boolean] = week match{
        case 0 => getFirstWeekSchedule.getValueAtInterval(interval)
        case 1 => getSecondWeekSchedule.getValueAtInterval(interval)
        case _ => None
    }

    def isAvailable(week: Int, interval: Int): Boolean = getState(week, interval) match {
        case Some(state) => state
        case _ => false
    }
}
