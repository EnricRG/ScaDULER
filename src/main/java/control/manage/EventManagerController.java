package control.manage;

import app.AppSettings;
import app.MainApp;
import control.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import misc.Duration;
import model.Event;
import model.Resource;
import model.Subject;
import scala.collection.JavaConverters;
import service.EventDatabase;
import service.SubjectDatabase;

import java.net.URL;
import java.util.ResourceBundle;

public class EventManagerController implements Initializable {

    private final MainController mainController;

    private final EventDatabase eventDatabase = MainApp.getDatabase().eventDatabase();
    private final SubjectDatabase subjectDatabase = MainApp.getDatabase().subjectDatabase();

    public TableView<Event> eventTable;

    public TableColumn<Event, String> eventTable_nameColumn;
    public TableColumn<Event, String> eventTable_shortNameColumn;
    public TableColumn<Event, String> eventTable_subjectColumn;
    public TableColumn<Event, String> eventTable_resourceColumn;
    public TableColumn<Event, String> eventTable_weekColumn;
    public TableColumn<Event, String> eventTable_durationColumn;
    public TableColumn<Event, String> eventTable_incompatibilitiesColumn;

    public Button addEventButton;
    public Button editEventButton;
    public Button removeEventButton;

    public EventManagerController(MainController mainController){
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        setupViews();
        bindActions();
    }

    private void initializeContentLanguage() {
        eventTable.setPlaceholder(new Label(AppSettings.language().getItem("eventListPlaceholder")));

        eventTable_nameColumn.setText(AppSettings.language().getItem("eventManager_nameColumnHeader"));
        eventTable_shortNameColumn.setText(AppSettings.language().getItem("eventManager_shortNameColumnHeader"));
        eventTable_subjectColumn.setText(AppSettings.language().getItem("subjectManager_subjectColumnHeader"));
        eventTable_resourceColumn.setText(AppSettings.language().getItem("subjectManager_resourceColumnHeader"));
        eventTable_weekColumn.setText(AppSettings.language().getItem("subjectManager_weekColumnHeader"));
        eventTable_durationColumn.setText(AppSettings.language().getItem("subjectManager_durationColumnHeader"));
        eventTable_incompatibilitiesColumn.setText(AppSettings.language().getItem("subjectManager_incompatibilitiesColumnHeader"));

        addEventButton.setText(AppSettings.language().getItem("eventManager_addEventButton"));
        editEventButton.setText(AppSettings.language().getItem("eventManager_editEventButton"));
        removeEventButton.setText(AppSettings.language().getItem("eventManager_removeEventButton"));
    }

    private void setupViews() {
        //because we'll be using ids from the DB itself, it should be secure to use get() without checking
        eventTable_nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eventTable_shortNameColumn.setCellValueFactory(new PropertyValueFactory<>("shortName"));
        eventTable_subjectColumn.setCellValueFactory(cell -> {
            Subject subject = cell.getValue().getSafeSubject();
            if(subject != null) return new SimpleStringProperty(subject.getName());
            else return new SimpleStringProperty();
        });
        eventTable_resourceColumn.setCellValueFactory(cell -> {
            Resource resource = cell.getValue().getSafeNeededResource();
            if(resource != null) return new SimpleStringProperty(resource.getName());
            else return new SimpleStringProperty();
        });
        eventTable_weekColumn.setCellValueFactory(new PropertyValueFactory<>("week"));
        eventTable_durationColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(Duration.asPrettyString(cell.getValue().getDuration()))
        );
        eventTable_incompatibilitiesColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(String.valueOf(cell.getValue().getIncompatibilities().size()))
        );

        eventTable.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(eventDatabase.getElements())));
    }

    private void bindActions() {
        removeEventButton.setOnAction(event -> removeSelectedEvent());
    }

    private void removeSelectedEvent() {
        Event event = eventTable.getSelectionModel().getSelectedItem();

        if(event != null){

            Subject subject = event.getSafeSubject();
            if(subject != null){
                subject.removeEvent(event.getID());
            }

            for(Event e : JavaConverters.asJavaCollection(event.getIncompatibilities())){
                e.removeIncompatibility(event);
            }

            eventTable.getItems().remove(event);

            mainController.removeEvent(event);

            eventDatabase.removeElement(event);
        }
    }

}
