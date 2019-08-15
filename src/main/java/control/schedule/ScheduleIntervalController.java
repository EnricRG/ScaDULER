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

    public ScheduleIntervalController(QuarterScheduleController quarterScheduleController, Region regionBelow){
        this.quarterScheduleController = quarterScheduleController;
        this.boundingRegion = regionBelow;
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

    public void addEvent(MainController mainController, EventViewController eventView, Integer interval){
        AssignedEventViewController assignedView = new AssignedEventViewController(mainController,eventView);

        try{new ViewFactory<>(FXMLPaths.AssignedEvent()).load(assignedView);} catch (IOException ioe){ ioe.printStackTrace(); }

        assignedView.setHour(interval);
        boundingBox.getChildren().add(assignedView.hourPane);
    }

    public HBox getBoundingBox() {
        return boundingBox;
    }

}
