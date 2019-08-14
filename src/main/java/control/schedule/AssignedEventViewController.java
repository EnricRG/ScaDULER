package control.schedule;

public class AssignedEventViewController extends EventViewController {

    private final UnassignedEventViewController oldController;

    public AssignedEventViewController(QuarterScheduleController controller, UnassignedEventViewController oldController) {
        super(controller, oldController.getEvent());
        this.oldController = oldController;
    }

    @Override
    protected void initializeBehavior() {

    }
}
