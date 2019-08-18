package control.schedule;

import control.MainController;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import misc.Weeks;
import model.NewEvent;
import model.Quarter;

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
            System.out.println("Dropped here: " + week + " " + interval); //TODO remove this line
            courseController.notifyEventDrop(this, intervalController, null, -1);
        });
    }

    public void processEventDrop(NewEvent event, int dragSource, int hint, ScheduleIntervalController previousController, Node cell, int scheduleWeek, Integer interval) {
        if(dragSource == MainController.EventDrag.FROM_ASSIGNED) unassignEvent(event);
        assignEvent(event, dragSource, cell, scheduleWeek, interval, hint, previousController);
    }

    //pre event not assigned to this quarter
    //TODO fix cell, actually the quarter does not know to wich
    public void assignEvent(NewEvent scheduleEvent, int dragSource, Node cell, int scheduleWeek, Integer interval, int hint, ScheduleIntervalController previousController) {
        if(scheduleEvent.getWeek() == Weeks.getEveryWeek()){
            assignEventI(scheduleEvent, firstWeekController, firstWeekEventViews, cell, scheduleWeek, interval, hint, previousController);
            assignEventI(scheduleEvent, secondWeekController, secondWeekEventViews, cell, scheduleWeek, interval, hint, previousController);
        }
        else if(scheduleEvent.getWeek() == Weeks.getAWeek()){
            assignEventI(scheduleEvent, firstWeekController, firstWeekEventViews, cell, scheduleWeek, interval, hint, previousController);
        }
        else if(scheduleEvent.getWeek() == Weeks.getBWeek()){
            assignEventI(scheduleEvent, secondWeekController, secondWeekEventViews, cell, scheduleWeek, interval, hint, previousController);
        }
        //else error, no week like this exists

        eventsAtInterval.put(scheduleEvent.getID(), interval);
        quarter.schedule().addEvent(scheduleWeek, interval, scheduleEvent); //scheduleWeek is a dummy parameter here
    }

    private void assignEventI(NewEvent event, ScheduleController weekController, Map<Integer, ScheduleIntervalController> weekIntervals,
                              Node cell, int week, Integer interval, int hint, ScheduleIntervalController previousController){

        ScheduleIntervalController intervalController = weekIntervals.get(interval);

        if(hint >= 0) intervalController.addEvent(event, hint);
        else intervalController.addEvent(event, -1);
    }

    //pre: event assigned to this quarter
    public void unassignEvent(NewEvent scheduleEvent) {
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

    public void startEventDrag(NewEvent event, int dragSource, AssignedEventViewController assignedEventViewController, ScheduleIntervalController intervalController) {
        mainController.startEventDrag(event, dragSource, assignedEventViewController, intervalController);
    }

    public void notifyEventDrop(ScheduleIntervalController intervalController, AssignedEventViewController assignedEventViewController, int hint) {
        courseController.notifyEventDrop(this, intervalController, assignedEventViewController, hint);
    }

    public Quarter getQuarter() { return quarter; }
}
