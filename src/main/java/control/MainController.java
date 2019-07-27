package control;

import app.AppSettings;
import gui.EventForm;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    /** Main application interface element */
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
    public ListView rightPane_eventList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Setting text depending on the language chosen //
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

        // Setting button behaviour //
        addButtons_event.setOnAction(actionEvent -> EventForm.promptForm(1));
    }
}
