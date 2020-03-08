package control.form;

import app.AppSettings;
import app.FXMLPaths;
import app.MainApp;
import control.MainController;
import control.StageController;
import factory.ViewFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import misc.Duration;
import misc.EventTypeIncompatibility;
import misc.Warning;
import model.*;
import scala.collection.JavaConverters;
import service.CourseDatabase;
import service.EventDatabase;
import service.ResourceDatabase;
import service.SubjectDatabase;
import util.Utils;

import java.util.*;

public class SubjectFormController extends FormController {

    public Label subjectNameTag;
    public TextField subjectNameField;

    public Label subjectShortNameTag;
    public TextField subjectShortNameField;

    public Label subjectDescriptionTag;
    public TextArea subjectDescriptionField;

    public Label subjectColorTag;
    public Label subjectColorExplanation;
    public ColorPicker subjectColorPicker;

    public Label subjectCourseTag;
    public ComboBox<Course> subjectCoursePicker;

    public Label subjectQuarterTag;
    public ComboBox<Quarter> subjectQuarterPicker;

    public Label generateEventsTag;
    public ComboBox<EventType> generateEvents_eventTypeSelector;
    public Label generateEvents_rangeTag;
    public TextField generateEvents_rangeLowerBound;
    public TextField generateEvents_rangeUpperBound;
    public Button generateEvents_equalButton;
    public ComboBox<Weeks.Periodicity> generateEvents_periodicitySelector;
    public ComboBox<Duration> generateEvents_durationSelector;
    public Label generationExampleTag;
    public Label generationExampleLabel;

    public Label selectResourceTag;
    public TextField selectResourceSearchBar;
    public ListView<Resource> selectResourceListView;

    public Button generateEventsButton;

    public Button manageEventTypeIncompatibilitiesButton;

    public TableView<Event> eventTable; //table contains Event IDs, not Events itself
    public TableColumn<Event, String> eventTable_nameColumn;
    public TableColumn<Event, String> eventTable_resourceColumn;

    public Button deleteSelectedEventsButton;
    public Button deleteAllEventsButton;

    public Button createSubjectButton;

    private SubjectDatabase subjectDatabase = MainApp.getDatabase().subjectDatabase();
    private CourseDatabase courseDatabase = MainApp.getDatabase().courseDatabase();
    private EventDatabase eventDatabase = MainApp.getDatabase().eventDatabase();
    private ResourceDatabase resourceDatabase = MainApp.getDatabase().resourceDatabase();

    private Collection<EventTypeIncompatibility> eventTypeIncompatibilities = new HashSet<>();

    public SubjectFormController(MainController mainController){
        super(mainController);
    }
    public SubjectFormController(Stage stage, MainController mainController){
        super(stage, mainController);
    }

