package factory;

import app.FXMLPaths;
import control.schedule.ScheduleController;
import javafx.scene.Node;

import java.io.IOException;

public class ScheduleViewFactory<C extends ScheduleController> extends ViewFactory<C> {

    public ScheduleViewFactory(C controller) {
        super(FXMLPaths.GenericSchedule());
        this.controller = controller;
    }

    @Override
    public Node load() throws IOException {
        return super.load(controller);
    }
}
