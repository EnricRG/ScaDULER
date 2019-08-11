package factory;

import app.AppSettings;
import app.FXMLPaths;
import control.ScheduleController;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.io.IOException;

public class ScheduleViewFactory<C extends ScheduleController> extends ViewFactory<ScheduleController> {

    public ScheduleViewFactory(C controller) {
        super(FXMLPaths.GenericSchedule());
        this.controller = controller;
    }

    @Override
    public Node load() throws IOException {
        return super.load(controller);
    }
}
