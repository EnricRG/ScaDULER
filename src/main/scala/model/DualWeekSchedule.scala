package model

class DualWeekSchedule[T](intervalsPerWeek: Int) {
    private val firstWeekSchedule = new Schedule[T](intervalsPerWeek)
    private val secondWeekSchedule = new Schedule[T](intervalsPerWeek)

    def getFirstWeekSchedule: Schedule[T] = firstWeekSchedule
    def getSecondWeekSchedule: Schedule[T] = secondWeekSchedule
}
