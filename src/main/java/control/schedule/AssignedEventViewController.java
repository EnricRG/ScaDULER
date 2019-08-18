package control.schedule;

import app.AppSettings;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import model.NewEvent;

public class AssignedEventViewController extends EventViewController {

    private final ScheduleIntervalController intervalController;

    public TitledPane hourPane;

    public AssignedEventViewController(ScheduleIntervalController intervalController, NewEvent event) {
        super(event);
        this.intervalController = intervalController;
    }

    public AssignedEventViewController(ScheduleIntervalController intervalController, EventViewController oldController) {
        this(intervalController, oldController.getEvent());
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
        initializeBoxSize();
        setEventColor();
    }

    private void initializeBoxSize() {
        hourPane.setMaxWidth(Double.MAX_VALUE);
        double maxHeight = getEvent().getDuration()*intervalController.getBoundingRegion().getHeight();
        hourPane.setMaxHeight(maxHeight);
        hourPane.setPrefHeight(maxHeight);
        hourPane.setMinHeight(maxHeight);
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
            intervalController.startEventDrag(getEvent(), this);
            event.consume();
        });

        hourPane.setOnMouseDragReleased(event -> {
            intervalController.notifyEventDrop(this);
            event.consume();
        });
    }

    public ScheduleIntervalController getIntervalController() { return intervalController; }

    @Override
    public Node getNode() {
        return hourPane;
    }
}
