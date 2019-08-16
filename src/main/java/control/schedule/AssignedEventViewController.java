package control.schedule;

import app.AppSettings;
import control.MainController;
import javafx.scene.control.TitledPane;
import model.NewEvent;

public class AssignedEventViewController extends EventViewController {

    protected final MainController controller;
    protected final ScheduleIntervalController intervalController;

    public TitledPane hourPane;

    public AssignedEventViewController(MainController controller, ScheduleIntervalController intervalController, NewEvent event) {
        super(event);
        this.controller = controller;
        this.intervalController = intervalController;
    }

    public AssignedEventViewController(MainController controller, ScheduleIntervalController intervalController, EventViewController oldController) {
        this(controller, intervalController, oldController.getEvent());
    }

    public void setHour(Integer interval){
        hourPane.setText(computeHour(interval));
    }

    private String computeHour(Integer interval) {
        Integer dayRelativeInterval = interval % AppSettings.timeSlotsPerDay();
        Integer slotsPerHour = 60/AppSettings.TimeSlotDuration();
        Integer minutes = (dayRelativeInterval % slotsPerHour) * AppSettings.TimeSlotDuration();
        Integer hours = AppSettings.dayStart() + dayRelativeInterval / slotsPerHour;
        return String.format("%02d:%02d",hours,minutes);
    }

    @Override
    protected void initializeEventView(){
        super.initializeEventView();
        setEventColor();
    }

    @Override
    protected void setEventColor() {
        hourPane.setStyle("-fx-color: #" + event.getEventType().color().toString().substring(2) + ";");
        super.setEventColor();
    }

    @Override
    protected void initializeBehavior() {
        hourPane.setOnDragDetected(event -> {
            hourPane.startFullDrag();
            controller.startEventDrag(MainController.EventDrag.FROM_ASSIGNED,controller,this, intervalController);
            event.consume();
        });

        hourPane.setOnMouseDragReleased(event -> {
            System.out.println("DragDropped"); //TODO delete this
            MainController.EventDrag eventDrag = controller.getEventDrag();
            if(eventDrag.getEventViewController() != this){
                intervalController.addEventFromAssignedView(eventDrag, this);
            }
            eventDrag.finish(); //only the function that calls getEventDrag should call this
            event.consume();
        });
    }
}
