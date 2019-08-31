package model

import app.AppSettings
import misc.Weeks.{AWeek, BWeek, EveryWeek}

@SerialVersionUID(1L)
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

    def isAvailable(week: Int, interval: Int): Boolean = {
        if (week == EveryWeek.toWeekNumber) {
            val aState = getState(AWeek.toWeekNumber, interval) match {
                case Some(state) => state
                case _ => false
            }
            val bState = getState(BWeek.toWeekNumber, interval) match {
                case Some(state) => state
                case _ => false
            }
            aState && bState
        }
        else getState(week, interval) match {
            case Some(state) => state
            case _ => false
        }
    }

    //post sorted Iterable of unavailable intervals
    //def getUnavailableIntervals(week: Int): Iterable[Int] = (0 until intervalsPerWeek).filter(!isAvailable(week,_))
    //post sorted Iterable of unavailable intervals
    //def getUnavailableIntervalsOrElse(week: Int, el: Int): Iterable[Int] =
    //    (0 until intervalsPerWeek).map(x => if (!isAvailable(week, x)) x else el)

    //pre: day < 5 && day >= 0
    //post sorted Iterable of unavailable intervals at that day
    def getUnavailableIntervalsOrElse(week: Int, day: Int, el: Int): Iterable[Int] =
        (day*AppSettings.timeSlotsPerDay until (day+1)*AppSettings.timeSlotsPerDay).map(x => if (!isAvailable(week, x)) x else el)
}
