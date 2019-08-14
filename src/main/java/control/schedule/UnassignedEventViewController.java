package control.schedule;

import control.MainController;
import model.NewEvent;

public class UnassignedEventViewController extends EventViewController {

    public UnassignedEventViewController(MainController controller, NewEvent event) {
        super(controller,event);
    }

    @Override
    protected void initializeBehavior() {
        mainBox.setOnDragDetected(event -> {
            mainBox.startFullDrag();
            System.out.println("Drag Started");
            this.controller.setEventDragSource(this);
        });
        /*mainBox.setOnDragDropped(event -> {
            this.controller.finishEventDrag();
        });*/
    }
}
