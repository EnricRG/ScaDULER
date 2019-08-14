package factory;

import app.FXMLPaths;
import control.schedule.CourseScheduleController;
import control.schedule.QuarterScheduleController;
import javafx.scene.Node;

import java.io.IOException;

public class CourseScheduleViewFactory extends ViewFactory<CourseScheduleController> {

    private final CourseScheduleController controller;

    public CourseScheduleViewFactory(CourseScheduleController controller) {
        super(FXMLPaths.CourseSchedule());
        this.controller = controller;
    }

    @Override
    public Node load() throws IOException {
        Node q1 = new DualWeekScheduleViewFactory<>(controller.getFirstQuarterController()).load();
        Node q2 = new DualWeekScheduleViewFactory<>(controller.getSecondQuarterController()).load();

        Node n = super.load(controller);

        controller.setFirstQuarterContent(q1);
        controller.setSecondQuarterContent(q2);

        return n;
    }

    @Override
    public Node load(CourseScheduleController controller) throws IOException {
        throw new UnsupportedOperationException();
    }
}
