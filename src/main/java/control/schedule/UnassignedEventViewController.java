package control.schedule;

import control.MainController;
import model.NewEvent;

public class UnassignedEventViewController extends EventViewController {

    protected final MainController controller;

    public UnassignedEventViewController(MainController controller, NewEvent event) {
        super(event);
        this.controller = controller;
    }

    @Override
    protected void initializeBehavior() {
        mainBox.setOnDragDetected(event -> {
            mainBox.startFullDrag();
            System.out.println("Drag Started");
            this.controller.startEventDrag(MainController.EventDrag.FROM_UNASSIGNED, controller, this, null);
            event.consume();
        });
    }
}
