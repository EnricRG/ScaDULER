package control.schedule;

import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import misc.Warning;
import model.Course;
import model.NewEvent;
import model.Quarter;

import java.net.URL;
import java.util.ResourceBundle;

public class QuarterScheduleController extends DualWeekScheduleViewController<ScheduleController, ScheduleController>{

    private final Quarter quarter;
    private EventViewController dragSource;

    public QuarterScheduleController(Quarter quarter) {
        super(new ScheduleController(), new ScheduleController());
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
        cell.setOnDragDropped(dragEvent -> processDropEvent(dragEvent, cell, week, interval));
    }

    private void processDropEvent(DragEvent dragEvent, Node cell, int week, Integer interval) {
        NewEvent scheduleEvent = dragSource.getEvent();

        if(isViableAssignment(quarter, scheduleEvent, week, interval)) {
            quarter.schedule().addEvent(week,interval,scheduleEvent);
        }
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

    public void setEventDragSource(EventViewController controller) {
        dragSource = controller;
    }

    public void finishEventDrag() {
        dragSource = null;
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
