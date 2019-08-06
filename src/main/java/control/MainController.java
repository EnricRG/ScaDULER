package control;

import app.AppSettings;
import app.FXMLPaths;
import factory.CourseViewFactory;
import gui.EventForm;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Course;
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
        setLanguageTags();

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
        addButtons_event.setOnAction(actionEvent -> EventForm.promptForm(1));
    }

    private void setLanguageTags() {

        menuBar_fileMenu.setText(AppSettings.Language().getItem("fileMenu"));
        fileMenu_save.setText(AppSettings.Language().getItem("fileMenu_save"));
        fileMenu_saveAs.setText(AppSettings.Language().getItem("fileMenu_saveAs"));
        fileMenu_close.setText(AppSettings.Language().getItem("fileMenu_close"));

        menuBar_editMenu.setText(AppSettings.Language().getItem("editMenu"));

        menuBar_settingsMenu.setText(AppSettings.Language().getItem("settingsMenu"));
        settingsMenu_appSettings.setText(AppSettings.Language().getItem("settingsMenu_appSettings"));

        menuBar_helpMenu.setText(AppSettings.Language().getItem("helpMenu"));
        helpMenu_about.setText(AppSettings.Language().getItem("helpMenu_about"));

        addButtons_title.setText(AppSettings.Language().getItem("addButtons_title"));
        addButtons_course.setText(AppSettings.Language().getItem("addButtons_course"));
        addButtons_subject.setText(AppSettings.Language().getItem("addButtons_subject"));
        addButtons_event.setText(AppSettings.Language().getItem("addButtons_event"));

        manageButtons_title.setText(AppSettings.Language().getItem("manageButtons_title"));
        manageButtons_courses.setText(AppSettings.Language().getItem("manageButtons_courses"));
        manageButtons_subjects.setText(AppSettings.Language().getItem("manageButtons_subjects"));
        manageButtons_events.setText(AppSettings.Language().getItem("manageButtons_events"));
        manageButtons_unfinishedEvents.setText(AppSettings.Language().getItem("manageButtons_unfinishedEvents"));

        viewButtons_title.setText(AppSettings.Language().getItem("viewButtons_title"));
        viewButtons_eventList.setText(AppSettings.Language().getItem("viewButtons_eventList"));
        viewButtons_unfinishedEventsList.setText(AppSettings.Language().getItem("viewButtons_unfinishedEventsList"));

        runButtons_title.setText(AppSettings.Language().getItem("runButtons_title"));
        runButtons_solve.setText(AppSettings.Language().getItem("runButtons_solve"));
        runButtons_optimize.setText(AppSettings.Language().getItem("runButtons_optimize"));
        runButtons_stop.setText(AppSettings.Language().getItem("runButtons_stop"));

        rightPane_eventSearch.setPromptText(AppSettings.Language().getItem("rightPane_eventSearch"));
    }

    public void addCourseTab(){
        //TODO: this has to prompt a screen to create a course.
        Course c = new Course(AppSettings.Language().getItem("course") + courseTabs.getTabs().size());
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

        //TODO: Find a bug somewhere that allows the last tab to be closed.

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
