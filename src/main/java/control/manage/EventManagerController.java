package control.manage;

import app.AppSettings;
import app.MainApp;
import control.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import misc.Duration;
import model.Event;
import model.Resource;
import model.Subject;
import model.Weeks;
import scala.Option;
import scala.collection.JavaConverters;
import service.EventDatabase;

public class EventManagerController extends EntityManagerController<Event> {

    private final EventDatabase eventDatabase = MainApp.getDatabase().eventDatabase();

    public TableColumn<Event, String> nameColumn = new TableColumn<>();
    public TableColumn<Event, String> shortNameColumn = new TableColumn<>();
    public TableColumn<Event, String> subjectColumn = new TableColumn<>();
    public TableColumn<Event, String> resourceColumn = new TableColumn<>();
    public TableColumn<Event, String> periodicityColumn = new TableColumn<>();
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
        periodicityColumn.setText(AppSettings.language().getItem("subjectManager_periodicityColumnHeader"));
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
        addColumn(periodicityColumn);
        addColumn(weekColumn);
        addColumn(durationColumn);
        addColumn(incompatibilitiesColumn);
    }

    private void configureColumns(){
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().name()));
        shortNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().shortName()));
        subjectColumn.setCellValueFactory(cell -> {
            Option<Subject> subject = cell.getValue().subject();
            if(subject.nonEmpty()) return new SimpleStringProperty(subject.get().name());
            else return new SimpleStringProperty();
        });
        resourceColumn.setCellValueFactory(cell -> {
            Option<Resource> resource = cell.getValue().neededResource();
            if(resource.nonEmpty()) return new SimpleStringProperty(resource.get().name());
            else return new SimpleStringProperty();
        });
        periodicityColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().periodicity().toString()));
        weekColumn.setCellValueFactory(cell -> {
            Option<Weeks.Week> week = cell.getValue().week();
            if(week.nonEmpty()) return new SimpleStringProperty(week.get().toString());
            else return new SimpleStringProperty();
        });
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
                subject.events_$minus$eq(event);
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
