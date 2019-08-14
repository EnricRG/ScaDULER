package control.schedule;

import model.NewEvent;

public class UnassignedEventViewController extends EventViewController {

    public UnassignedEventViewController(QuarterScheduleController controller, NewEvent event) {
        super(controller,event);
    }

    @Override
    protected void initializeBehavior() {
        mainBox.setOnDragDetected(event -> {
            mainBox.startFullDrag();
            this.controller.setEventDragSource(this);
        });
        mainBox.setOnDragDropped(event -> {
            this.controller.finishEventDrag();
        });
    }
}
