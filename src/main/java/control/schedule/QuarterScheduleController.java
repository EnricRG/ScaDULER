package control.schedule;

import app.AppSettings;
import control.MainController;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import misc.Warning;
import misc.Weeks;
import model.NewEvent;
import model.Quarter;
import scala.collection.JavaConverters;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class QuarterScheduleController extends DualWeekScheduleViewController<ScheduleController, ScheduleController>{

    private final Quarter quarter;
    private final MainController mainController;
    private final CourseScheduleController courseController;

    private Map<Long, Integer> eventsAtInterval = new HashMap<>(); //map of eventID and interval

    private Map<Integer, ScheduleIntervalController> firstWeekEventViews = new HashMap<>(); //map that holds the EventViews at each interval
    private Map<Integer, ScheduleIntervalController> secondWeekEventViews = new HashMap<>(); //map that holds the EventViews at each interval

    public QuarterScheduleController(MainController mainController, CourseScheduleController courseController, Quarter quarter) {
        super(new ScheduleController(), new ScheduleController());
        this.mainController = mainController;
        this.quarter = quarter;
        this.courseController = courseController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        initializeCustomListeners();
    }

    private void initializeCustomListeners() {
        for(int week = 0; week <= 1; week++){
            ScheduleController weekController = week == 0 ? firstWeekController : secondWeekController;
            ArrayList<Node> innerCells = new ArrayList<>(weekController.getInnerCells());
            for(int interval = 0; interval < innerCells.size(); interval++){
                Node cell = innerCells.get(interval);

                ScheduleIntervalController intervalController = new ScheduleIntervalController(this, (Region) cell, week, interval);

                Map<Integer, ScheduleIntervalController> weekIntervals = week == 0? firstWeekEventViews : secondWeekEventViews;
                weekIntervals.put(interval, intervalController);

                addIntervalViewToWeekController(intervalController, weekController, cell);

                setupCellBehavior(cell, week, ScheduleController.computeInterval(weekController.gridPane, cell), intervalController);
            }
        }
    }

    private void setupCellBehavior(Node cell, int week, Integer interval, ScheduleIntervalController intervalController) {
        cell.setOnMouseDragReleased(dragEvent -> {
            System.out.println("Dropped here: " + week + " " + interval); //TODO remove this line
            courseController.notifyEventDrop(this, intervalController, null, -1);
        });
    }

    public void processEventDrop(NewEvent event, int dragSource, int hint, ScheduleIntervalController previousController, Node cell, int scheduleWeek, Integer interval) {
        if(dragSource == MainController.EventDrag.FROM_ASSIGNED) newUnassignEvent(event);
        newAssignEvent(event, dragSource, cell, scheduleWeek, interval, hint, previousController);
    }

    //pre event not assigned to this quarter
    //TODO fix cell, actually the quarter does not know to wich
    public void newAssignEvent(NewEvent scheduleEvent, int dragSource, Node cell, int scheduleWeek, Integer interval, int hint, ScheduleIntervalController previousController) {
        if(scheduleEvent.getWeek() == Weeks.getEveryWeek()){
            newAssignEventI(scheduleEvent, firstWeekController, firstWeekEventViews, cell, scheduleWeek, interval, hint, previousController);
            newAssignEventI(scheduleEvent, secondWeekController, secondWeekEventViews, cell, scheduleWeek, interval, hint, previousController);
        }
        else if(scheduleEvent.getWeek() == Weeks.getAWeek()){
            newAssignEventI(scheduleEvent, firstWeekController, firstWeekEventViews, cell, scheduleWeek, interval, hint, previousController);
        }
        else if(scheduleEvent.getWeek() == Weeks.getBWeek()){
            newAssignEventI(scheduleEvent, secondWeekController, secondWeekEventViews, cell, scheduleWeek, interval, hint, previousController);
        }
        //else error, no week like this exists

        eventsAtInterval.put(scheduleEvent.getID(), interval);
        quarter.schedule().addEvent(scheduleWeek, interval, scheduleEvent); //scheduleWeek is a dummy parameter here
    }

    private void newAssignEventI(NewEvent event, ScheduleController weekController, Map<Integer, ScheduleIntervalController> weekIntervals,
                                 Node cell, int week, Integer interval, int hint, ScheduleIntervalController previousController){

        ScheduleIntervalController intervalController = weekIntervals.get(interval);

        if(hint >= 0) intervalController.newAddEvent(event, hint);
        else intervalController.newAddEvent(event, -1);
    }

    //pre: event assigned to this quarter
    public void newUnassignEvent(NewEvent scheduleEvent) {
        Weeks.Week week = scheduleEvent.getWeek();

        if(week == Weeks.getEveryWeek()){
            firstWeekEventViews.get(scheduleEvent.getStartInterval()).removeAssignment(scheduleEvent);
            secondWeekEventViews.get(scheduleEvent.getStartInterval()).removeAssignment(scheduleEvent);
        }
        else if(week == Weeks.getAWeek()) firstWeekEventViews.get(scheduleEvent.getStartInterval()).removeAssignment(scheduleEvent);
        else if(week == Weeks.getBWeek()) secondWeekEventViews.get(scheduleEvent.getStartInterval()).removeAssignment(scheduleEvent);
        //else error, no week like this exists

        eventsAtInterval.remove(scheduleEvent.getID());
        quarter.getSchedule().removeEvent(scheduleEvent.getWeek().toWeekNumber(), scheduleEvent.getStartInterval(), scheduleEvent); //TODO improvable call
    }
/*
    public void assignEvent(MainController.EventDrag eventDrag, Node cell, int week, Integer interval){
        if(eventDrag.getEventViewController().getEvent().getWeek() != Weeks.getEveryWeek()){
            processDropEvent(eventDrag, cell, week, interval);
            eventDrag.finish();
        }
        else{
            processDropEvent(eventDrag, cell, 0, interval);
            processDropEvent(eventDrag, cell, 1, interval);
            eventDrag.finish();
        }
    }

    public void processDropEvent(MainController.EventDrag eventDrag, Node cell, int week, Integer interval) {
        NewEvent scheduleEvent = eventDrag.getEventViewController().getEvent();

        if(isViableAssignment(quarter, scheduleEvent, week, interval)) {
            Map<Integer, ScheduleIntervalController> targetWeekViews = week == 0 ? firstWeekEventViews : secondWeekEventViews;
            ScheduleIntervalController intervalController = targetWeekViews.get(interval);

            if (intervalController == null){
                intervalController = new ScheduleIntervalController(this, (Region) cell, week, interval);
                targetWeekViews.put(interval, intervalController);
                addIntervalViewToWeekController(intervalController, week == 0 ? firstWeekController : secondWeekController, cell);
            }

            if(eventDrag.dragSource == MainController.EventDrag.FROM_UNASSIGNED) intervalController.addEvent(eventDrag);
            else intervalController.addEvent(eventDrag, (AssignedEventViewController) eventDrag.getEventViewController());

            if(!eventsAtInterval.containsKey(scheduleEvent.getID())){
                eventsAtInterval.put(scheduleEvent.getID(), interval);
                quarter.schedule().addEvent(week, interval, scheduleEvent);
            }
        }

        //eventDrag.finish(); //maybe this shouldn't be here
    }

    //pre: event assigned to this quarter
    public void unassignEvent(MainController.EventDrag eventDrag, int week){
        NewEvent event = eventDrag.getEventViewController().getEvent();
        if(event.getWeek() != Weeks.getEveryWeek()){
            unassignEventI(eventDrag.getIntervalController(), eventDrag.getEventViewController().getEvent());
        }
        else{
            if(week == Weeks.getAWeek().toWeekNumber() || week == -1) unassignEventI(firstWeekEventViews.get(event.getStartInterval()), event);
            if(week == Weeks.getBWeek().toWeekNumber() || week == -1) unassignEventI(secondWeekEventViews.get(event.getStartInterval()), event);
        }
        eventsAtInterval.remove(eventDrag.getEventViewController().getEvent().getID());
        quarter.getSchedule().removeEvent(event.getWeek().toWeekNumber(), event.getStartInterval(), event);
    }

    private void unassignEventI(ScheduleIntervalController intervalController, NewEvent event){
        intervalController.removeAssignment(event);
    }
*/
    private void addIntervalViewToWeekController(ScheduleIntervalController intervalController, ScheduleController weekController, Node cell) {
        Node visibleBox = intervalController.getBoundingBox();
        weekController.overPane.getChildren().add(visibleBox);
        visibleBox.setLayoutX(cell.getLayoutX());
        visibleBox.setLayoutY(cell.getLayoutY());
    }
/*
    private boolean isViableAssignment(Quarter quarter, NewEvent scheduleEvent, int week, Integer interval) {
        Warning warning = checkAssignmentViability(quarter,scheduleEvent, week, interval);
        if(warning == null){
            courseController.hideWarnings();
            return true;
        }
        else{
            courseController.popUpWarning(warning);
            return false;
        }
    }*/
    /*
    //pre: quarter, scheduleEvent and interval not null
    private Warning checkAssignmentViability(Quarter quarter, NewEvent scheduleEvent, int week, Integer interval) {
        Warning warning = null;

        warning = checkEventPrecedences(quarter, scheduleEvent, week, interval);

        if(warning != null) return warning;

        warning = checkEventIncompatibilities(quarter, scheduleEvent, week, interval);

        if(warning != null) return warning;

        warning = checkResourceAvailability(quarter, scheduleEvent, week, interval);

        return warning;

    }

    private Warning checkEventPrecedences(Quarter quarter, NewEvent scheduleEvent, int week, Integer interval) {
        return null; //TODO
    }

    private Warning checkEventIncompatibilities(Quarter quarter, NewEvent scheduleEvent, int week, Integer interval) {
        ArrayList<NewEvent> incompatibilities = new ArrayList<>(JavaConverters.asJavaCollection(quarter.getSchedule().getIncompatibleEvents(scheduleEvent,week,interval)));

        if(incompatibilities.isEmpty()) return null;
        else return new Warning(String.format(
                        AppSettings.language().getItem("warning_incompatibleEvents"),
                        scheduleEvent.getShortName(),
                        incompatibilities.get(0).getShortName()));
    }

    private Warning checkResourceAvailability(Quarter quarter, NewEvent scheduleEvent, int week, Integer interval) {
        return null; //TODO
    }*/

    public MainController getMainController() { return mainController; }

    public void startEventDrag(NewEvent event, int dragSource, AssignedEventViewController assignedEventViewController, ScheduleIntervalController intervalController) {
        mainController.startEventDrag(event, dragSource, assignedEventViewController, intervalController);
    }

    public void notifyEventDrop(ScheduleIntervalController intervalController, AssignedEventViewController assignedEventViewController, int hint) {
        courseController.notifyEventDrop(this, intervalController, assignedEventViewController, hint);
    }

    public Quarter getQuarter() { return quarter; }
}
