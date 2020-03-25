package factory

import java.io.IOException

import app.FXMLPaths
import control.schedule.{DualWeekScheduleViewController, WeekScheduleController}

import javafx.scene.Node

class DualWeekScheduleViewFactory[C <: DualWeekScheduleViewController[C1, C2],
    C1 <: WeekScheduleController,
    C2 <: WeekScheduleController] (controller: C) extends ViewFactory[C](FXMLPaths.DualWeekGenericSchedule) {

    @throws[IOException]
    override def load: Node = {
        val n1 = new ScheduleViewFactory[C1](controller.getFirstWeekController).load
        val n2 = new ScheduleViewFactory[C2](controller.getSecondWeekController).load

        //main controller has to be loaded after the content of both weeks has been instantiated
        val n = super.load(controller)

        controller.setAWeekContent(n1)
        controller.setBWeekContent(n2)

        n
    }

    @throws[UnsupportedOperationException]
    override def load(controller: C) = throw new UnsupportedOperationException
}
