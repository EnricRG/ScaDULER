package control.schedule;

import control.MainController;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import model.Event;
import model.Quarter;
import model.Weeks;
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
        System.out.println("Quarter init");
    }

    private void initializeCustomListeners() {
        for(int week = 0; week <= 1; week++){
            ScheduleController weekController = week == 0 ? firstWeekController : secondWeekController;
            ArrayList<Node> innerCells = new ArrayList<>(weekController.getInnerCells());
            for(int interval = 0; interval < innerCells.size(); interval++){
                Node cell = innerCells.get(interval);

                ScheduleIntervalController intervalController = new ScheduleIntervalController(this, weekController, (Region) cell, week, interval);

                Map<Integer, ScheduleIntervalController> weekIntervals = week == 0? firstWeekEventViews : secondWeekEventViews;
                weekIntervals.put(interval, intervalController);

                addIntervalViewToWeekController(intervalController, weekController, cell);

                setupCellBehavior(cell, week, ScheduleController.computeInterval(weekController.gridPane, cell), intervalController);
            }
        }
    }

    private void setupCellBehavior(Node cell, int week, Integer interval, ScheduleIntervalController intervalController) {
        cell.setOnMouseDragReleased(dragEvent -> {
            System.out.println("Dropped here: " + week + " " + interval + " " + mainController.getEventDrag().getEvent().getID()); //TODO remove this line
            courseController.notifyEventDrop(this, intervalController, -1);
        });
    }

    public void fillQuarter() {
        for(Event e: JavaConverters.asJavaCollection(quarter.getSchedule().getEvents())){
            //unassignEvent(e);
            processEventDrop(e, MainController.EventDrag.FROM_UNASSIGNED, -1, e.getWeek().toWeekNumber(), e.getStartInterval());
        }
        //FIXME this will need a fix
    }

    public void processEventDrop(Event event, int dragSource, int hint, int scheduleWeek, Integer interval) {
        if(dragSource == MainController.EventDrag.FROM_ASSIGNED) unassignEvent(event);
        assignEvent(event, scheduleWeek, interval, hint);
    }

    //pre event not assigned to this quarter
    //TODO fix cell, actually the quarter does not know to which
    public void assignEvent(Event scheduleEvent, int scheduleWeek, Integer interval, int hint) {
        if(scheduleEvent.getWeek() == Weeks.getEveryWeek()){
            assignEventI(scheduleEvent, firstWeekEventViews, interval, hint);
            assignEventI(scheduleEvent, secondWeekEventViews, interval, hint);
        }
        else if(scheduleEvent.getWeek() == Weeks.getAWeek()){
            assignEventI(scheduleEvent, firstWeekEventViews, interval, hint);
        }
        else if(scheduleEvent.getWeek() == Weeks.getBWeek()){
            assignEventI(scheduleEvent, secondWeekEventViews, interval, hint);
        }
        //else error, no week like this exists

        eventsAtInterval.put(scheduleEvent.getID(), interval);
        quarter.getSchedule().addEvent(scheduleWeek, interval, scheduleEvent); //scheduleWeek is a dummy parameter here
    }

    private void assignEventI(Event event, Map<Integer, ScheduleIntervalController> weekIntervals, Integer interval, int hint){

        ScheduleIntervalController intervalController = weekIntervals.get(interval);

        if(hint >= 0) intervalController.addEvent(event, hint);
        else intervalController.addEvent(event, -1);
    }

    //pre: event assigned to this quarter
    public void unassignEvent(Event scheduleEvent) {
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

    private void addIntervalViewToWeekController(ScheduleIntervalController intervalController, ScheduleController weekController, Node cell) {
        Node visibleBox = intervalController.getBoundingBox();
        weekController.overPane.getChildren().add(visibleBox);
        visibleBox.setLayoutX(cell.getLayoutX());
        visibleBox.setLayoutY(cell.getLayoutY());
    }

    public MainController getMainController() { return mainController; }

    public void startEventDrag(Event event, int dragSource, AssignedEventViewController assignedEventViewController, ScheduleIntervalController intervalController) {
        mainController.startEventDrag(event, dragSource, assignedEventViewController, intervalController);
    }

    public void notifyEventDrop(ScheduleIntervalController intervalController, AssignedEventViewController assignedEventViewController, int hint) {
        courseController.notifyEventDrop(this, intervalController, hint);
    }

    public Quarter getQuarter() { return quarter; }

    public CourseScheduleController getCourseController() {
        return courseController;
    }

    public ScheduleIntervalController getVisibleIntervalControllerAt(int week, int interval) {
        Map<Integer, ScheduleIntervalController> weekViews =
                week == Weeks.getAWeek().toWeekNumber() ? firstWeekEventViews : secondWeekEventViews;

        return weekViews.get(interval);
    }
}
