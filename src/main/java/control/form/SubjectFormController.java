package control.form;

import app.AppSettings;
import app.MainApp;
import control.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misc.Duration;
import misc.Warning;
import model.*;
import scala.collection.JavaConverters;
import service.EventDatabase;
import service.ResourceDatabase;
import service.SubjectDatabase;

import java.util.Collection;

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

    public Label generateEventsTag;
    public ComboBox<EventType> generateEvents_eventTypeSelector;
    public Label generateEvents_rangeTag;
    public TextField generateEvents_rangeLowerBound;
    public TextField generateEvents_rangeUpperBound;
    public Button generateEvents_equalButton;
    public ComboBox<Weeks.Week> generateEvents_weekSelector;
    public ComboBox<Duration> generateEvents_durationSelector;
    public Label generationExampleTag;
    public Label generationExampleLabel;

    public Label selectResourceTag;
    public TextField selectResourceSearchBar;
    public ListView<Resource> selectResourceListView;

    public Button generateEventsButton;

    public TableView<Event> eventTable; //table contains Event IDs, not Events itself
    public TableColumn<Event, String> eventTable_nameColumn;
    public TableColumn<Event, String> eventTable_resourceColumn;

    public Button deleteSelectedEventsButton;
    public Button deleteAllEventsButton;

    public Button createSubjectButton;

    private SubjectDatabase subjectDatabase = MainApp.getDatabase().subjectDatabase();
    private EventDatabase eventDatabase = MainApp.getDatabase().eventDatabase();
    private ResourceDatabase resourceDatabase = MainApp.getDatabase().resourceDatabase();

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

        generateEventsTag.setText(AppSettings.language().getItem("subjectForm_generateEventsTag"));

        generateEvents_eventTypeSelector.setPromptText(AppSettings.language().getItem("subjectForm_eventType"));
        generateEvents_rangeTag.setText(AppSettings.language().getItem("subjectForm_rangeTag"));
        generateEvents_rangeLowerBound.setPromptText(AppSettings.language().getItem("subjectForm_rangeLowerBound"));
        generateEvents_rangeUpperBound.setPromptText(AppSettings.language().getItem("subjectForm_rangeUpperBound"));

        generateEvents_weekSelector.setPromptText(AppSettings.language().getItem("subjectForm_eventWeek"));
        generateEvents_durationSelector.setPromptText(AppSettings.language().getItem("subjectForm_eventDuration"));

        generationExampleTag.setText(AppSettings.language().getItem("subjectForm_generationExampleTag"));
        generationExampleLabel.setText("");

        selectResourceTag.setText(AppSettings.language().getItem("subjectForm_selectResourceTag"));
        selectResourceSearchBar.setPromptText(AppSettings.language().getItem("subjectForm_resourceSearchBar"));
        selectResourceListView.setPlaceholder(new Label(AppSettings.language().getItem("subjectForm_resourcePlaceholder")));

        generateEventsButton.setText(AppSettings.language().getItem("subjectForm_generateEventsButton"));

        eventTable.setPlaceholder(new Label(AppSettings.language().getItem("subjectForm_evenTablePlaceHolder")));
        eventTable_nameColumn.setText(AppSettings.language().getItem("subjectForm_eventTableNameColumn"));
        eventTable_resourceColumn.setText(AppSettings.language().getItem("subjectForm_eventTableResourceColumn"));

        deleteSelectedEventsButton.setText(AppSettings.language().getItem("subjectForm_deleteSelectedEventsButton"));
        deleteAllEventsButton.setText(AppSettings.language().getItem("subjectForm_deleteAllEventsButton"));

        createSubjectButton.setText(AppSettings.language().getItem("subjectForm_createSubjectButton"));
    }

    @Override
    protected void setupViews() {
        generateEvents_eventTypeSelector.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(EventTypes.commonEventTypes())));

        generateEvents_durationSelector.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(Duration.getDurations().toBuffer())));
        generateEvents_durationSelector.setCellFactory(param -> new ListCell<>(){
                    @Override
                    protected void updateItem(Duration item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty || item == null) setGraphic(null);
                        else setText(item.toString());
                    }
                }
        );

        generateEvents_weekSelector.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(Weeks.weekList())));

        selectResourceListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        selectResourceListView.setCellFactory(param -> new ListCell<>() {
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
                    generateEvents_weekSelector.getSelectionModel().getSelectedItem(),
                    1);
            keyEvent.consume();
        });

        generateEvents_eventTypeSelector.setOnAction(event -> {
            computeGenerationExample(
                    subjectNameField.getText(),
                    generateEvents_eventTypeSelector.getValue(),
                    generateEvents_weekSelector.getSelectionModel().getSelectedItem(),
                    1);
            event.consume();
        });
        generateEvents_equalButton.setOnAction(event -> {
            equalizeRangeValues();
            event.consume();
        });
        generateEvents_weekSelector.setOnAction(event -> {
            computeGenerationExample(
                    subjectNameField.getText(),
                    generateEvents_eventTypeSelector.getValue(),
                    generateEvents_weekSelector.getSelectionModel().getSelectedItem(),
                    1);
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
                    generateEvents_weekSelector.getSelectionModel().getSelectedItem(),
                    generateEvents_durationSelector.getSelectionModel().getSelectedItem(),
                    selectResourceListView.getSelectionModel().getSelectedItem()
            );
            event.consume();
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

    private boolean canGenerateExample(String subjectName, EventType eventType, Weeks.Week week) {
        return subjectName != null && eventType != null && week != null;
    }

    private void equalizeRangeValues() {
        generateEvents_rangeUpperBound.setText(generateEvents_rangeLowerBound.getText());
    }

    private void computeGenerationExample(String subjectName, EventType eventType, Weeks.Week week, int number) {
        if(canGenerateExample(subjectName, eventType, week)) {
            generationExampleLabel.setText(
                    String.format("%s (%s-%d) (%s)", subjectName, eventType.toString(), number, week.toShortString())
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

        if(!filter.isBlank()) resources.removeIf(resource -> !resource.getName().toLowerCase().contains(filter.trim().toLowerCase()));

        selectResourceListView.setItems(resources);
    }

    private int getNumberFromField(TextField textField){
        int number = 1;
        if(!textField.getText().isBlank()) {
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
                                Weeks.Week week, Duration duration, Resource neededResource) {
        if(!warnings(checkEventGenerationWarnings(eventType, rangeStart, rangeEnd, week, duration, neededResource))) {
            for(int i = rangeStart; i<=rangeEnd; i++){
                Event event = eventDatabase.createEvent()._2;

                event.setName(String.format("%s\n(%s-%d) (%s)", subjectName, eventType.toString(), i, week.toShortString()));
                event.setShortName(String.format("%s (%s %d) (%s)", subjectShortName, eventType.toShortString(), i, week.toShortString()));
                event.setEventType(eventType);
                event.setNeededResource(neededResource);
                event.setWeek(week);
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
            subject.setColor(subjectColorPicker.getValue());

            for(Event e : eventTable.getItems()){
                subject.addEvent(e.getID(), e);
                e.setSubject(subject);
                getMainController().addUnassignedEvent(e);
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
        if(subjectNameField.getText().isBlank()){
            return new Warning(AppSettings.language().getItem("warning_subjectNameCannotBeEmpty"));
        }
        else if(subjectShortNameField.getText().isBlank()){
            return new Warning(AppSettings.language().getItem("warning_subjectShortNameCannotBeEmpty"));
        }
        return null;
    }

    private Warning checkEventGenerationWarnings(EventType eventType, int rangeStart, int rangeEnd, Weeks.Week week, Duration duration, Resource neededResource) {
        if(neededResource == null){
            return new Warning(AppSettings.language().getItem("warning_resourcesNotSelected"));
        }
        else if (rangeStart > rangeEnd){
            return new Warning(AppSettings.language().getItem("warning_descendingRange"));
        }
        else if(eventType == null){
            return new Warning(AppSettings.language().getItem("warning_eventTypeNotSelected"));
        }
        else if(week == null){
            return new Warning(AppSettings.language().getItem("warning_weekNotSelected"));
        }
        else if(duration == null){
            return new Warning(AppSettings.language().getItem("warning_durationNotSelected"));
        }
        else return checkSubjectCreationWarnings();
    }
}