    @Override
    protected void initializeContentLanguage() {
        subjectNameTag.setText(AppSettings.language().getItem("subjectForm_subjectNameTag"));
        subjectNameField.setPromptText(AppSettings.language().getItem("subjectForm_subjectNameField"));

        subjectShortNameTag.setText(AppSettings.language().getItem("subjectForm_subjectShortNameTag"));
        subjectShortNameField.setPromptText(AppSettings.language().getItem("subjectForm_subjectShortNameField"));

        subjectDescriptionTag.setText(AppSettings.language().getItem("subjectForm_subjectDescriptionTag"));
        subjectDescriptionField.setPromptText(AppSettings.language().getItem("subjectForm_subjectDescriptionField"));

        subjectColorTag.setText(AppSettings.language().getItem("subjectForm_subjectColorTag"));
        subjectColorExplanation.setText(AppSettings.language().getItem("subjectForm_subjectColorExplanation"));

        subjectCourseTag.setText(AppSettings.language().getItem("subjectForm_subjectCourseTag"));
        subjectQuarterTag.setText(AppSettings.language().getItem("subjectForm_subjectQuarterTag"));

        generateEventsTag.setText(AppSettings.language().getItem("subjectForm_generateEventsTag"));

        generateEvents_eventTypeSelector.setPromptText(AppSettings.language().getItem("subjectForm_eventType"));
        generateEvents_rangeTag.setText(AppSettings.language().getItem("subjectForm_rangeTag"));
        generateEvents_rangeLowerBound.setPromptText(AppSettings.language().getItem("subjectForm_rangeLowerBound"));
        generateEvents_rangeUpperBound.setPromptText(AppSettings.language().getItem("subjectForm_rangeUpperBound"));

        generateEvents_periodicitySelector.setPromptText(AppSettings.language().getItem("subjectForm_eventPeriodicity"));
        generateEvents_durationSelector.setPromptText(AppSettings.language().getItem("subjectForm_eventDuration"));

        generationExampleTag.setText(AppSettings.language().getItem("subjectForm_generationExampleTag"));
        generationExampleLabel.setText("");

        selectResourceTag.setText(AppSettings.language().getItem("subjectForm_selectResourceTag"));
        selectResourceSearchBar.setPromptText(AppSettings.language().getItem("subjectForm_resourceSearchBar"));
        selectResourceListView.setPlaceholder(new Label(AppSettings.language().getItem("subjectForm_resourcePlaceholder")));

        generateEventsButton.setText(AppSettings.language().getItem("subjectForm_generateEventsButton"));

        manageEventTypeIncompatibilitiesButton.setText(AppSettings.language().getItem("subjectForm_manageEventTypeIncompatibilitiesButton"));

        eventTable.setPlaceholder(new Label(AppSettings.language().getItem("subjectForm_evenTablePlaceHolder")));
        eventTable_nameColumn.setText(AppSettings.language().getItem("subjectForm_eventTableNameColumn"));
        eventTable_resourceColumn.setText(AppSettings.language().getItem("subjectForm_eventTableResourceColumn"));

        deleteSelectedEventsButton.setText(AppSettings.language().getItem("subjectForm_deleteSelectedEventsButton"));
        deleteAllEventsButton.setText(AppSettings.language().getItem("subjectForm_deleteAllEventsButton"));

        createSubjectButton.setText(AppSettings.language().getItem("subjectForm_createSubjectButton"));
    }

