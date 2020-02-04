package control.schedule;

import app.AppSettings;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import model.Event;

public class AssignedEventViewController extends EventViewController {

    private final ScheduleIntervalController intervalController;
    private final Integer interval;

    public TitledPane hourPane;

    public AssignedEventViewController(ScheduleIntervalController intervalController, Event event, Integer interval) {
        super(event);
        this.intervalController = intervalController;
        this.interval = interval;
    }

    @Override
    protected void initializeEventView(){
        setHour(interval);
        super.initializeEventView();
        initializeBoxSize();
        bindBoxSize();
        setEventColor();
    }

    private void setHour(Integer interval){
        hourPane.setText(computeHour(interval));
    }

    private String computeHour(Integer interval) {
        Integer dayRelativeInterval = interval % AppSettings.timeSlotsPerDay();
        Integer slotsPerHour = 60/AppSettings.TimeSlotDuration();
        Integer minutes = (dayRelativeInterval % slotsPerHour) * AppSettings.TimeSlotDuration();
        Integer hours = AppSettings.dayStart() + dayRelativeInterval / slotsPerHour;
        return String.format("%02d:%02d",hours,minutes);
    }

    private void initializeBoxSize() {
        hourPane.setMaxWidth(Double.MAX_VALUE);
        resizeBox();
        hourPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
    }

    private void resizeBox(){
        double height = getEvent().getDuration()*intervalController.getBoundingRegion().getHeight();
        hourPane.setMaxHeight(height);
        hourPane.setMinHeight(height);
    }

    private void bindBoxSize() {
        intervalController.getBoundingRegion().heightProperty().addListener((observable, oldValue, newValue) -> {
            resizeBox();
        });
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

    @Override
    public Node getNode() {
        return hourPane;
    }
}
