package model

import app.AppSettings
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import model.Weeks.{AWeek, BWeek, EveryWeek}

@SerialVersionUID(1L)
class ResourceSchedule(@JsonProperty("ipw") intervalsPerWeek: Int)
    extends DualWeekSchedule[Int](intervalsPerWeek){

    def get(week: Int, interval: Int): Int = week match{
        case 0 | 2 => getFirstWeekSchedule.getValueAtIntervalOrElse(interval,0)
        case 1 => getSecondWeekSchedule.getValueAtIntervalOrElse(interval,0)
        case _ => 0
    }
    def set(week: Int, interval: Int, amount: Int): Unit = if(amount > 0) week match{
        case 0 => getFirstWeekSchedule.updateInterval(interval, amount)
        case 1 => getSecondWeekSchedule.updateInterval(interval, amount)
        case 2 => set(0, interval, amount); set(1, interval, amount)
        case _ =>
    }

    def unset(week: Int, interval: Int): Unit = week match{
        case 0 => getFirstWeekSchedule.removeInterval(interval)
        case 1 => getSecondWeekSchedule.removeInterval(interval)
        case 2 => unset(0, interval); unset(1, interval)
        case _ =>
    }

    def increment(week: Int, interval: Int, amount: Int): Unit = {
        val v = get(week, interval)
        if(v + amount > 0) set(week, interval, v + amount)
        else if (v > 0) unset(week, interval) //if the result of adding is below zero and was previously set
    }

    def decrement(week: Int, interval: Int, amount: Int): Unit =
        increment(week, interval, -amount)

    def isAvailable(week: Int, interval: Int): Boolean = {
        if (week == EveryWeek.toWeekNumber) {
            val aAvailability = get(AWeek.toWeekNumber, interval)
            val bAvailability = get(BWeek.toWeekNumber, interval)
            aAvailability > 0 && bAvailability > 0
        }
        else get(week, interval) > 0
    }

    @JsonIgnore
    def getMax: Int = (getFirstWeekSchedule.getAllElements ++ getSecondWeekSchedule.getAllElements ++ List(0)).max

    def getIntervalsWith(week: Int, day:Int, quantity: Int, el: Int): Iterable[Int] =
        (day*AppSettings.timeSlotsPerDay until (day+1)*AppSettings.timeSlotsPerDay).map(i => if (get(week, i) == quantity) i else el)

    //pre: day < 5 && day >= 0
    //post sorted Iterable of unavailable intervals at that day
    def getUnavailableIntervalsOrElse(week: Int, day: Int, el: Int): Iterable[Int] =
        (day*AppSettings.timeSlotsPerDay until (day+1)*AppSettings.timeSlotsPerDay).map(x => if (!isAvailable(week, x)) x else el)
}
