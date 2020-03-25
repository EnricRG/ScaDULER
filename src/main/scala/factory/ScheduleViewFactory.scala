package factory

import java.io.IOException

import app.FXMLPaths
import control.schedule.WeekScheduleController
import javafx.scene.Node

class ScheduleViewFactory[C <: WeekScheduleController](controller: C) extends ViewFactory[C](FXMLPaths.GenericSchedule) {

    @throws[IOException]
    override def load: Node = super.load(controller)
}