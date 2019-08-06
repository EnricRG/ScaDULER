package control;

import app.AppSettings;
import app.FXMLPaths;
import factory.CourseViewFactory;
import factory.ResourceManagerViewFactory;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Course;
import model.Quarter;
import scala.Option;
import scala.collection.mutable.ListBuffer;
import view.DraggableVBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    /** Main application interface element */
    public StackPane mainStackPane;

    public BorderPane mainBorderPane;

    /** Menu bar container */
    public MenuBar menuBar;

    /** File menu */
    public Menu menuBar_fileMenu;
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
    public Button manageButtons_courseResources;
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
    public Button runButtons_stop;

    public TabPane courseTabs;
    public Tab courseTabs_addTab;

    public ScrollPane leftPane;

    public VBox rightPane;
    public TextField rightPane_eventSearch;
    public ScrollPane rightPane_scrollPane;
    public VBox rightPane_VBox;


    private boolean tabClosingEnabledFlag = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Setting text depending on the language chosen //
        initializeLanguage();

        // Setting button behaviour //
        setButtonActions();

        // Setting course view behavior //
        configureCoursePane();

        Parent event1;

        try{
            event1 = FXMLLoader.load(new File(FXMLPaths.UnassignedEvent()).toURI().toURL());
        } catch (Exception e){
            e.printStackTrace();
            event1 = new VBox();
        }

        rightPane_VBox.getChildren().add(new DraggableVBox((VBox) event1,this));

        rightPane_VBox.setOnDragEntered(dragEvent -> {
            System.out.println("Right Pane VBOX Drag Entered");
        });

        rightPane_scrollPane.setOnDragOver(dragEvent -> {
            System.out.println("Right Pane Drag Entered");
            dragEvent.acceptTransferModes(TransferMode.ANY);
        });

        rightPane_scrollPane.setOnDragDropped(dragEvent -> {
            Node unassignedEvent = null;
            try {
                unassignedEvent = (Node) dragEvent.getGestureSource();
            } catch (ClassCastException cce){
                cce.printStackTrace();
            }

            if(unassignedEvent != null) {
                moveUnassignedEvent((Node) dragEvent.getGestureSource(), (VBox) ((Node) (dragEvent.getGestureSource())).getParent(), rightPane_VBox);
            }
        });

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

    private void setButtonActions() {
        addButtons_event.setOnAction(actionEvent -> promptEventForm());
        addButtons_course.setOnAction(actionEvent -> promptCourseForm());
        manageButtons_courseResources.setOnAction(actionEvent ->
                promptResourceManager(manageButtons_courseResources.getScene().getWindow()));
    }

    private void promptEventForm() {
        //TODO: prompt event form
    }

    private void promptCourseForm() {
        //TODO: improve coherence adding a FXML factory
        //TODO: write a separate method to handle this
        Scene scene;
        FXMLLoader loader;

        try{
            loader = new FXMLLoader(new File(FXMLPaths.CourseForm()).toURI().toURL());
            Parent root = loader.load();
            scene = new Scene(root);
        } catch (IOException ioe){
            ioe.printStackTrace();
            scene = new Scene(new VBox());
            loader = new FXMLLoader();
        }

        Stage prompt = new Stage();
        prompt.initModality(Modality.WINDOW_MODAL);
        prompt.initOwner(addButtons_course.getScene().getWindow());
        prompt.setTitle(AppSettings.language().getItem("courseForm_createCourseTitle"));
        prompt.setScene(scene);

        CourseFormController cfc = loader.getController();
        cfc.setMainController(this);

        prompt.show();
    }

    public void promptResourceManager(Window owner) {
        Stage prompt = new Stage();
        Scene scene;

        try{
            scene = new Scene((Parent) ResourceManagerViewFactory.load());
        } catch (IOException ioe){
            ioe.printStackTrace();
            scene = new Scene(new VBox());
        }

        prompt.initModality(Modality.WINDOW_MODAL);
        prompt.initOwner(owner);
        prompt.setTitle(AppSettings.language().getItem("manageResources_windowTitle"));
        prompt.setScene(scene);

        prompt.show();
    }

    private void initializeLanguage() {

        menuBar_fileMenu.setText(AppSettings.language().getItem("fileMenu"));
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
        manageButtons_courseResources.setText(AppSettings.language().getItem("manageButtons_courseResources"));
        manageButtons_subjects.setText(AppSettings.language().getItem("manageButtons_subjects"));
        manageButtons_events.setText(AppSettings.language().getItem("manageButtons_events"));
        manageButtons_unfinishedEvents.setText(AppSettings.language().getItem("manageButtons_unfinishedEvents"));

        viewButtons_title.setText(AppSettings.language().getItem("viewButtons_title"));
        viewButtons_eventList.setText(AppSettings.language().getItem("viewButtons_eventList"));
        viewButtons_unfinishedEventsList.setText(AppSettings.language().getItem("viewButtons_unfinishedEventsList"));

        runButtons_title.setText(AppSettings.language().getItem("runButtons_title"));
        runButtons_solve.setText(AppSettings.language().getItem("runButtons_solve"));
        runButtons_optimize.setText(AppSettings.language().getItem("runButtons_optimize"));
        runButtons_stop.setText(AppSettings.language().getItem("runButtons_stop"));

        rightPane_eventSearch.setPromptText(AppSettings.language().getItem("rightPane_eventSearch"));
    }

    //TODO: remove this method, it has only debugging purposes.
    public void addCourseTab(){
        //TODO: decouple course creation and delegate to database.
        Course c = new Course(AppSettings.language().getItem("course") + courseTabs.getTabs().size(), Option.apply(null),
        new Quarter(new ListBuffer<>()), new Quarter(new ListBuffer<>()));
        addCourseTab(c);
    }

    public void addCourseTab(Course c){

        //create course grid.
        Node courseTabContent = null;

        try{
            courseTabContent = CourseViewFactory.load(this);
        } catch (IOException e){
            e.printStackTrace();
        }

        //create tab with course grid if possible. If not, create empty tab.
        final Tab newTab = courseTabContent == null ? new Tab(c.name()) : new Tab(c.name(), courseTabContent);

        //Setting tab properties.
        //newTab.setClosable(true); //This is done by default.

        //Setting on close action. If only one tab is open, it cannot be closed.
        newTab.setOnCloseRequest(event -> {
            TabPane tab_pane = newTab.getTabPane();
            System.out.println(tab_pane.getTabs().size());
            if(tab_pane.getTabs().size() < 3){ //If there's only one tab left (not including creation tab).
                disableTabClosing();
            }
        });

        //FIXME: Last tab can be closed, and shouldn't.

        //Add tab at the end and select it.
        courseTabs.getTabs().add(courseTabs.getTabs().size()-1, newTab);
        courseTabs.getSelectionModel().select(newTab);

        enableTabClosing();
    }

    private void disableTabClosing() {
        for(Tab t: courseTabs.getTabs()){
            t.setClosable(false);
        }
        tabClosingEnabledFlag = false;
    }

    private void enableTabClosing() {
        for(Tab t: courseTabs.getTabs()){
            t.setClosable(true);
        }
        courseTabs_addTab.setClosable(false); //This could be done inside the for loop, but I wanted to avoid an if statement there.
        tabClosingEnabledFlag = true;
    }

    public void moveUnassignedEvent(Node gestureSource, VBox sourceParent, VBox target) {
        if(gestureSource != target ){
            sourceParent.getChildren().remove(gestureSource);
            target.getChildren().add(gestureSource);
        }
    }
}
