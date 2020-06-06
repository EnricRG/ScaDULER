package control.manage;

import app.AppSettings;
import app.MainApp;
import control.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import model.Event;
import model.Subject;
import scala.collection.JavaConverters;
import service.SubjectDatabase;

public class SubjectManagerController extends EntityManagerController<Subject> {

    private SubjectDatabase subjectDatabase = MainApp.getDatabase().subjectDatabase();

    public TableColumn<Subject, String> nameColumn = new TableColumn<>();
    public TableColumn<Subject, String> shortNameColumn = new TableColumn<>();
    public TableColumn<Subject, String> descriptionColumn = new TableColumn<>();
    public TableColumn<Subject, String> eventCountColumn = new TableColumn<>();

    public SubjectManagerController(MainController mainController){
        super(mainController);
    }
    public SubjectManagerController(Stage stage, MainController mainController){
        super(stage, mainController);
    }

    @Override
    protected void initializeContentLanguage() {
        table.setPlaceholder(new Label(AppSettings.language().getItem("subjectTable_placeholder")));

        nameColumn.setText(AppSettings.language().getItem("subjectManager_nameColumnHeader"));
        shortNameColumn.setText(AppSettings.language().getItem("subjectManager_shortNameColumnHeader"));
        descriptionColumn.setText(AppSettings.language().getItem("subjectManager_descriptionColumnHeader"));
        eventCountColumn.setText(AppSettings.language().getItem("subjectManager_eventCountColumnHeader"));

        addButton.setText(AppSettings.language().getItem("subjectManager_addSubjectButton"));
        editButton.setText(AppSettings.language().getItem("subjectManager_editSubjectButton"));
        removeButton.setText(AppSettings.language().getItem("subjectManager_removeSubjectButton"));
    }

    @Override
    protected void setupTable() {
        addColumns();
        configureColumns();
        fillTable(JavaConverters.asJavaCollection(subjectDatabase.getFinishedSubjects()));
    }

    private void addColumns(){
        addColumn(nameColumn);
        addColumn(shortNameColumn);
        addColumn(descriptionColumn);
        addColumn(eventCountColumn);
    }

    private void configureColumns(){
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().name()));
        shortNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().shortName()));
        descriptionColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().description()));
        //TODO replace eventSummary for more advanced cell value factory
        eventCountColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEventSummary()));
    }

    @Override
    protected void addButtonAction(ActionEvent e) {

    }

    @Override
    protected void editButtonAction(ActionEvent e) {

    }

    @Override
    protected void removeButtonAction(ActionEvent e) {
        Subject subject = table.getSelectionModel().getSelectedItem();

        if(subject != null){
            removeRow(subject);
            for(Event ev : JavaConverters.asJavaCollection(subject.events()))
                getMainController().removeEvent(ev);
            subjectDatabase.removeSubject(subject);
        }
    }
}
