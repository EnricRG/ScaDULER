package control.schedule;

import control.MainController;
import model.Event;

public class UnassignedEventViewController extends EventViewController {

    protected final MainController mainController;

    public UnassignedEventViewController(MainController mainController, Event event) {
        super(event);
        this.mainController = mainController;
    }

    @Override
    protected void initializeBehavior() {
        mainBox.setOnDragDetected(event -> {
            mainBox.startFullDrag();
            this.mainController.startEventDrag(getEvent(), MainController.EventDrag.FROM_UNASSIGNED, this, null);
            event.consume();
        });
    }
}