    @Override
    protected void setupViews() {
        subjectCoursePicker.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(courseDatabase.getCourses())));
        subjectCoursePicker.getItems().add(0, NoCourse.noCourse());

        subjectQuarterPicker.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(Quarters.quarters())));
        subjectQuarterPicker.getItems().add(0, NoQuarter.noQuarter());

        generateEvents_eventTypeSelector.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(EventTypes.commonEventTypes())));

        generateEvents_durationSelector.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(Duration.getDurations().toBuffer())));
        generateEvents_durationSelector.setCellFactory(param -> new ListCell<Duration>(){
                    @Override
                    protected void updateItem(Duration item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty || item == null) setGraphic(null);
                        else setText(item.toString());
                    }
                }
        );

        generateEvents_periodicitySelector.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(Weeks.periodicityList())));

        selectResourceListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        selectResourceListView.setCellFactory(param -> new ListCell<Resource>() {
            @Override
            protected void updateItem(Resource resource, boolean empty) {
                super.updateItem(resource, empty);
                if (empty || resource == null) setText(null);
                else setText(resource.getName());
            }
        });
        selectResourceListView.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(resourceDatabase.getElements())));

        eventTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //This should be secure because all table elements are valid IDs
        eventTable_nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        //This should be secure because all table elements are valid IDs
        eventTable_resourceColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNeededResource().getName()));
    }

    @Override
    protected void bindActions() {
        subjectNameField.setOnKeyTyped(keyEvent -> {
            computeGenerationExample(
                    subjectNameField.getText(),
                    generateEvents_eventTypeSelector.getValue(),
                    generateEvents_periodicitySelector.getSelectionModel().getSelectedItem(),
                    1);
            keyEvent.consume();
        });

        generateEvents_eventTypeSelector.setOnAction(event -> {
            computeGenerationExample(
                    subjectNameField.getText(),
                    generateEvents_eventTypeSelector.getValue(),
                    generateEvents_periodicitySelector.getSelectionModel().getSelectedItem(),
                    1);
            event.consume();
        });
        generateEvents_equalButton.setOnAction(event -> {
            equalizeRangeValues();
            event.consume();
        });

        selectResourceSearchBar.setOnKeyTyped(keyEvent -> {
            filterResourceList(selectResourceSearchBar.getText());
            keyEvent.consume();
        });
        generateEventsButton.setOnAction(event -> {
            generateEvents(
                    subjectNameField.getText(),
                    subjectShortNameField.getText(),
                    generateEvents_eventTypeSelector.getValue(),
                    getRangeLowerBound(),
                    getRangeUpperBound(),
                    generateEvents_periodicitySelector.getSelectionModel().getSelectedItem(),
                    generateEvents_durationSelector.getSelectionModel().getSelectedItem(),
                    selectResourceListView.getSelectionModel().getSelectedItem()
            );
            event.consume();
        });

        manageEventTypeIncompatibilitiesButton.setOnAction(event -> {
            StageController incompatibilityFormController = new SubjectEventIncompatibilityFormController(eventTypeIncompatibilities);

            incompatibilityFormController.setStage(Utils.promptBoundWindow(
                    AppSettings.language().getItem("eventForm_manageIncompatibilities"),
                    manageEventTypeIncompatibilitiesButton.getScene().getWindow(),
                    Modality.WINDOW_MODAL,
                    new ViewFactory<>(FXMLPaths.SubjectIncompatibilityForm()),
                    incompatibilityFormController
            ));

            incompatibilityFormController.show();
        });

        deleteSelectedEventsButton.setOnAction(event -> {
            deleteSubjectEvents(eventTable.getSelectionModel().getSelectedItems());
            event.consume();
        });
        deleteAllEventsButton.setOnAction(event -> {
            deleteSubjectEvents(eventTable.getItems());
            event.consume();
        });

        createSubjectButton.setOnAction(event -> {
            if(createSubject()) close();
            event.consume();
        });
    }

    private boolean canGenerateExample(String subjectName, EventType eventType, Weeks.Periodicity periodicity) {
        return subjectName != null && eventType != null && periodicity != null;
    }

    private void equalizeRangeValues() {
        generateEvents_rangeUpperBound.setText(generateEvents_rangeLowerBound.getText());
    }

    private void computeGenerationExample(String subjectName, EventType eventType, Weeks.Periodicity periodicity, int number) {
        if(canGenerateExample(subjectName, eventType, periodicity)) {
            generationExampleLabel.setText(
                    String.format("%s (%s-%d) (%s)", subjectName, eventType.toString(), number, periodicity.toShortString())
            );
        }
    }

    private void deleteSubjectEvents(Collection<Event> selectedItems) {
        for(Event e : selectedItems){
            eventDatabase.deleteEvent(e);
        }
        eventTable.getItems().removeAll(selectedItems);
    }

    //pre filter not null
    private void filterResourceList(String filter) {
        ObservableList<Resource> resources = FXCollections.observableArrayList(JavaConverters.asJavaCollection(resourceDatabase.getElements()));

        if(!filter.trim().isEmpty()) resources.removeIf(resource -> !resource.getName().toLowerCase().contains(filter.trim().toLowerCase()));

        selectResourceListView.setItems(resources);
    }

    private int getNumberFromField(TextField textField){
        int number = 1;
        if(!textField.getText().trim().isEmpty()) {
            try {
                number = Integer.parseInt(textField.getText());
            } catch (NumberFormatException npe){
                number = -1;
            }
        } else number = -1;

        if (number < 1) {
            textField.setText(String.valueOf(1));
            return 1;
        }
        else return number;
    }

    private int getRangeLowerBound() {
        return getNumberFromField(generateEvents_rangeLowerBound);
    }

    private int getRangeUpperBound() {
        return getNumberFromField(generateEvents_rangeUpperBound);
    }

    private void generateEvents(String subjectName, String subjectShortName, EventType eventType, int rangeStart, int rangeEnd,
                                Weeks.Periodicity periodicity, Duration duration, Resource neededResource) {
        if(!warnings(checkEventGenerationWarnings(eventType, rangeStart, rangeEnd, periodicity, duration, neededResource))) {
            for(int i = rangeStart; i<=rangeEnd; i++){
                Event event = eventDatabase.createEvent()._2;

                //TODO abstract string pattern
                event.setName(String.format("%s (%s-%d) (%s)", subjectName, eventType.toString(), i, periodicity.toShortString()));
                event.setShortName(String.format("%s (%s %d) (%s)", subjectShortName, eventType.toShortString(), i, periodicity.toShortString()));
                event.setEventType(eventType);
                event.setNeededResource(neededResource);
                event.setPeriodicity(periodicity);
                event.setDuration(duration.toInt());

                eventTable.getItems().add(event);
            }
        }
    }

    private boolean createSubject() {
        if(!warnings()) {
            Subject subject = subjectDatabase.createSubject()._2;

            subject.setName(subjectNameField.getText());
            subject.setShortName(subjectShortNameField.getText());
            subject.setDescription(subjectDescriptionField.getText());
            subject.setColor(new Color(subjectColorPicker.getValue()));
            subject.setCourse(subjectCoursePicker.getValue());
            subject.setQuarter(subjectQuarterPicker.getValue());

            HashMap<EventType, List<Event>> eventsByType = new HashMap<>();
            for(EventType et: JavaConverters.asJavaCollection(EventTypes.commonEventTypes())){
                eventsByType.put(et, new ArrayList<>());
            }

            for(Event e : eventTable.getItems()){
                subject.addEvent(e.getID(), e);
                e.setSubject(subject);
                e.setCourse(subjectCoursePicker.getValue());
                eventsByType.get(e.getEventType()).add(e);
                getMainController().addUnassignedEvent(e);
            }

            for(EventTypeIncompatibility eti : eventTypeIncompatibilities){
                for(Event e1 : eventsByType.get(eti.getFirstType())){
                    for(Event e2: eventsByType.get(eti.getSecondType())){
                        e1.addIncompatibility(e2);
                    }
                }
            }

            subjectDatabase.setAsFinished(subject.getID());
            return true;
        }
        else return false;
    }

    @Override
    protected Warning checkWarnings(){
        return checkSubjectCreationWarnings();
    }

    private Warning checkSubjectCreationWarnings() {
        if(subjectNameField.getText().trim().isEmpty()){
            return new Warning(AppSettings.language().getItem("warning_subjectNameCannotBeEmpty"));
        }
        else if(subjectShortNameField.getText().trim().isEmpty()){
            return new Warning(AppSettings.language().getItem("warning_subjectShortNameCannotBeEmpty"));
        }
        return null;
    }

    private Warning checkEventGenerationWarnings(EventType eventType, int rangeStart, int rangeEnd,
                                                 Weeks.Periodicity periodicity, Duration duration, Resource neededResource) {
        if(neededResource == null){
            return new Warning(AppSettings.language().getItem("warning_resourcesNotSelected"));
        }
        else if (rangeStart > rangeEnd){
            return new Warning(AppSettings.language().getItem("warning_descendingRange"));
        }
        else if(eventType == null){
            return new Warning(AppSettings.language().getItem("warning_eventTypeNotSelected"));
        }
        else if(periodicity == null){
            return new Warning(AppSettings.language().getItem("warning_periodicityNotSelected"));
        }
        else if(duration == null){
            return new Warning(AppSettings.language().getItem("warning_durationNotSelected"));
        }
        else return checkSubjectCreationWarnings();
    }
}
