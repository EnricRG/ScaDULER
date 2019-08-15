package control.schedule;

import control.MainController;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.Region;
import misc.Warning;
import model.NewEvent;
import model.Quarter;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class QuarterScheduleController extends DualWeekScheduleViewController<ScheduleController, ScheduleController>{

    private final Quarter quarter;
    private final MainController mainController;

    private Map<Integer, ScheduleIntervalController> firstWeekEventViews = new HashMap<>(); //map that holds the EventViews at each interval
    private Map<Integer, ScheduleIntervalController> secondWeekEventViews = new HashMap<>(); //map that holds the EventViews at each interval

    public QuarterScheduleController(MainController mainController, Quarter quarter) {
        super(new ScheduleController(), new ScheduleController());
        this.mainController = mainController;
        this.quarter = quarter;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        initializeCustomListeners();
    }

    private void initializeCustomListeners() {
        for(int week = 0; week <= 1; week++){
            ScheduleController c = week == 0 ? firstWeekController : secondWeekController;
            for(Node cell : c.getInnerCells()){
                setupCellBehavior(cell, week, ScheduleController.computeInterval(c.gridPane, cell));
            }
        }
    }

    private void setupCellBehavior(Node cell, int week, Integer interval) {
        cell.setOnMouseDragReleased(dragEvent -> {
            System.out.println("Dropped here: " + week + " " + interval);
            processDropEvent(dragEvent, cell, week, interval);
        });
    }

    private void processDropEvent(MouseDragEvent dragEvent, Node cell, int week, Integer interval) {
        NewEvent scheduleEvent = mainController.getEventDragSource().getEvent();

        if(isViableAssignment(quarter, scheduleEvent, week, interval)) {
            Map<Integer, ScheduleIntervalController> targetWeekViews = week == 0 ? firstWeekEventViews : secondWeekEventViews;
            ScheduleIntervalController intervalController = targetWeekViews.get(interval);

            if (intervalController == null){
                intervalController = new ScheduleIntervalController(this, (Region) cell);
                targetWeekViews.put(interval, intervalController);
            }

            intervalController.addEvent(mainController,mainController.getEventDragSource(),interval);

            addEventToIntervalView(intervalController, week == 0 ? firstWeekController : secondWeekController, cell);

            quarter.schedule().addEvent(week,interval,scheduleEvent);
        }
    }

    private void addEventToIntervalView(ScheduleIntervalController intervalController, ScheduleController weekController, Node cell) {
        Node visibleBox = intervalController.getBoundingBox();
        weekController.overPane.getChildren().add(visibleBox);
        visibleBox.setLayoutX(cell.getLayoutX());
        visibleBox.setLayoutY(cell.getLayoutY());
    }

    private boolean isViableAssignment(Quarter quarter, NewEvent scheduleEvent, int week, Integer interval) {
        Warning warning = checkAssignmentViability(quarter,scheduleEvent, week, interval);
        if(warning == null){
            //hideWarnings();
            return true;
        }
        else{
            //popUpWarning(warning);
            return false;
        }
    }

    private Warning checkAssignmentViability(Quarter quarter, NewEvent scheduleEvent, int week, Integer interval) {
        if(scheduleEvent == null){
            //TODO: finish this warning
            return new Warning("No drag source");
        }
        else return null;
    }

/*
    private void initializeWarningSystem() {
        hideWarnings();
        warningTag.setText("");
    }

    private void hideWarnings(){ warningTag.setVisible(false); }
    private void showWarnings(){ warningTag.setVisible(true); }

    private void popUpWarning(Warning warning) {
        warningTag.setText(warning.toString());
        showWarnings();
    }
    */
}
