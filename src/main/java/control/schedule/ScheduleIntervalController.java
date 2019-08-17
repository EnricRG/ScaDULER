package control.schedule;

import app.AppSettings;
import app.FXMLPaths;
import control.MainController;
import factory.ViewFactory;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import model.NewEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScheduleIntervalController {

    private final QuarterScheduleController quarterScheduleController;
    private final Region boundingRegion;

    private final Map<Long, AssignedEventViewController> eventViewControllers = new HashMap<>();

    private final HBox boundingBox = new HBox();
    private final Integer interval;

    private final Integer week;

    public ScheduleIntervalController(QuarterScheduleController quarterScheduleController, Region regionBelow, Integer week, Integer interval){
        this.quarterScheduleController = quarterScheduleController;
        this.boundingRegion = regionBelow;
        this.interval = interval;
        this.week = week;
        initializeBoundingBox();
    }

    private void initializeBoundingBox() {
        boundingBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        boundingBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        boundingBox.setMaxWidth(boundingRegion.getWidth()*AppSettings.eventViewColumnPercentage());
        boundingBox.setMaxHeight(boundingRegion.getHeight()* AppSettings.maxEventDuration());

        boundingRegion.widthProperty().addListener((observable, oldValue, newValue) -> {
            boundingBox.setMaxWidth(newValue.doubleValue()*AppSettings.eventViewColumnPercentage());
            rePosition();
        });

        boundingRegion.heightProperty().addListener((observable, oldValue, newValue) -> {
            boundingBox.setMaxHeight(newValue.doubleValue()*AppSettings.maxEventDuration());
            resizeNodes(oldValue.doubleValue(), newValue.doubleValue());
            rePosition();
        });
    }

    private void rePosition() {
        boundingBox.setLayoutX(boundingRegion.getLayoutX());
        boundingBox.setLayoutY(boundingRegion.getLayoutY());
    }

    private void resizeNodes(double oldValue, double newValue) {
        for(Node n : boundingBox.getChildren()){
            Region r = (Region) n;
            r.setMinHeight((r.getHeight()/oldValue)*newValue);
            r.setPrefHeight((r.getHeight()/oldValue)*newValue);
            r.setMaxHeight((r.getHeight()/oldValue)*newValue);
        }
    }

    public void startEventDrag(NewEvent event, AssignedEventViewController assignedEventViewController) {
        quarterScheduleController.startEventDrag(event, MainController.EventDrag.FROM_ASSIGNED, assignedEventViewController, this);
    }

    public void newAddEvent(NewEvent event, int hint) {
        AssignedEventViewController assignedView = new AssignedEventViewController(this,event);

        try{new ViewFactory<>(FXMLPaths.AssignedEvent()).load(assignedView);} catch (IOException ioe){ ioe.printStackTrace(); }

        assignedView.setHour(interval);

        if(hint < 0) boundingBox.getChildren().add(assignedView.hourPane);
        else boundingBox.getChildren().add(hint, assignedView.hourPane);

        eventViewControllers.put(event.getID(), assignedView);
    }

    /*
    public void addEvent(MainController.EventDrag eventDrag){
        addEvent(eventDrag.getEventViewController().getEvent(), eventDrag.dragSource, null);
    }

    public void addEvent(NewEvent event, int dragSource, AssignedEventViewController assignedEventViewController) {

        if(dragSource == MainController.EventDrag.FROM_UNASSIGNED){ //comes from an unassigned event
            addNewAssignment(quarterScheduleController.getMainController(), event, assignedEventViewController);
            //quarterScheduleController.getMainController().assignmentDone(eventDrag);
        }
        else if(dragSource == MainController.EventDrag.FROM_ASSIGNED){
            if(eventViewControllers.get(event.getID()) == assignedEventViewController){ //same interval
                addNewAssignment(quarterScheduleController.getMainController(), event, assignedEventViewController);
                removeOldController(assignedEventViewController);
            }
            else{ //another interval
                //assignedEventViewController.getIntervalController().getQuarterScheduleController().unassignEvent(eventDrag, week);
                //FIXME uncomment this and update behavior
                addNewAssignment(quarterScheduleController.getMainController(), event, null);
            }
        }
        //else error, no event drag with this code should exist.
    }
    */
    private void removeOldController(Node node) {
        boundingBox.getChildren().remove(node);
    }

    private void removeOldController(AssignedEventViewController assignedEventViewController) {
        removeOldController(assignedEventViewController.hourPane);
    }

    public void removeAssignment(NewEvent event){
        AssignedEventViewController eventViewController = eventViewControllers.get(event.getID());
        if(eventViewController != null){
            removeOldController(eventViewController);
            eventViewControllers.remove(event.getID());
        }
    }

    public void notifyEventDrop(AssignedEventViewController assignedEventViewController) {
        quarterScheduleController.notifyEventDrop(this, assignedEventViewController, boundingBox.getChildren().indexOf(assignedEventViewController.getNode()));
    }

    /*
    private void addNewAssignment(MainController mainController, NewEvent event, AssignedEventViewController hint){
        AssignedEventViewController assignedView = new AssignedEventViewController(mainController,this,event);

        try{new ViewFactory<>(FXMLPaths.AssignedEvent()).load(assignedView);} catch (IOException ioe){ ioe.printStackTrace(); }

        assignedView.setHour(interval);

        int hintIndex = hint != null ? boundingBox.getChildren().indexOf(hint.hourPane) : -1;

        if(hintIndex < 0) boundingBox.getChildren().add(assignedView.hourPane);
        else boundingBox.getChildren().add(hintIndex, assignedView.hourPane);

        eventViewControllers.put(event.getID(), assignedView);
    }*/

    public HBox getBoundingBox() {
        return boundingBox;
    }

    public Region getBoundingRegion() {
        return boundingRegion;
    }

    public Integer getInterval() { return interval; }

    public Integer getWeek() {
        return week;
    }

    public QuarterScheduleController getQuarterScheduleController() { return quarterScheduleController; }



}
