package control.schedule;

import model.NewEvent;

public class AssignedEventViewController extends EventViewController {

    private final UnassignedEventViewController oldController;

    public AssignedEventViewController(CourseScheduleController controller, UnassignedEventViewController oldController) {
        super(controller, oldController.getEvent());
        this.oldController = oldController;
    }

    @Override
    protected void initializeBehavior() {

    }
}
