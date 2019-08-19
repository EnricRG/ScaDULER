package control;

import app.AppSettings;
import app.FXMLPaths;
import app.MainApp;
import app.AssignmentViabilityChecker;
import control.form.EventFormController;
import control.manage.CourseManagerController;
import control.manage.CourseResourceManagerController;
import control.manage.EventManagerController;
import control.manage.SubjectManagerController;
import control.schedule.*;
import factory.*;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import misc.Warning;
import model.Course;
import model.NewEvent;
import model.NewEventSchedule;
import model.Quarter;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.mutable.ListBuffer;
import service.AppDatabase;
import service.CourseDatabase;
import util.Utils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    /** Main application interface element */
    public StackPane mainStackPane;

    public BorderPane mainBorderPane;

    /** Menu bar container */
    public MenuBar menuBar;

    /** File menu */
    public Menu menuBar_fileMenu;

    /** Open item in File menu */
    public MenuItem fileMenu_open;
    /** Save item in File menu */
    public MenuItem fileMenu_save;
    /** Save item in File menu */
    public MenuItem fileMenu_saveAs;
    /** Save item in File menu */
    public MenuItem fileMenu_close;

    /** Edit menu */
    public Menu menuBar_editMenu;

    /** Settings menu */
    public Menu menuBar_settingsMenu;
    /** Application settings item in Settings menu */
    public MenuItem settingsMenu_appSettings;

    /** Help menu */
    public Menu menuBar_helpMenu;
    /** About item in Settings menu */
    public MenuItem helpMenu_about;

    public VBox addButtons;
    public Label addButtons_title;
    public Button addButtons_course;
    public Button addButtons_subject;
    public Button addButtons_event;

    public VBox manageButtons;
    public Label manageButtons_title;
    public Button manageButtons_courses;
    public Button manageButtons_resources;
    public Button manageButtons_subjects;
    public Button manageButtons_events;
    public Button manageButtons_unfinishedEvents;

    public VBox viewButtons;
    public Label viewButtons_title;
    public ToggleButton viewButtons_eventList;
    public ToggleButton viewButtons_unfinishedEventsList;

    public VBox runButtons;
    public Label runButtons_title;
    public Button runButtons_solve;
    public Button runButtons_optimize;
    public Label runButtons_timeoutLabel;
    public TextField runButtons_timeoutField;
    public Button runButtons_stop;

    public TabPane courseTabs;
    public Tab courseTabs_addTab;

    public ScrollPane leftPane;

    public VBox rightPane;
    public TextField rightPane_eventSearch;
    public ScrollPane rightPane_scrollPane;
    public VBox rightPane_VBox;

    private Map<Long, QuarterScheduleController> eventQuartersMap = new HashMap<>();
    private Map<Long, UnassignedEventViewController> unassignedEventsMap = new HashMap<>();
    private Map<Long,Tab> courseTabMap = new HashMap<>();
    private Map<Long,Node> eventViewMap = new HashMap<>();

    public void processEventAssignment(CourseScheduleController courseScheduleController,
                                       QuarterScheduleController quarterScheduleController,
                                       ScheduleIntervalController intervalController,
                                       AssignedEventViewController assignedEventViewController,
                                       int hint) {

        AssignmentViabilityChecker viabilityChecker = new AssignmentViabilityChecker(courseScheduleController.getCourse(),
                quarterScheduleController.getQuarter(), eventDrag.getEvent().getWeek(),
                intervalController.getWeek(), intervalController.getInterval(),
                eventDrag.getEvent()
        );

        if(viabilityChecker.isAViableAssignment()){ //if event can be assigned there
            //TODO allow multiple course assignments
            courseScheduleController.hideWarnings();
            quarterScheduleController.processEventDrop(
                    eventDrag.getEvent(),
                    eventDrag.dragSource,
                    hint,
                    eventDrag.getIntervalController(),
                    intervalController.getBoundingRegion(),
                    intervalController.getWeek(),
                    intervalController.getInterval()
            );
            eventQuartersMap.put(eventDrag.getEvent().getID(), quarterScheduleController);
            assignmentDone(eventDrag);
        }
        else {
            Warning warning = viabilityChecker.getWarning();
            courseScheduleController.popUpWarning(warning);
        }
    }

    public void processEventUnassignment(QuarterScheduleController quarterScheduleController, NewEvent event){
        quarterScheduleController.unassignEvent(event);
        eventQuartersMap.remove(event.getID());
        addUnassignedEvent(event);
    }

    //pre event exists in DB
    public void removeEvent(NewEvent event) {
        QuarterScheduleController quarterScheduleController = eventQuartersMap.get(event.getID());
        if(quarterScheduleController != null) {
            quarterScheduleController.unassignEvent(event);
            eventQuartersMap.remove(event.getID());
        }
        removeUnassignedEvent(event);
    }

    private void removeUnassignedEvent(NewEvent event) {
        Long eventID = event.getID();
        UnassignedEventViewController viewController = unassignedEventsMap.get(eventID);
        if(viewController != null){
            rightPane_VBox.getChildren().remove(viewController.getNode());
            unassignedEventsMap.remove(eventID);
        }
    }

    public class EventDrag{
        public static final int FROM_UNASSIGNED = 1;
        public static final int FROM_ASSIGNED = 2;

        public final int dragSource;
        private final MainController mainController;
        private final EventViewController eventViewController;
        private final NewEvent event;

        private final ScheduleIntervalController intervalController;

        public EventDrag(NewEvent event, int dragSource, MainController mainController, EventViewController eventViewController, ScheduleIntervalController intervalController){
            this.event = event;
            this.dragSource = dragSource;
            this.mainController = mainController;
            this.eventViewController = eventViewController;
            this.intervalController = intervalController;
        }

        public EventViewController getEventViewController() { return eventViewController; }

        public ScheduleIntervalController getIntervalController() { return intervalController; }

        public void finish(){
            mainController.finishEventDrag();
        }

        public boolean fromAssigned() {
            return dragSource == FROM_ASSIGNED;
        }

        public NewEvent getEvent() { return event; }
    }

    public EventDrag startEventDrag(NewEvent event, int eventType, EventViewController viewController, ScheduleIntervalController intervalController){
        eventDrag = new EventDrag(event, eventType, this, viewController, intervalController);
        return eventDrag;
    }

    public EventDrag getEventDrag(){
        return eventDrag;
    }

    public void assignmentDone(EventDrag eventDrag) {
        if(eventDrag.dragSource == EventDrag.FROM_UNASSIGNED) rightPane_VBox.getChildren().remove(eventDrag.getEventViewController().getNode());
    }

    public void finishEventDrag(){ eventDrag = null; }

    private EventDrag eventDrag = null;

    private File userProjectFile = null;
    private boolean userMadeChanges = false; //TODO update when user made changes

    private CourseDatabase courseDatabase = MainApp.getDatabase().courseDatabase();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Setting text depending on the language chosen //
        initializeLanguage();

        // Setting button behaviour //
        setButtonActions();

        // Setting course view behavior //
        configureCoursePane();

        configureUnassignedEventPane();
    }

    private void initializeLanguage() {

        menuBar_fileMenu.setText(AppSettings.language().getItem("fileMenu"));
        fileMenu_open.setText(AppSettings.language().getItem("fileMenu_open"));
        fileMenu_save.setText(AppSettings.language().getItem("fileMenu_save"));
        fileMenu_saveAs.setText(AppSettings.language().getItem("fileMenu_saveAs"));
        fileMenu_close.setText(AppSettings.language().getItem("fileMenu_close"));

        menuBar_editMenu.setText(AppSettings.language().getItem("editMenu"));

        menuBar_settingsMenu.setText(AppSettings.language().getItem("settingsMenu"));
        settingsMenu_appSettings.setText(AppSettings.language().getItem("settingsMenu_appSettings"));

        menuBar_helpMenu.setText(AppSettings.language().getItem("helpMenu"));
        helpMenu_about.setText(AppSettings.language().getItem("helpMenu_about"));

        addButtons_title.setText(AppSettings.language().getItem("addButtons_title"));
        addButtons_course.setText(AppSettings.language().getItem("addButtons_course"));
        addButtons_subject.setText(AppSettings.language().getItem("addButtons_subject"));
        addButtons_event.setText(AppSettings.language().getItem("addButtons_event"));

        manageButtons_title.setText(AppSettings.language().getItem("manageButtons_title"));
        manageButtons_courses.setText(AppSettings.language().getItem("manageButtons_courses"));
        manageButtons_resources.setText(AppSettings.language().getItem("manageButtons_resources"));
        manageButtons_subjects.setText(AppSettings.language().getItem("manageButtons_subjects"));
        manageButtons_events.setText(AppSettings.language().getItem("manageButtons_events"));
        //manageButtons_unfinishedEvents.setText(AppSettings.language().getItem("manageButtons_unfinishedEvents"));

        viewButtons_title.setText(AppSettings.language().getItem("viewButtons_title"));
        viewButtons_eventList.setText(AppSettings.language().getItem("viewButtons_eventList"));
        //viewButtons_unfinishedEventsList.setText(AppSettings.language().getItem("viewButtons_unfinishedEventsList"));

        runButtons_title.setText(AppSettings.language().getItem("runButtons_title"));
        runButtons_solve.setText(AppSettings.language().getItem("runButtons_solve"));
        runButtons_optimize.setText(AppSettings.language().getItem("runButtons_optimize"));
        runButtons_timeoutLabel.setText(AppSettings.language().getItem("runButtons_timeout"));
        runButtons_timeoutField.setPromptText(AppSettings.language().getItem("runButtons_timeoutUnit"));
        runButtons_timeoutField.setTooltip(new Tooltip(AppSettings.language().getItem("runButtons_timeoutTooltip")));
        runButtons_stop.setText(AppSettings.language().getItem("runButtons_stop"));

        rightPane_eventSearch.setPromptText(AppSettings.language().getItem("rightPane_eventSearch"));
    }

    private void configureCoursePane() {
        addCourseTab();

        //courseTabs.getSelectionModel().select(courseTabs_addTab);

        //set new tab behavior
        courseTabs_addTab.setOnSelectionChanged(event -> {
            //FIXME: when you cancel course creation, tab gets selected and cannot be deselected because there's only 1 tab
            if(courseTabs_addTab.isSelected()) {
                promptCourseForm();
                event.consume();
            }
        });
    }

    private void configureUnassignedEventPane() {
        rightPane_scrollPane.setOnMouseDragReleased(event -> {
            EventDrag eventDrag = this.getEventDrag();
            if(eventDrag.dragSource == EventDrag.FROM_ASSIGNED) processEventUnassignment(eventDrag.getIntervalController().getQuarterScheduleController(), eventDrag.getEvent());
            event.consume();
        });
    }

    private void setButtonActions() {
        fileMenu_open.setOnAction(event -> openFile());
        fileMenu_save.setOnAction(event -> saveToFile());
        fileMenu_saveAs.setOnAction(event -> saveToNewFile());

        addButtons_course.setOnAction(actionEvent -> promptCourseForm());
        addButtons_subject.setOnAction(actionEvent -> promptSubjectForm());
        addButtons_event.setOnAction(actionEvent -> promptEventForm());

        manageButtons_courses.setOnAction(event -> promptCourseManager());
        manageButtons_resources.setOnAction(actionEvent ->
                promptResourceManager(manageButtons_resources.getScene().getWindow(), null));
        manageButtons_subjects.setOnAction(event -> promptSubjectManager());
        manageButtons_events.setOnAction(event -> promptEventManager());

        viewButtons_eventList.setOnAction(event -> {
            if(viewButtons_eventList.isSelected()) mainBorderPane.setRight(rightPane);
            else mainBorderPane.setRight(null);
            event.consume();
        });

        runButtons_timeoutField.setText(String.valueOf(AppSettings.defaultTimeout()));

        runButtons_solve.setOnAction(event -> {
            MainApp.solve(getTimeoutFieldValue());
            event.consume();
        });
        runButtons_stop.setOnAction(event -> {
            MainApp.stopSolver();
            event.consume();
        });
    }

    private double getTimeoutFieldValue() {
        double timeout;

        try{
            timeout = Double.parseDouble(runButtons_timeoutField.getText());
        } catch (NumberFormatException nfe){
            timeout = -1;
        }

        if(timeout < 1) {
            timeout = AppSettings.defaultTimeout();
            runButtons_timeoutField.setText(String.valueOf(timeout));
        }

        return timeout;
    }

    private void openFile(){
        File f = new FileChooser().showOpenDialog(fileMenu_open.getStyleableNode().getScene().getWindow());

        //TODO prompt warning if changes haven't been saved.
        if(f != null){
            userProjectFile = f;
            //TODO simplify try catch blocks
            ObjectInputStream oin = null;
            try{
                FileInputStream fin = new FileInputStream(f);
                oin = new ObjectInputStream(fin);
                MainApp.setDatabase((AppDatabase) oin.readObject());
                projectLoaded();
                fin.close();
            } catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            } finally {
                try { if(oin != null) oin.close(); } catch (IOException ioe){ ioe.printStackTrace(); }
            }
        }
    }

    private void projectLoaded() {
        closeOpenCourseTabs();
        clearEventList();
        reloadDatabase();
        openNewCourseTabs();
        addAddTab();
        addUnassignedEvents();
    }

    private void addUnassignedEvents() {
        ArrayList<NewEvent> unassignedEvents = new ArrayList<>(JavaConverters.asJavaCollection(MainApp.getDatabase().eventDatabase().getUnassignedEvents()));
        for(NewEvent e : unassignedEvents) addUnassignedEvent(e);
    }

    private void closeOpenCourseTabs() {
        courseTabs.getTabs().removeAll(courseTabs.getTabs());
    }

    private void addAddTab() {
        courseTabs.getTabs().add(courseTabs_addTab);
    }

    private void clearEventList() {
        rightPane_VBox.getChildren().removeAll(rightPane_VBox.getChildren());
    }

    private void reloadDatabase() {
        courseDatabase = MainApp.getDatabase().courseDatabase();
    }

    private void openNewCourseTabs() {
        for(Course c : JavaConverters.asJavaCollection(courseDatabase.getElements())){
            addCourseTab(c, true);
        }
        courseTabs.getSelectionModel().select(0);
    }

    private void saveToNewFile() {
        File f = new FileChooser().showSaveDialog(fileMenu_saveAs.getStyleableNode().getScene().getWindow());

        if(f != null){
            userProjectFile = f;
            saveToFile();
        }
        //else TODO notify the user that the file has not been saved
    }

    private void saveToFile() {
        if(userProjectFile != null) {
            if(userMadeChanges || true){
                FileOutputStream fout = null;
                ObjectOutputStream oout = null;

                //TODO simplify try catch blocks
                try{
                    new FileWriter(userProjectFile).close(); //clean file's previous content
                    fout = new FileOutputStream(userProjectFile);
                    oout = new ObjectOutputStream(fout);
                    oout.writeObject(MainApp.getDatabase());
                } catch (IOException ioe){
                    ioe.printStackTrace();
                    //TODO better exception handling
                } finally {
                    try {if(fout != null) fout.close(); } catch(IOException ioe2){ ioe2.printStackTrace();}
                    try {if(oout != null) oout.close(); } catch(IOException ioe3){ ioe3.printStackTrace();}
                }

            }
        } else{
            saveToNewFile(); //be careful with recursion kids
        }
    }

    private void promptCourseForm() {
        Stage prompt = Utils.promptBoundWindow(
                AppSettings.language().getItem("courseForm_windowTitle"),
                addButtons_course.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new CourseFormViewFactory(FXMLPaths.CourseForm(), this)
        );

        prompt.show();
    }

    private void promptSubjectForm(){
        Stage prompt = Utils.promptBoundWindow(
                AppSettings.language().getItem("subjectForm_windowTitle"),
                addButtons_subject.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new SubjectFormViewFactory(FXMLPaths.SubjectForm(), this)
        );

        prompt.show();
    }

    private void promptEventForm() {
        Stage prompt = Utils.promptBoundWindow(
                AppSettings.language().getItem("eventForm_windowTitle"),
                addButtons_event.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.EventForm()),
                new EventFormController(this)
        );

        prompt.show();
    }



    private void promptCourseManager(){
        Stage prompt = Utils.promptBoundWindow(
                AppSettings.language().getItem("courseManager_windowTitle"),
                manageButtons_courses.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.ManageCoursesPanel()),
                new CourseManagerController(this)
        );

        prompt.show();
    }

    public void promptResourceManager(Window owner, CourseResourceManagerController crmc) {

        Stage prompt = Utils.promptBoundWindow(
                AppSettings.language().getItem("manageResources_windowTitle"),
                owner,
                Modality.WINDOW_MODAL,
                new ViewFactory(FXMLPaths.ManageResourcesPanel())
        );

        if(crmc != null) prompt.setOnCloseRequest(event -> crmc.updateResources());

        prompt.show();
    }

    private void promptSubjectManager(){
        Stage prompt = Utils.promptBoundWindow(
                AppSettings.language().getItem("subjectManager_windowTitle"),
                manageButtons_subjects.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.ManageSubjectsPanel()),
                new SubjectManagerController(this)
        );

        prompt.show();
    }

    private void promptEventManager() {
        Stage prompt = Utils.promptBoundWindow(
                AppSettings.language().getItem("eventManager_windowTitle"),
                manageButtons_events.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.ManageEventsPanel()),
                new EventManagerController(this)
        );

        prompt.show();
    }

    //TODO: remove this method, it has only debugging purposes.
    public void addCourseTab(){
        //TODO: decouple course creation and delegate to database.
        Course c = new Course("Default", Option.apply(null),
        new Quarter(new ListBuffer<>(), new NewEventSchedule(AppSettings.timeSlots())), new Quarter(new ListBuffer<>(), new NewEventSchedule(AppSettings.timeSlots())));
        addCourseTab(MainApp.getDatabase().courseDatabase().addCourse(c), false);
    }

    public void addCourseTab(Course c, boolean reload){

        //create course grid.
        Node courseTabContent = null;

        try{
            //TODO load final schedule view
            //courseTabContent = CoursePanelViewFactory.load(this);
            courseTabContent = new CourseScheduleViewFactory(new CourseScheduleController(this,c)).load();
        } catch (IOException e){
            e.printStackTrace();
        }

        //create tab with course grid if possible. If not, create empty tab.
        final Tab newTab = courseTabContent == null ? new Tab(c.name()) : new Tab(c.name(), courseTabContent);

        //mapping the course to the tab
        courseTabMap.put(c.getID(), newTab);

        //Setting tab properties.
        newTab.setClosable(false); //This is done by default.

        //Setting on close action. If only one tab is open, it cannot be closed.
        newTab.setOnCloseRequest(event -> closeCourseTab(c, newTab));

        //FIXME: Last tab can be closed, and shouldn't.

        //Add tab at the end and select it.
        courseTabs.getTabs().add(reload ? courseTabs.getTabs().size() : courseTabs.getTabs().size()-1, newTab);
        courseTabs.getSelectionModel().select(newTab);

        enableTabClosing();
    }

    private void disableTabClosing() {
        for(Tab t: courseTabs.getTabs()){
            t.setClosable(false);
        }
        courseTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }

    private void enableTabClosing() {
        for(Tab t: courseTabs.getTabs()){
            t.setClosable(true);
        }
        courseTabs_addTab.setClosable(false); //This could be done inside the for loop, but I wanted to avoid an if statement there.

        courseTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
    }

    public void closeCourseTab(Course c) {
        closeCourseTab(c, courseTabMap.get(c.getID()));
    }

    //called when the tab is closed from the main interface, so it has to be deleted from db
    public void closeCourseTab(Course c, Tab tab){
        if(tab != null) {
            for(NewEvent e : JavaConverters.asJavaCollection(c.getAllEvents()))
                addUnassignedEvent(e);

            courseTabMap.remove(c.getID());
            courseTabs.getTabs().remove(tab);
            courseDatabase.removeCourse(c);

            if(courseTabs.getTabs().size() < 3){ //If there's only one tab left (not including creation tab).
                disableTabClosing();
            }
        }
    }

    public void addUnassignedEvent(NewEvent event){
        Node eventView = null;
        UnassignedEventViewController controller = new UnassignedEventViewController(this, event);

        try{
            eventView = new ViewFactory<>(FXMLPaths.UnassignedEvent()).load(controller);
        } catch (IOException ioe){
            ioe.printStackTrace();
        }

        if (eventView != null){
            unassignedEventsMap.put(event.getID(), controller);
            rightPane_VBox.getChildren().add(eventView); //TODO: improve this with a method that updates the list.
            eventViewMap.put(event.getID(), eventView);
        }
    }
}
