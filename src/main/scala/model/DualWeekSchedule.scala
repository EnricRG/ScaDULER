package model

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@SerialVersionUID(1L)
class DualWeekSchedule[T](intervalsPerWeek: Int) extends Serializable {

    @JsonProperty("w1s")
    @JsonDeserialize(using = classOf[ScheduleDeserializer[T]])
    private val firstWeekSchedule = new Schedule[T](intervalsPerWeek)

    @JsonProperty("w2s")
    @JsonDeserialize(using = classOf[ScheduleDeserializer[T]])
    private val secondWeekSchedule = new Schedule[T](intervalsPerWeek)



    @JsonIgnore
    def getFirstWeekSchedule: Schedule[T] = firstWeekSchedule

    @JsonIgnore
    def getSecondWeekSchedule: Schedule[T] = secondWeekSchedule

    @JsonIgnore
    def getWeekSchedule(week: Int): Schedule[T] = week match{
        case 1 => getSecondWeekSchedule
        case _ => getFirstWeekSchedule
    }

    override def toString: String = firstWeekSchedule.toString + "\n" + secondWeekSchedule.toString
}
