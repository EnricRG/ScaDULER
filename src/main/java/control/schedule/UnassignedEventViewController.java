package control.schedule;

import control.MainController;
import model.Event;

public class UnassignedEventViewController extends EventViewController {

    protected final MainController controller;

    public UnassignedEventViewController(MainController controller, Event event) {
        super(event);
        this.controller = controller;
    }

    @Override
    protected void initializeBehavior() {
        mainBox.setOnDragDetected(event -> {
            mainBox.startFullDrag();
            this.controller.startEventDrag(getEvent(), MainController.EventDrag.FROM_UNASSIGNED, this, null);
            event.consume();
        });
    }
}
