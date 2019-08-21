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
    private final ScheduleController weekController;
    private final Region boundingRegion;

    private final Map<Long, AssignedEventViewController> eventViewControllers = new HashMap<>();

    private final HBox boundingBox = new HBox();
    private final Integer interval;

    private final Integer week;

    public ScheduleIntervalController(QuarterScheduleController quarterScheduleController, ScheduleController weekController, Region regionBelow, Integer week, Integer interval){
        this.quarterScheduleController = quarterScheduleController;
        this.weekController = weekController;
        this.boundingRegion = regionBelow;
        this.interval = interval;
        this.week = week;
        initializeBoundingBox();
    }

    private void initializeBoundingBox() {
        boundingBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        boundingBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        boundingBox.setMaxWidth(boundingRegion.getWidth() * AppSettings.eventViewColumnPercentage());
        boundingBox.setMaxHeight((boundingRegion.getHeight()-1) * AppSettings.maxEventDuration());

        //FIXME bounding box size problem

        //boundingBox.setStyle("-fx-border-width: 2; -fx-border-color: red");

        boundingRegion.layoutXProperty().addListener((observableValue, number, t1) -> {
            boundingBox.setLayoutX(boundingRegion.getLayoutX());
        });

        boundingRegion.layoutYProperty().addListener((observableValue, number, t1) -> {
            boundingBox.setLayoutY(boundingRegion.getLayoutY());
        });

        boundingRegion.widthProperty().addListener((observable, oldValue, newValue) -> {
            boundingBox.setMaxWidth(newValue.doubleValue()*AppSettings.eventViewColumnPercentage());
        });

        boundingRegion.heightProperty().addListener((observable, oldValue, newValue) -> {
            boundingBox.setMaxHeight(newValue.doubleValue()*AppSettings.maxEventDuration());
            resizeNodes(oldValue.doubleValue(), newValue.doubleValue());
        });

        //FIXME this doesn't work when maximizing
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

    public void addEvent(NewEvent event, int hint) {
        AssignedEventViewController assignedView = new AssignedEventViewController(this,event);

        try{new ViewFactory<>(FXMLPaths.AssignedEvent()).load(assignedView);} catch (IOException ioe){ ioe.printStackTrace(); }

        assignedView.setHour(interval);

        if(hint < 0) boundingBox.getChildren().add(assignedView.hourPane);
        else boundingBox.getChildren().add(hint, assignedView.hourPane);

        eventViewControllers.put(event.getID(), assignedView);
    }

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
