package control.schedule;

import app.AssignmentViabilityChecker;
import control.MainController;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import model.Event;
import model.QuarterData;
import model.Weeks;
import scala.collection.JavaConverters;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class QuarterScheduleController extends DualWeekScheduleViewController<WeekScheduleController, WeekScheduleController>{

    private final QuarterData quarterData;
    private final MainController mainController;

    private final CourseScheduleController courseController;

    private Map<Long, Integer> eventsAtInterval = new HashMap<>(); //map of eventID and interval

    private Map<Integer, ScheduleIntervalController> firstWeekEventViews = new HashMap<>(); //map that holds the EventViews at each interval
    private Map<Integer, ScheduleIntervalController> secondWeekEventViews = new HashMap<>(); //map that holds the EventViews at each interval

    public QuarterScheduleController(MainController mainController, CourseScheduleController courseController, QuarterData quarterData) {
        super(new WeekScheduleController(), new WeekScheduleController());
        this.mainController = mainController;
        this.quarterData = quarterData;
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
            WeekScheduleController weekController = week == 0 ? firstWeekController : secondWeekController;
            ArrayList<Node> innerCells = new ArrayList<>(weekController.getInnerCells());
            for(int interval = 0; interval < innerCells.size(); interval++){
                Node cell = innerCells.get(interval);

                ScheduleIntervalController intervalController = new ScheduleIntervalController(this, weekController, (Region) cell, week, interval);

                Map<Integer, ScheduleIntervalController> weekIntervals = week == 0? firstWeekEventViews : secondWeekEventViews;
                weekIntervals.put(interval, intervalController);

                addIntervalViewToWeekController(intervalController, weekController, cell);

                setupCellBehavior(cell, week, WeekScheduleController.computeInterval(weekController.gridPane, cell), intervalController);
            }
        }
    }

    private void setupCellBehavior(Node cell, int week, Integer interval, ScheduleIntervalController intervalController) {
        cell.setOnMouseDragReleased(dragEvent -> {
            if(dragEvent.getButton() == MouseButton.PRIMARY) {
                System.out.println("Dropped here: " + week + " " + interval + " " + mainController.getEventDrag().getEvent().getID()); //TODO remove this print
                courseController.notifyEventDrop(this, intervalController, -1);
            }
        });
    }

    public void fillQuarter() {
        for(Event e: JavaConverters.asJavaCollection(quarterData.getSchedule().getEvents())){
            //TODO optimize this, save viability state on persistence.
            //FIXME
            AssignmentViabilityChecker checker = new AssignmentViabilityChecker(courseController.getCourse(), quarterData, e.week().get().toWeekNumber(), e.getStartInterval(), e);
            processEventDrop(e, MainController.EventDrag.FROM_UNASSIGNED, -1, e.week().get().toWeekNumber(), e.getStartInterval(), checker.isAViableAssignment());
        }
        //FIXME this will need a fix
    }

    public void processEventDrop(Event event, int dragSource, int hint, int scheduleWeek, Integer interval, boolean viable) {
        if(dragSource == MainController.EventDrag.FROM_ASSIGNED) unassignEvent(event);
        assignEvent(event, scheduleWeek, interval, hint, viable);
    }

    //pre event not assigned to this quarter
    //TODO improve call performance vs readability: Call only getWeek() once
    //TODO improve call redundancy. No need to call addEvent when reloading the course.
    public void assignEvent(Event scheduleEvent, int scheduleWeek, Integer interval, int hint, boolean viable) {
        Weeks.Week eventWeek = scheduleEvent.periodicity() == Weeks.weekly() ?
            Weeks.getEveryWeek() : (scheduleWeek == Weeks.getAWeek().toWeekNumber() ? Weeks.getAWeek() : Weeks.getBWeek());

        if(eventWeek == Weeks.getEveryWeek()){//scheduleEvent.getWeek() == Weeks.getEveryWeek()){
            assignEventI(scheduleEvent, firstWeekEventViews, interval, hint, viable);
            assignEventI(scheduleEvent, secondWeekEventViews, interval, hint, viable);
        }
        else if(eventWeek == Weeks.getAWeek()){
            assignEventI(scheduleEvent, firstWeekEventViews, interval, hint, viable);
        }
        else if(eventWeek == Weeks.getBWeek()){
            assignEventI(scheduleEvent, secondWeekEventViews, interval, hint, viable);
        }
        //else error, no week like this exists

        eventsAtInterval.put(scheduleEvent.getID(), interval);
        quarterData.getSchedule().addEvent(eventWeek, interval, scheduleEvent); //scheduleWeek is a dummy parameter here
    }

    private void assignEventI(Event event, Map<Integer, ScheduleIntervalController> weekIntervals, Integer interval, int hint, boolean viable){

        ScheduleIntervalController intervalController = weekIntervals.get(interval);

        if(hint >= 0) intervalController.addEvent(event, hint, viable);
        else intervalController.addEvent(event, -1, viable);
    }

    //pre: event assigned to this quarter
    public void unassignEvent(Event scheduleEvent) {
        Weeks.Week week = scheduleEvent.week().get(); //event will have an assigned week

        if(week == Weeks.getEveryWeek()){
            firstWeekEventViews.get(scheduleEvent.getStartInterval()).removeAssignment(scheduleEvent);
            secondWeekEventViews.get(scheduleEvent.getStartInterval()).removeAssignment(scheduleEvent);
        }
        else if(week == Weeks.getAWeek()) firstWeekEventViews.get(scheduleEvent.getStartInterval()).removeAssignment(scheduleEvent);
        else if(week == Weeks.getBWeek()) secondWeekEventViews.get(scheduleEvent.getStartInterval()).removeAssignment(scheduleEvent);
        //else error, no week like this exists

        eventsAtInterval.remove(scheduleEvent.getID());
        //TODO improvable call, there's no need to pass the first parameter.
        quarterData.getSchedule().removeEvent(scheduleEvent.getStartInterval(), scheduleEvent);
    }

    private void addIntervalViewToWeekController(ScheduleIntervalController intervalController, WeekScheduleController weekController, Node cell) {
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

    public QuarterData getQuarterData() { return quarterData; }

    public CourseScheduleController getCourseController() {
        return courseController;
    }

    //if week is EveryWeek.toWeekNumber, it will still work as intended because it will assign to both weeks.
    //I don't know if I like this behavior, but it works and it's intended.
    public ScheduleIntervalController getIntervalControllerAt(int week, int interval) {
        Map<Integer, ScheduleIntervalController> weekViews =
                week == Weeks.getAWeek().toWeekNumber() ? firstWeekEventViews : secondWeekEventViews;

        return weekViews.get(interval);
    }
}
