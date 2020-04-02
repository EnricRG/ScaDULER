package control.form;

import app.AppSettings;
import app.FXMLPaths;
import app.MainApp;
import control.MainController;
import control.StageController;
import factory.ViewFactory;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import misc.Duration;
import misc.Warning;
import model.*;
import model.blueprint.EventBlueprint;
import scala.collection.JavaConverters;
import service.CourseDatabase;
import service.EventDatabase;
import service.ResourceDatabase;
import service.SubjectDatabase;
import util.Utils;

import java.util.ArrayList;

public class EventFormController extends FormController<EventBlueprint> {

    public Label eventNameTag;
    public TextField eventNameField;

    public Label eventShortNameTag;
    public TextField eventShortNameField;

    public Label eventDescriptionTag;
    public TextArea eventDescriptionField;
    public CheckBox wrapEventDescriptionCheckbox;

    public Label eventCourseTag;
    public ComboBox<Course> eventCourseBox;

    public Label eventQuarterTag;
    public ComboBox<Quarter> eventQuarterBox;

    public Label eventSubjectTag;
    public ComboBox<Subject> eventSubjectBox;
    public Button unassignSubjectButton;

    public Label eventDurationTag;
    public ComboBox<Duration> eventDurationBox;

    public Label eventTypeTag;
    public ComboBox<EventType> eventTypeBox;

    public Label eventPeriodicityTag;
    public ComboBox<Weeks.Periodicity> eventPeriodicityBox;

    public Label eventResourceTag;
    public ComboBox<Resource> eventResourceBox;
    public Button unassignResourceButton;

    public Button manageIncompatibilitiesButton;
    //public Button managePrecedencesButton;

    public Button createEventButton;

    private CourseDatabase courseDatabase = MainApp.getDatabase().courseDatabase();
    private SubjectDatabase subjectDatabase = MainApp.getDatabase().subjectDatabase();
    private EventDatabase eventDatabase = MainApp.getDatabase().eventDatabase();
    private ResourceDatabase resourceDatabase = MainApp.getDatabase().resourceDatabase();
    private ArrayList<Event> incompatibilities = new ArrayList<>();

    public EventFormController(MainController mainController){
        super(mainController);
    }
    public EventFormController(Stage stage, MainController mainController){
        super(stage, mainController);
    }

    @Override
    protected void initializeContentLanguage() {
        eventNameTag.setText(AppSettings.language().getItem("eventForm_eventName"));
        eventNameField.setPromptText(AppSettings.language().getItem("eventForm_eventNameHelp"));

        eventShortNameTag.setText(AppSettings.language().getItem("eventForm_eventShortName"));
        eventShortNameField.setPromptText(AppSettings.language().getItem("eventForm_eventShortNameHelp"));

        eventDescriptionTag.setText(AppSettings.language().getItem("eventForm_eventDescription"));
        eventDescriptionField.setPromptText(AppSettings.language().getItem("eventForm_eventDescriptionHelp"));
        wrapEventDescriptionCheckbox.setText(AppSettings.language().getItem("form_wrapDescription"));

        eventCourseTag.setText(AppSettings.language().getItem("eventForm_eventCourseTag"));

        eventQuarterTag.setText(AppSettings.language().getItem("eventForm_eventQuarterTag"));

        eventSubjectTag.setText(AppSettings.language().getItem("eventForm_eventSubjectTag"));

        eventDurationTag.setText(AppSettings.language().getItem("eventForm_eventDurationTag"));

        eventTypeTag.setText(AppSettings.language().getItem("eventForm_eventTypeTag"));

        eventPeriodicityTag.setText(AppSettings.language().getItem("eventForm_eventPeriodicityTag"));

        eventResourceTag.setText(AppSettings.language().getItem("eventForm_eventResourceTag"));

        manageIncompatibilitiesButton.setText(AppSettings.language().getItem("eventForm_manageIncompatibilities") + "...");

        createEventButton.setText(AppSettings.language().getItem("eventForm_confirmationButton"));
    }

    @Override
    protected void setupViews() {
        eventCourseBox.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(courseDatabase.getCourses())));
        eventCourseBox.getItems().add(0, NoCourse.noCourse());

        eventQuarterBox.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(Quarters.quarters())));
        eventQuarterBox.getItems().add(0, NoQuarter.noQuarter());

        eventSubjectBox.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(subjectDatabase.getFinishedSubjects())));
        eventSubjectBox.setConverter(new StringConverter<Subject>() {
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

        eventPeriodicityBox.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(Weeks.periodicityList())));

        eventResourceBox.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(resourceDatabase.getElements())));
        eventResourceBox.setConverter(new StringConverter<Resource>() {
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

    @Override
    protected void bindActions() {
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
            if(createEvent()) close();
            event.consume();
        });
    }

    private void manageIncompatibilities(ArrayList<Event> incompatibilities) {
        StageController managerController = new EventIncompatibilityFormController(incompatibilities);

        managerController.setStage(Utils.promptBoundWindow(
                AppSettings.language().getItem("eventForm_manageIncompatibilities"),
                manageIncompatibilitiesButton.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new ViewFactory<>(FXMLPaths.EventIncompatibilityFrom()),
                managerController
        ));

        managerController.show();
    }

    private boolean createEvent() {
        if(!warnings()){
            Event event = eventDatabase.createEvent()._2;

            event.setName(eventNameField.getText().trim());
            event.setShortName(eventShortNameField.getText().trim());
            event.setDescription(eventDescriptionField.getText().trim());
            event.setCourse(eventCourseBox.getValue());
            event.setQuarter(eventQuarterBox.getValue());
            event.setDuration(eventDurationBox.getValue().toInt());
            event.setEventType(eventTypeBox.getValue());
            event.setPeriodicity(eventPeriodicityBox.getValue());

            for(Event e: incompatibilities) event.addIncompatibility(e);

            Resource resource = eventResourceBox.getValue();
            if(resource != null){
                event.setNeededResource(resource);
            }

            if(eventCourseBox.getValue() != null) event.setCourse(eventCourseBox.getValue());

            Subject subject = eventSubjectBox.getValue();
            if(subject != null) {
                event.setSubject(subject);
                subject.addEvent(event.getID(), event);
            }

            getMainController().addUnassignedEvent(event);
            return true;
        }
        return false;
    }

    @Override
    protected Warning checkWarnings() {
        if(eventNameField.getText().trim().isEmpty()){
            return new Warning(AppSettings.language().getItem("warning_eventNameCannotBeEmpty"));
        }
        else if(eventCourseBox.getValue() == null){
            return new Warning(AppSettings.language().getItem("warning_courseCannotBeEmpty"));
        }
        else if(eventQuarterBox.getValue() == null){
            return new Warning(AppSettings.language().getItem("warning_quarterCannotBeEmpty"));
        }
        else if(eventDurationBox.getValue() == null){
            return new Warning(AppSettings.language().getItem("warning_durationCannotBeEmpty"));
        }
        else if(eventTypeBox.getValue() == null){
            return new Warning(AppSettings.language().getItem("warning_eventTypeCannotBeEmpty"));
        }
        else if(eventPeriodicityBox.getValue() == null){
            return new Warning(AppSettings.language().getItem("warning_eventPeriodicityCannotBeEmpty"));
        }
        else return null;
    }

    @Override
    public EventBlueprint waitFormResult() {
        return null; //TODO
    }

}
