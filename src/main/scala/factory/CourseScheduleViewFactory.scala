package factory

import java.io.IOException

import app.FXMLPaths
import control.schedule.{CourseScheduleController, QuarterScheduleController, WeekScheduleController}
import javafx.scene.Node

class CourseScheduleViewFactory(controller: CourseScheduleController) extends
    ViewFactory[CourseScheduleController](FXMLPaths.CourseSchedule) {

    @throws[IOException]
    override def load: Node = {
        //TODO check type inference
        val q1 = new DualWeekScheduleViewFactory[QuarterScheduleController, WeekScheduleController, WeekScheduleController](controller.getFirstQuarterController).load
        val q2 = new DualWeekScheduleViewFactory[QuarterScheduleController, WeekScheduleController, WeekScheduleController](controller.getSecondQuarterController).load

        val n = super.load(controller)

        controller.setFirstQuarterContent(q1)
        controller.setSecondQuarterContent(q2)
        n
    }

    @throws[UnsupportedOperationException]
    override def load(controller: CourseScheduleController) = throw new UnsupportedOperationException
}
