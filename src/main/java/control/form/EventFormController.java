package control.form;

import app.AppSettings;
import app.FXMLPaths;
import app.MainApp;
import control.MainController;
import control.manage.EventIncompatibilityManagerController;
import factory.ViewFactory;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import misc.Duration;
import misc.Warning;
import misc.Weeks;
import model.*;
import scala.collection.JavaConverters;
import scala.collection.convert.AsScalaConverters;
import service.EventDatabase;
import service.ResourceDatabase;
import service.SubjectDatabase;
import util.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EventFormController implements Initializable {

    private final MainController mainController;

    public Label eventNameTag;
    public TextField eventNameField;

    public Label eventShortNameTag;
    public TextField eventShortNameField;

    public Label eventDescriptionTag;
    public TextArea eventDescriptionField;
    public CheckBox wrapEventDescriptionCheckbox;

    public Label eventSubjectTag;
    public ComboBox<Subject> eventSubjectBox;
    public Button unassignSubjectButton;

    public Label eventDurationTag;
    public ComboBox<Duration> eventDurationBox;

    public Label eventTypeTag;
    public ComboBox<EventType> eventTypeBox;

    public Label eventWeekTag;
    public ComboBox<Weeks.Week> eventWeekBox;

    public Label eventResourceTag;
    public ComboBox<Resource> eventResourceBox;
    public Button unassignResourceButton;

    public Button manageIncompatibilitiesButton;
    //public Button managePrecedencesButton;

    public Label warningTag;

    public Button createEventButton;

    private SubjectDatabase subjectDatabase = MainApp.getDatabase().subjectDatabase();
    private EventDatabase eventDatabase = MainApp.getDatabase().eventDatabase();
    private ResourceDatabase resourceDatabase = MainApp.getDatabase().resourceDatabase();
    private ArrayList<NewEvent> incompatibilities = new ArrayList<>();

    public EventFormController(MainController mainController){
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        setupViews();
        initializeWarningSystem();
        bindActions();
    }

    private void initializeContentLanguage() {
        eventNameTag.setText(AppSettings.language().getItem("eventForm_eventName"));
        eventNameField.setPromptText(AppSettings.language().getItem("eventForm_eventNameHelp"));

        eventShortNameTag.setText(AppSettings.language().getItem("eventForm_eventShortName"));
        eventShortNameField.setPromptText(AppSettings.language().getItem("eventForm_eventShortNameHelp"));

        eventDescriptionTag.setText(AppSettings.language().getItem("eventForm_eventDescription"));
        eventDescriptionField.setPromptText(AppSettings.language().getItem("eventForm_eventDescriptionHelp"));
        wrapEventDescriptionCheckbox.setText(AppSettings.language().getItem("form_wrapDescription"));

        eventSubjectTag.setText(AppSettings.language().getItem("eventForm_eventSubjectTag"));

        eventDurationTag.setText(AppSettings.language().getItem("eventForm_eventDurationTag"));

        eventTypeTag.setText(AppSettings.language().getItem("eventForm_eventTypeTag"));

        eventWeekTag.setText(AppSettings.language().getItem("eventForm_eventWeekTag"));

        eventResourceTag.setText(AppSettings.language().getItem("eventForm_eventResourceTag"));

        manageIncompatibilitiesButton.setText(AppSettings.language().getItem("eventForm_manageIncompatibilities") + "...");

        createEventButton.setText(AppSettings.language().getItem("eventForm_confirmationButton"));
    }

    private void setupViews() {
        eventSubjectBox.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(subjectDatabase.getFinishedSubjects())));
        eventSubjectBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Subject object) {
                if (object == null) return null;
                else return object.getName();
            }

            @Override
            public Subject fromString(String string) {
                return null;
            }
        });

        eventDurationBox.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(Duration.getDurations())));

        eventTypeBox.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(EventTypes.allEventTypes())));

        eventWeekBox.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(Weeks.weekList())));

        eventResourceBox.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(resourceDatabase.getElements())));
        eventResourceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Resource object) {
                if (object == null) return null;
                else return object.getName();
            }

            @Override
            public Resource fromString(String string) {
                return null;
            }
        });
    }

    private void bindActions() {
        wrapEventDescriptionCheckbox.selectedProperty().bindBidirectional(eventDescriptionField.wrapTextProperty());

        unassignSubjectButton.setOnAction(event -> {
            eventSubjectBox.getSelectionModel().clearSelection();
            event.consume();
        });

        unassignResourceButton.setOnAction(event -> {
            eventResourceBox.getSelectionModel().clearSelection();
            event.consume();
        });

        manageIncompatibilitiesButton.setOnAction(event -> {
            manageIncompatibilities(incompatibilities);
            event.consume();
        });

        createEventButton.setOnAction(event -> {
            if(createEvent()) closeWindow();
            event.consume();
        });
    }

    private void closeWindow() {
        ((Stage)createEventButton.getScene().getWindow()).close();
    }

    private void manageIncompatibilities(ArrayList<NewEvent> incompatibilities) {
        Stage manager = Utils.promptBoundWindow(
                AppSettings.language().getItem("eventForm_manageIncompatibilities"),
                manageIncompatibilitiesButton.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.EventIncompatibilityFrom()),
                new EventIncompatibilityManagerController(incompatibilities)
        );

        manager.show();
    }

    private boolean createEvent() {
        if(!warnings()){
            NewEvent event = new NewEvent();

            event.setName(eventNameField.getText().trim());
            event.setShortName(eventShortNameField.getText().trim());
            event.setDescription(eventDescriptionField.getText().trim());
            event.setDuration(eventDurationBox.getValue().toInt());
            event.setEventType(eventTypeBox.getValue());
            event.setWeek(eventWeekBox.getValue());
            for(NewEvent e: incompatibilities) event.addIncompatibility(e);

            Resource resource = eventResourceBox.getValue();
            if(resource != null){
                event.setNeededResource(resource);
            }

            eventDatabase.addElement(event);

            Subject subject = eventSubjectBox.getValue();
            if(subject != null) {
                event.setSubject(subject);
                subject.addEvent(event.getID(), event);
            }

            mainController.addUnassignedEvent(event);
            return true;
        }
        return false;
    }

    private boolean warnings() {
        Warning warning = checkWarnings();
        if(warning == null){
            hideWarnings();
            return false;
        }
        else{
            popUpWarning(warning);
            return true;
        }
    }

    private Warning checkWarnings() {
        if(eventNameField.getText().isBlank()){
            return new Warning(AppSettings.language().getItem("warning_eventNameCannotBeEmpty"));
        }
        else if(eventDurationBox.getValue() == null){
            return new Warning(AppSettings.language().getItem("warning_durationCannotBeEmpty"));
        }
        else if(eventTypeBox.getValue() == null){
            return new Warning(AppSettings.language().getItem("warning_eventTypeCannotBeEmpty"));
        }
        else if(eventWeekBox.getValue() == null){
            return new Warning(AppSettings.language().getItem("warning_eventWeekCannotBeEmpty"));
        }
        else return null;
    }

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

}
