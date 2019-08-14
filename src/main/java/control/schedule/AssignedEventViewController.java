package control.schedule;

import control.MainController;
import model.NewEvent;

public class AssignedEventViewController extends EventViewController {

    public AssignedEventViewController(MainController controller, NewEvent event) {
        super(controller, event);
    }

    public AssignedEventViewController(MainController controller, UnassignedEventViewController oldController) {
        super(controller, oldController.getEvent());
    }

    @Override
    protected void initializeBehavior() {

    }
}
