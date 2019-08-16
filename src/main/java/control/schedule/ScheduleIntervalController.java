package control.schedule;

import app.AppSettings;
import app.FXMLPaths;
import control.MainController;
import factory.ViewFactory;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.io.IOException;

public class ScheduleIntervalController {

    private final QuarterScheduleController quarterScheduleController;
    private final Region boundingRegion;

    private final HBox boundingBox = new HBox();
    private final Integer interval;

    public ScheduleIntervalController(QuarterScheduleController quarterScheduleController, Region regionBelow, Integer interval){
        this.quarterScheduleController = quarterScheduleController;
        this.boundingRegion = regionBelow;
        this.interval = interval;
        initializeBoundingBox();
    }

    private void initializeBoundingBox() {
        boundingBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        boundingBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        boundingBox.setMaxWidth(boundingRegion.getWidth()*AppSettings.eventViewColumnPercentage());
        boundingBox.setMaxHeight(boundingRegion.getHeight()* AppSettings.maxEventDuration());

        boundingRegion.widthProperty().addListener(((observable, oldValue, newValue) -> {
            boundingBox.setMaxWidth(newValue.doubleValue()*AppSettings.eventViewColumnPercentage());
        }));

        boundingRegion.heightProperty().addListener((observable, oldValue, newValue) -> {
            boundingBox.setMaxHeight(newValue.doubleValue()*AppSettings.maxEventDuration());
        });
    }

    public void addEvent(MainController.EventDrag eventDrag){
        addEvent(eventDrag, null);
    }

    public void addEvent(MainController.EventDrag eventDrag, AssignedEventViewController assignedEventViewController) {

        if(eventDrag.eventSource == MainController.EventDrag.FROM_UNASSIGNED){ //comes from an unassigned event
            addNewAssignment(quarterScheduleController.getMainController(), eventDrag.getEventViewController(), assignedEventViewController);
            quarterScheduleController.getMainController().assignmentDone(eventDrag);
        }
        else if(eventDrag.eventSource == MainController.EventDrag.FROM_ASSIGNED){
            if(boundingBox.getChildren().contains(assignedEventViewController.hourPane)){ //same interval
                addNewAssignment(quarterScheduleController.getMainController(), eventDrag.getEventViewController(), assignedEventViewController);
                removeAssignment(assignedEventViewController);
            }
            else{ //another interval
                assignedEventViewController.getIntervalController().removeAssignment(assignedEventViewController);
                addNewAssignment(quarterScheduleController.getMainController(), eventDrag.getEventViewController(), null);
            }
        }
        //else error, no event drag with this code should exist.
    }

    public void removeAssignment(Node node) {
        boundingBox.getChildren().remove(node);
    }

    public void removeAssignment(AssignedEventViewController assignedEventViewController) {
        removeAssignment(assignedEventViewController.hourPane);
    }

    private void addNewAssignment(MainController mainController, EventViewController eventView, AssignedEventViewController hint){
        AssignedEventViewController assignedView = new AssignedEventViewController(mainController,this,eventView);

        try{new ViewFactory<>(FXMLPaths.AssignedEvent()).load(assignedView);} catch (IOException ioe){ ioe.printStackTrace(); }

        assignedView.setHour(interval);

        int hintIndex = hint != null ? boundingBox.getChildren().indexOf(hint.hourPane) : -1;

        if(hintIndex < 0) boundingBox.getChildren().add(assignedView.hourPane);
        else boundingBox.getChildren().add(hintIndex, assignedView.hourPane);
    }

    public HBox getBoundingBox() {
        return boundingBox;
    }

    public Integer getInterval() { return interval; }


}
