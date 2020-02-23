package factory;

import app.FXMLPaths;
import control.schedule.WeekScheduleController;
import javafx.scene.Node;

import java.io.IOException;

public class ScheduleViewFactory<C extends WeekScheduleController> extends ViewFactory<C> {

    public ScheduleViewFactory(C controller) {
        super(FXMLPaths.GenericSchedule());
        this.controller = controller;
    }

    @Override
    public Node load() throws IOException {
        return super.load(controller);
    }
}
