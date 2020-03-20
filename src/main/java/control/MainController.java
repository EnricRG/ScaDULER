package control;

import app.*;
import control.form.CourseFormController;
import control.form.EventFormController;
import control.form.SubjectFormController;
import control.manage.CourseManagerController;
import control.manage.EventManagerController;
import control.manage.ResourceManagerController;
import control.manage.SubjectManagerController;
import control.schedule.*;
import control.select.ResourceSelectorController;
import exception.FileFormatException;
import factory.CourseScheduleViewFactory;
import factory.ViewFactory;
import file.in.ImportError;
import file.in.ImportJob;
import file.in.MCFImportReader;
import file.in.SRFImporter;
import file.out.SRFExporter;
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
import model.Event;
import model.Quarter;
import model.Quarters;
import scala.collection.JavaConverters;
import service.AppDatabase;
import service.CourseDatabase;
import service.ResourceDatabase;
import solver.EventAssignment;
import util.Utils;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MainController extends StageController {

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

    public Menu fileMenu_importMenu;
    public MenuItem importMenu_importSubjects;
    public MenuItem importMenu_importResources;

    public Menu fileMenu_exportMenu;
    public MenuItem exportMenu_exportResources;

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

    public CheckBox softViabilityCheckBox;

    public TabPane courseTabs;
    public Tab courseTabs_addTab;

    public ScrollPane leftPane;

    public VBox rightPane;
    public TextField rightPane_eventSearch;
    public ScrollPane rightPane_scrollPane;
    public VBox rightPane_VBox;

    private Map<Tab, CourseScheduleController> tabCourseMap = new HashMap<>();
    private Map<Long, QuarterScheduleController> eventQuartersMap = new HashMap<>();
    private Map<Long, UnassignedEventViewController> unassignedEventsMap = new HashMap<>();
    private Map<Long, Tab> courseTabMap = new HashMap<>();
    private Map<Long, Node> eventViewMap = new HashMap<>();

    private Map<Long, Event> nonViableEventAssignments = new HashMap<>();
    private boolean selectingTabs;

    public void processEventAssignment(CourseScheduleController courseScheduleController,
                                       QuarterScheduleController quarterScheduleController,
                                       ScheduleIntervalController intervalController,
                                       int hint) {

        AssignmentViabilityChecker viabilityChecker = new AssignmentViabilityChecker(
                courseScheduleController.getCourse(),
                quarterScheduleController.getQuarterData(),
                intervalController.getWeek(),
                intervalController.getInterval(),
                eventDrag.getEvent()
        );

        if(viabilityChecker.isAViableAssignment() || AppSettings.softViabilityCheck()){ //if event can be assigned there

            courseScheduleController.hideWarnings();
            quarterScheduleController.processEventDrop(
                    eventDrag.getEvent(),
                    eventDrag.dragSource,
                    hint,
                    intervalController.getWeek(),
                    intervalController.getInterval(),
                    viabilityChecker.isAViableAssignment()
            );
            eventQuartersMap.put(eventDrag.getEvent().getID(), quarterScheduleController);
            if(!viabilityChecker.isAViableAssignment()) nonViableEventAssignments.put(eventDrag.getEvent().getID(), eventDrag.getEvent());
            assignmentDone(eventDrag);
        }

        if(!viabilityChecker.isAViableAssignment()) {
            Warning warning = viabilityChecker.getWarning();
            courseScheduleController.popUpWarning(warning);
        }
    }

    public void processEventUnassignment(QuarterScheduleController quarterScheduleController, Event event){
        quarterScheduleController.unassignEvent(event);
        eventQuartersMap.remove(event.getID());
        nonViableEventAssignments.remove(event.getID());
        addUnassignedEvent(event);
    }

    public void processEventAssignments(Collection<EventAssignment> eventAssignments){
        for(EventAssignment ea : eventAssignments){
            Event event = MainApp.getDatabase().eventDatabase().getElementOrElse(ea.eventID(), null);

            CourseScheduleController courseScheduleController = getEventCourseController(event);
            QuarterScheduleController quarterScheduleController = courseScheduleController.getQuarterController(event.getQuarter());
            ScheduleIntervalController intervalController =
                    quarterScheduleController.getIntervalControllerAt(ea.week().toWeekNumber(), ea.interval());

            startEventDrag(
                    event,
                    EventDrag.FROM_UNASSIGNED,
                    unassignedEventsMap.get(ea.eventID()),
                    intervalController);

            processEventAssignment(
                    courseScheduleController,
                    quarterScheduleController,
                    intervalController,
                    -1);
        }
    }

    private CourseScheduleController getEventCourseController(Event event) {
        //TODO highly improvable, better data structure
        return tabCourseMap.get(courseTabMap.get(event.getCourse().getID()));
    }

    private CourseScheduleController getVisibleCourse() {
        return tabCourseMap.get(courseTabs.getSelectionModel().getSelectedItem());
    }

    //pre event exists in DB
    public void removeEvent(Event event) {
        QuarterScheduleController quarterScheduleController = eventQuartersMap.get(event.getID());
        if(quarterScheduleController != null) {
            quarterScheduleController.unassignEvent(event);
            eventQuartersMap.remove(event.getID());
        }
        removeUnassignedEvent(event);
    }

    private void removeUnassignedEvent(Event event) {
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
        private final Event event;

        private final ScheduleIntervalController intervalController;

        public EventDrag(Event event, int dragSource, MainController mainController, EventViewController eventViewController, ScheduleIntervalController intervalController){
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

        public Event getEvent() { return event; }
    }

    public EventDrag startEventDrag(Event event, int dragSource, EventViewController viewController, ScheduleIntervalController intervalController){
        eventDrag = new EventDrag(event, dragSource, this, viewController, intervalController);
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
    private boolean debug = true;

    private CourseDatabase courseDatabase = MainApp.getDatabase().courseDatabase();
    private ResourceDatabase resourceDatabase = MainApp.getDatabase().resourceDatabase();


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
        fileMenu_importMenu.setText(AppSettings.language().getItemOrElse("fileMenu_importMenu", "Import"));
        fileMenu_exportMenu.setText(AppSettings.language().getItemOrElse("fileMenu_exportMenu", "Export"));
        fileMenu_saveAs.setText(AppSettings.language().getItem("fileMenu_saveAs"));
        fileMenu_close.setText(AppSettings.language().getItem("fileMenu_close"));

        importMenu_importSubjects.setText(AppSettings.language().getItemOrElse("importMenu_importSubjects", "Subjects..."));
        importMenu_importResources.setText(AppSettings.language().getItemOrElse("importMenu_importResources", "Resources..."));

        exportMenu_exportResources.setText(AppSettings.language().getItemOrElse("importMenu_importResources", "Resources..."));

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

        softViabilityCheckBox.setText(AppSettings.language().getItem("softViabilityCheckBox"));

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

        importMenu_importSubjects.setOnAction(event -> importSubjects());
        importMenu_importResources.setOnAction(event -> importResources());
        exportMenu_exportResources.setOnAction(event -> exportResources());

        addButtons_course.setOnAction(actionEvent -> promptCourseForm());
        addButtons_subject.setOnAction(actionEvent -> promptSubjectForm());
        addButtons_event.setOnAction(actionEvent -> promptEventForm());

        manageButtons_courses.setOnAction(event -> promptCourseManager());
        manageButtons_resources.setOnAction(actionEvent ->
                promptResourceManager(manageButtons_resources.getScene().getWindow()));
        manageButtons_subjects.setOnAction(event -> promptSubjectManager());
        manageButtons_events.setOnAction(event -> promptEventManager());

        viewButtons_eventList.setOnAction(event -> {
            if(viewButtons_eventList.isSelected()) mainBorderPane.setRight(rightPane);
            else mainBorderPane.setRight(null);
            event.consume();
        });

        runButtons_timeoutField.setText(String.valueOf(AppSettings.defaultTimeout()));

        runButtons_solve.setOnAction(event -> {
            solve();
            event.consume();
        });
        runButtons_stop.setOnAction(event -> {
            MainApp.stopSolver();
            event.consume();
        });

        //TODO this should be done on a setUpViews method.
        softViabilityCheckBox.setSelected(AppSettings.softViabilityCheck());

        softViabilityCheckBox.setOnAction(actionEvent -> {
            AppSettings.softViabilityCheck_$eq(softViabilityCheckBox.isSelected());
            actionEvent.consume();
        });

        rightPane_eventSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRightPane(rightPane_eventSearch.getText().trim());
        });
    }

    private void solve() {
        if(nonViableEventAssignments.isEmpty()) MainApp.solve(getTimeoutFieldValue());
        else{
            boolean accepted = promptChoice(
                    AppSettings.language().getItem("nonViableAssignments_windowTitle"),
                    AppSettings.language().getItem("nonViableAssignments_message")
            );

            if(accepted){
                for(Event e: new ArrayList<>(nonViableEventAssignments.values())){
                    processEventUnassignment(eventQuartersMap.get(e.getID()), e);
                }
                //nonViableEventAssignments.clear(); //this is not necessary, code above has the same effect.
                solve(); //recursion hehe
            }
        }
    }

    private void filterRightPane(String text) {
        ArrayList<Node> filteredEvents = new ArrayList<>();

        for(EventViewController evc: unassignedEventsMap.values())
            if(evc.getEvent().getName().toLowerCase().contains(text.toLowerCase())) filteredEvents.add(evc.getNode());

        rightPane_VBox.getChildren().clear();
        rightPane_VBox.getChildren().addAll(filteredEvents);
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
        File f = new FileChooser().showOpenDialog(stage.getScene().getWindow());

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
            } catch (InvalidClassException ice){
                ice.printStackTrace();
                promptAlert(
                        AppSettings.language().getItem("unknownFileFormat_windowTitle"),
                        AppSettings.language().getItem("unknownFileFormat_explanation")
                );
            }
            catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            } finally {
                try { if(oin != null) oin.close(); } catch (IOException ioe){ ioe.printStackTrace(); }
            }
        }
    }

    private void importSubjects() {
        File f = new FileChooser().showOpenDialog(stage.getScene().getWindow());

        if(f != null){
            String extension = Utils.getFileExtension(f.getName());

            //TODO factory
            //TODO make it language specific
            if(extension == null) promptAlert("Error", "The file does not have an extension.");
            else if(extension.equals(MCFImportReader.MCFFileExtension())){
                importFromMCF(f);
            }
            else promptAlert("Error", "Unknown file extension.");
        }
    }

    private void importFromMCF(File f) {
        MCFImportReader reader = new MCFImportReader(f, MainApp.getDatabase().getReadOnlyDatabase());

        ImportJob importJob = reader.read().getImportJob();

        if(importJob.errors().nonEmpty()){
            //TODO print errors to new window
            for(ImportError e: JavaConverters.asJavaCollection(importJob.errors())){
                System.out.println(e.message());
            }
        }
        else{
            EntityManager.importEntities(importJob, this);
        }
    }

    private void importResources() {
        //TODO implement method
        System.out.println("Import resources");

        File f = new FileChooser().showOpenDialog(stage.getScene().getWindow());

        if(f != null){
            //TODO importer factory
            try {
                EntityManager.importResources(new SRFImporter(f));
            }
            catch (IOException ioe) {
                promptAlert(
                    AppSettings.language().getItemOrElse("error_windowTitle", "Error"),
                    AppSettings.language().getItemOrElse("readFileError_explanation",
                            "Error reading the file. Is it being used by another program?")
                );
            }
            catch (FileFormatException ffe) {
                promptAlert(
                        AppSettings.language().getItem("unknownFileFormat_windowTitle"),
                        AppSettings.language().getItem("unknownFileFormat_explanation")
                );
            }
        }
    }

    private void exportResources() {
        //TODO implement method
        System.out.println("Export resources");

        ResourceSelectorController selector = new ResourceSelectorController(resourceDatabase.getElements());
        selector.setStage(Utils.promptBoundWindow(
                "title", //TODO title
                stage.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.EntitySelectorPanel()),
                selector
        ));

        selector.waitSelection();

        File f = null; //new FileChooser().showSaveDialog(stage.getScene().getWindow());

        if(f != null){
            //TODO exporter factory
            try {
                EntityManager.exportResources(new SRFExporter(f), resourceDatabase.getElements());
            }
            catch (IOException ioe) {
                promptAlert(
                        AppSettings.language().getItemOrElse("error_windowTitle", "Error"),
                        AppSettings.language().getItemOrElse("readFileError_explanation",
                                "Error reading the file. Is it being used by another program?")
                );
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

    public void promptAlert(String title, String message){
        Stage alert = Utils.promptBoundWindow(
                title,
                addButtons.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.BasicAlert()),
                new BasicAlertController(message)
        );

        alert.show();
    }

    public boolean promptChoice(String title, String message){
        BinaryChoiceAlertController controller = new BinaryChoiceAlertController(message);

        Stage alert = Utils.promptBoundWindow(
                title,
                addButtons.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.ChoiceAlert()),
                controller
        );

        alert.showAndWait();

        return controller.accepted();
    }

    private void addUnassignedEvents() {
        ArrayList<Event> unassignedEvents = new ArrayList<>(JavaConverters.asJavaCollection(MainApp.getDatabase().eventDatabase().getUnassignedEvents()));
        for(Event e : unassignedEvents) addUnassignedEvent(e);
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
        File f = new FileChooser().showSaveDialog(stage.getScene().getWindow());

        if(f != null){
            userProjectFile = f;
            saveToFile();
        }
        //else TODO notify the user that the file has not been saved
    }

    private void saveToFile() {
        if(userProjectFile != null) {
            if(userMadeChanges || debug){
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
        StageController stageController = new CourseFormController(this);

        stageController.setStage(Utils.promptBoundWindow(
                AppSettings.language().getItem("courseForm_windowTitle"),
                addButtons_course.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.CourseForm()),
                stageController
        ));

        stageController.show();
    }

    private void promptSubjectForm(){
        StageController stageController = new SubjectFormController(this);

        stageController.setStage(Utils.promptBoundWindow(
                AppSettings.language().getItem("subjectForm_windowTitle"),
                addButtons_subject.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.SubjectForm()),
                stageController
        ));

        stageController.show();
    }

    private void promptEventForm() {
        StageController stageController = new EventFormController(this);

        stageController.setStage(Utils.promptBoundWindow(
                AppSettings.language().getItem("eventForm_windowTitle"),
                addButtons_event.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.EventForm()),
                stageController
        ));

        stageController.show();
    }



    private void promptCourseManager(){
        Utils.promptBoundWindow(
                AppSettings.language().getItem("courseManager_windowTitle"),
                manageButtons_courses.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.EntityManagerPanel()),
                new CourseManagerController(this)
        ).show();
    }

    public void promptResourceManager(Window owner) {
        Utils.promptBoundWindow(
                AppSettings.language().getItem("manageResources_windowTitle"),
                owner,
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.ManageResourcesPanel()),
                new ResourceManagerController(this)
        ).show();
    }

    private void promptSubjectManager(){
        Utils.promptBoundWindow(
                AppSettings.language().getItem("subjectManager_windowTitle"),
                manageButtons_subjects.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.EntityManagerPanel()),
                new SubjectManagerController(this)
        ).show();
    }

    private void promptEventManager() {
        Utils.promptBoundWindow(
                AppSettings.language().getItem("eventManager_windowTitle"),
                manageButtons_events.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.EntityManagerPanel()),
                new EventManagerController(this)
        ).show();
    }

    public void selectQuarterTabs(Quarter quarter){
        if(!selectingTabs) {
            selectingTabs = true;
            ArrayList<Tab> temporalCourseTabs = new ArrayList<>(courseTabs.getTabs());
            temporalCourseTabs.remove(courseTabs_addTab);
            for(Tab t: temporalCourseTabs){
                CourseScheduleController courseController = tabCourseMap.get(t);
                Tab quarterTab = quarter == Quarters.firstQuarter() ?
                        courseController.firstQuarterTab : courseController.secondQuarterTab;
                if(!quarterTab.isSelected()) courseController.tabPane.getSelectionModel().select(quarterTab);
            }
            selectingTabs = false;
        }
    }

    //TODO: remove this method, it has only debugging purposes.
    public void addCourseTab(){
        Course c = courseDatabase.createCourse()._2;
        c.setName("Default");
        addCourseTab(c, false);
    }

    public void addCourseTab(Course c, boolean reload){

        //create course grid.
        Node courseTabContent = null;

        CourseScheduleController controller = new CourseScheduleController(this,c);

        try{
            //TODO load final schedule view
            //courseTabContent = CoursePanelViewFactory.load(this);
            courseTabContent = new CourseScheduleViewFactory(controller).load();
        } catch (IOException e){
            e.printStackTrace();
        }

        //create tab with course grid if possible. If not, create empty tab.
        final Tab newTab = courseTabContent == null ? new Tab(c.getName()) : new Tab(c.getName(), courseTabContent);

        //mapping the course to the tab
        courseTabMap.put(c.getID(), newTab);

        //Setting tab properties.
        newTab.setClosable(false); //This is done by default.

        //Setting on close action. If only one tab is open, it cannot be closed.
        newTab.setOnCloseRequest(event -> closeCourseTab(c, newTab));

        //FIXME: Last tab can be closed, and shouldn't.

        tabCourseMap.put(newTab, controller);

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
            for(Event e : JavaConverters.asJavaCollection(c.getAllEvents()))
                addUnassignedEvent(e);

            courseTabMap.remove(c.getID());
            tabCourseMap.remove(tab);
            courseTabs.getTabs().remove(tab);
            courseDatabase.removeCourse(c);

            if(courseTabs.getTabs().size() < 3){ //If there's only one tab left (not including creation tab).
                disableTabClosing();
            }
        }
    }

    public void addUnassignedEvent(Event event){
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
