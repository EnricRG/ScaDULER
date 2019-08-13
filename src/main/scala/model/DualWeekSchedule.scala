package model

class DualWeekSchedule[T](intervalsPerWeek: Int) {
    private val firstWeekSchedule = new Schedule[T](intervalsPerWeek)
    private val secondWeekSchedule = new Schedule[T](intervalsPerWeek)

    def getFirstWeekSchedule: Schedule[T] = firstWeekSchedule
    def getSecondWeekSchedule: Schedule[T] = secondWeekSchedule

    def getWeekSchedule(week: Int): Schedule[T] = week match{
        case 1 => getSecondWeekSchedule
        case _ => getFirstWeekSchedule
    }
}
