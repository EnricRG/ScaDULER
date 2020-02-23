package factory;

import app.FXMLPaths;
import control.schedule.DualWeekScheduleViewController;
import control.schedule.WeekScheduleController;
import javafx.scene.Node;

import java.io.IOException;

public class DualWeekScheduleViewFactory<C extends DualWeekScheduleViewController<C1,C2>, C1 extends WeekScheduleController, C2 extends WeekScheduleController>
    extends ViewFactory<C>{

    public DualWeekScheduleViewFactory(C controller){
        super(FXMLPaths.DualWeekGenericSchedule());
        this.controller = controller;
    }

    @Override
    public Node load() throws IOException {
        Node n1 = new ScheduleViewFactory<>(controller.getFirstWeekController()).load();
        Node n2 = new ScheduleViewFactory<>(controller.getSecondWeekController()).load();

        Node n = super.load(controller); //main controller has to be loaded after the content of both weeks has been instantiated

        controller.setAWeekContent(n1);
        controller.setBWeekContent(n2);

        return n;
    }

    @Override
    public Node load(C controller) throws IOException {
        throw new UnsupportedOperationException();
    }
}
