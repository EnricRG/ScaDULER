package control.manage;

import app.AppSettings;
import app.MainApp;
import control.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import misc.Duration;
import model.Event;
import model.Resource;
import model.Subject;
import scala.collection.JavaConverters;
import service.EventDatabase;

public class EventManagerController extends EntityManagerController<Event> {

    private final EventDatabase eventDatabase = MainApp.getDatabase().eventDatabase();

    public TableColumn<Event, String> nameColumn = new TableColumn<>();
    public TableColumn<Event, String> shortNameColumn = new TableColumn<>();
    public TableColumn<Event, String> subjectColumn = new TableColumn<>();
    public TableColumn<Event, String> resourceColumn = new TableColumn<>();
    public TableColumn<Event, String> weekColumn = new TableColumn<>();
    public TableColumn<Event, String> durationColumn = new TableColumn<>();
    public TableColumn<Event, String> incompatibilitiesColumn = new TableColumn<>();

    public EventManagerController(MainController mainController){
        super(mainController);
    }
    public EventManagerController(Stage stage, MainController mainController){
        super(stage, mainController);
    }

    @Override
    protected void initializeContentLanguage() {
        table.setPlaceholder(new Label(AppSettings.language().getItem("eventListPlaceholder")));

        nameColumn.setText(AppSettings.language().getItem("eventManager_nameColumnHeader"));
        shortNameColumn.setText(AppSettings.language().getItem("eventManager_shortNameColumnHeader"));
        subjectColumn.setText(AppSettings.language().getItem("subjectManager_subjectColumnHeader"));
        resourceColumn.setText(AppSettings.language().getItem("subjectManager_resourceColumnHeader"));
        weekColumn.setText(AppSettings.language().getItem("subjectManager_weekColumnHeader"));
        durationColumn.setText(AppSettings.language().getItem("subjectManager_durationColumnHeader"));
        incompatibilitiesColumn.setText(AppSettings.language().getItem("subjectManager_incompatibilitiesColumnHeader"));

        addButton.setText(AppSettings.language().getItem("eventManager_addEventButton"));
        editButton.setText(AppSettings.language().getItem("eventManager_editEventButton"));
        removeButton.setText(AppSettings.language().getItem("eventManager_removeEventButton"));
    }

    @Override
    protected void setupTable() {
        addColumns();
        configureColumns();
        fillTable(JavaConverters.asJavaCollection(eventDatabase.getElements()));
    }

    private void addColumns(){
        addColumn(nameColumn);
        addColumn(shortNameColumn);
        addColumn(subjectColumn);
        addColumn(resourceColumn);
        addColumn(weekColumn);
        addColumn(durationColumn);
        addColumn(incompatibilitiesColumn);
    }

    private void configureColumns(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        shortNameColumn.setCellValueFactory(new PropertyValueFactory<>("shortName"));
        subjectColumn.setCellValueFactory(cell -> {
            Subject subject = cell.getValue().subject().getOrElse(null);
            if(subject != null) return new SimpleStringProperty(subject.getName());
            else return new SimpleStringProperty();
        });
        resourceColumn.setCellValueFactory(cell -> {
            Resource resource = cell.getValue().neededResource().getOrElse(null);
            if(resource != null) return new SimpleStringProperty(resource.name());
            else return new SimpleStringProperty();
        });
        weekColumn.setCellValueFactory(new PropertyValueFactory<>("week"));
        durationColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(Duration.asPrettyString(cell.getValue().duration()))
        );
        incompatibilitiesColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(String.valueOf(cell.getValue().incompatibilities().size()))
        );
    }

    @Override
    protected void addButtonAction(ActionEvent e) {

    }

    @Override
    protected void editButtonAction(ActionEvent e) {

    }

    @Override
    protected void removeButtonAction(ActionEvent e) {
        Event event = table.getSelectionModel().getSelectedItem();

        if(event != null){
            Subject subject = event.subject().getOrElse(null);
            if(subject != null){
                subject.removeEvent(event.getID());
            }

            for(Event ev : JavaConverters.asJavaCollection(event.incompatibilities())){
                ev.removeIncompatibility(event);
            }

            removeRow(event);
            getMainController().removeEvent(event);
            eventDatabase.removeElement(event);
        }
    }

}
