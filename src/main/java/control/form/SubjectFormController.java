package control.form;

import app.AppSettings;
import app.MainApp;
import control.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misc.Duration;
import misc.Warning;
import misc.Weeks;
import model.*;
import scala.collection.JavaConverters;
import service.EventDatabase;
import service.ResourceDatabase;
import service.SubjectDatabase;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class SubjectFormController implements Initializable {

    private MainController mainController;

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

    public TableView<Long> eventTable; //table contains Event IDs, not Events itself
    public TableColumn<Long, String> eventTable_nameColumn;
    public TableColumn<Long, String> eventTable_resourceColumn;

    public Button deleteSelectedEventsButton;
    public Button deleteAllEventsButton;

    public Label warningTag;
    public Button createSubjectButton;

    //public TextField generateEvents_numberOfEvents;

    private SubjectDatabase subjectDatabase = MainApp.getDatabase().subjectDatabase();
    private EventDatabase eventDatabase = MainApp.getDatabase().eventDatabase();
    private ResourceDatabase resourceDatabase = MainApp.getDatabase().resourceDatabase();
    private Long subjectID = subjectDatabase.newSubject();
    private Subject subject = subjectDatabase.getElement(subjectID).get(); //since we just created this subject, this should be secure

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        setupViews();
        bindActions();
        initializeWarningSystem();
    }

    private void initializeContentLanguage() {
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

    private void setupViews() {
        generateEvents_eventTypeSelector.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(EventTypes.commonEventTypes())));

        generateEvents_durationSelector.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(Duration.getDurations().toBuffer())));
        generateEvents_durationSelector.setCellFactory(param -> new ListCell<>(){
                    @Override
                    protected void updateItem(Duration item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item == null || empty){
                            setGraphic(null);
                        }
                        else {
                            setText(item.toString());
                        }
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
        eventTable_nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(eventDatabase.getElement(cell.getValue()).get().getName()));
        //This should be secure because all table elements are valid IDs
        eventTable_resourceColumn.setCellValueFactory(cell -> new SimpleStringProperty(eventDatabase.getElement(cell.getValue()).get().getNeededResource().getName()));

        //Add all existing subject events to the table (in case of editing)
        eventTable.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(subject.getEventIDs().toBuffer())));
    }

    private void bindActions() {
        //This could be improved with property bindings and reduce overload
        subjectNameField.setOnKeyTyped(keyEvent -> {
            subject.setName(subjectNameField.getText());
            computeGenerationExample(
                    subject,
                    generateEvents_eventTypeSelector.getValue(),
                    generateEvents_weekSelector.getSelectionModel().getSelectedItem(),
                    1);
            keyEvent.consume();
        });
        subjectShortNameField.setOnKeyTyped(keyEvent -> {
            subject.setShortName(subjectShortNameField.getText());
            keyEvent.consume();
        });
        subjectDescriptionField.setOnKeyTyped(keyEvent -> {
            subject.setDescription(subjectDescriptionField.getText());
            keyEvent.consume();
        });
        subjectColorPicker.setOnAction(event -> {
            subject.setColor(subjectColorPicker.getValue());
            event.consume();
        });


        generateEvents_eventTypeSelector.setOnAction(event -> {
            computeGenerationExample(
                    subject,
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
                    subject,
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
                    subject,
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
            if(createSubject(subject)) closeWindow();
            event.consume();
        });
    }

    private boolean canGenerateExample(Subject subject, EventType eventType, Weeks.Week week) {
        return subject != null && eventType != null && week != null;
    }

    private void equalizeRangeValues() {
        generateEvents_rangeUpperBound.setText(generateEvents_rangeLowerBound.getText());
    }

    private void computeGenerationExample(Subject subject, EventType eventType, Weeks.Week week, int number) {
        if(canGenerateExample(subject, eventType, week)) {
            generationExampleLabel.setText(
                    String.format("%s (%s-%d) (%s)", subject.getName(), eventType.toString(), number, week.toShortString())
            );
        }
    }

    private void deleteSubjectEvents(Collection<Long> selectedItems) {
        for(Long eid : selectedItems){
            subject.removeEvent(eid);
            eventDatabase.removeElement(eid);
        }
        eventTable.getItems().removeAll(selectedItems);
    }

    //pre filter not null
    private void filterResourceList(String filter) {
        ObservableList<Resource> resources = FXCollections.observableArrayList(JavaConverters.asJavaCollection(resourceDatabase.getElements()));

        if(!filter.isBlank()) resources.removeIf(resource -> !resource.getName().toLowerCase().contains(filter.trim().toLowerCase()));

        selectResourceListView.setItems(resources);
    }

    private void closeWindow() {
        ((Stage)createSubjectButton.getScene().getWindow()).close();
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

    private void generateEvents(Subject subject, EventType eventType, int rangeStart, int rangeEnd,
                                Weeks.Week week, Duration duration, Resource neededResource) {
        if(!warnings(checkEventGenerationWarnings(eventType, rangeStart, rangeEnd, week, duration, neededResource))) {
            for(int i = rangeStart; i<=rangeEnd; i++){
                Long eventID = eventDatabase.newEvent();
                Event event = eventDatabase.getElement(eventID).get(); //this should be secure because we've just created the event in DB.

                event.setName(String.format("%s\n(%s-%d) (%s)", subject.getName(), eventType.toString(), i, week.toShortString()));
                event.setShortName(String.format("%s (%s %d) (%s)", subject.getShortName(), eventType.toShortString(), i, week.toShortString()));
                event.setEventType(eventType);
                event.setNeededResource(neededResource);
                event.setWeek(week);
                event.setDuration(duration.toInt());

                event.setSubject(subject);
                subject.addEvent(eventID, event);
                eventTable.getItems().add(eventID);
            }
        }
    }

    private boolean createSubject(Subject sub) {
        if(!warnings(checkSubjectCreationWarnings())) {
            sub.setAsFinished();
            for(Event e : JavaConverters.asJavaCollection(sub.getEvents())){
                mainController.addUnassignedEvent(e);
            }
        }
        else sub.setAsUnfinished();

        return sub.isFinished();
    }

    private boolean warnings(Warning warning) {
        if (warning != null) {
            popUpWarning(warning);
            return true;
        } else return false;
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

    private Warning checkSubjectCreationWarnings() {
        if(subject.getName().isBlank()){
            return new Warning(AppSettings.language().getItem("warning_subjectNameCannotBeEmpty"));
        }
        else if(subject.getShortName().isBlank()){
            return new Warning(AppSettings.language().getItem("warning_subjectShortNameCannotBeEmpty"));
        }
        return null;
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

    //public MainController getMainController(){ return mainController; }
    public void setMainController(MainController mc){
        mainController = mc;
    }
}
