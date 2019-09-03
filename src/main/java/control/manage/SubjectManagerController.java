package control.manage;

import app.AppSettings;
import app.MainApp;
import control.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.TextAlignment;
import model.Event;
import scala.collection.JavaConverters;
import service.SubjectDatabase;

import java.net.URL;
import java.util.ResourceBundle;

public class SubjectManagerController implements Initializable {

    private final MainController mainController;

    private SubjectDatabase subjectDatabase = MainApp.getDatabase().subjectDatabase();

    public TableView<Long> subjectTable; //we store Subject ids, not Subjects itself

    public TableColumn<Long, String> subjectTable_nameColumn;
    public TableColumn<Long, String> subjectTable_shortNameColumn;
    public TableColumn<Long, String> subjectTable_descriptionColumn;
    public TableColumn<Long, String> subjectTable_eventCountColumn;

    public Button addSubjectButton;
    public Button editSubjectButton;
    public Button removeSubjectButton;

    public SubjectManagerController(MainController mainController){
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        setupViews();
        bindActions();
    }

    private void initializeContentLanguage() {
        subjectTable.setPlaceholder(new Label(AppSettings.language().getItem("subjectTable_placeholder")));

        subjectTable_nameColumn.setText(AppSettings.language().getItem("subjectManager_nameColumnHeader"));
        subjectTable_shortNameColumn.setText(AppSettings.language().getItem("subjectManager_shortNameColumnHeader"));
        subjectTable_descriptionColumn.setText(AppSettings.language().getItem("subjectManager_descriptionColumnHeader"));
        subjectTable_eventCountColumn.setText(AppSettings.language().getItem("subjectManager_eventCountColumnHeader"));

        addSubjectButton.setText(AppSettings.language().getItem("subjectManager_addSubjectButton"));
        editSubjectButton.setText(AppSettings.language().getItem("subjectManager_editSubjectButton"));
        removeSubjectButton.setText(AppSettings.language().getItem("subjectManager_removeSubjectButton"));
    }

    private void setupColumn(TableColumn tc){
        Label label = new Label();
        label.setStyle("-fx-padding: 5px;");
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.prefWidthProperty().bind(tc.prefWidthProperty().subtract(5));
        tc.setGraphic(label);
    }

    private void setupViews() {
        setupColumn(subjectTable_nameColumn);
        setupColumn(subjectTable_shortNameColumn);
        setupColumn(subjectTable_descriptionColumn);
        setupColumn(subjectTable_eventCountColumn);

        //because we'll be using ids from the DB itself, it should be secure to use get() without checking
        subjectTable_nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(subjectDatabase.getElement(cell.getValue()).get().getName()));
        subjectTable_shortNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(subjectDatabase.getElement(cell.getValue()).get().getShortName()));
        subjectTable_descriptionColumn.setCellValueFactory(cell -> new SimpleStringProperty(subjectDatabase.getElement(cell.getValue()).get().getDescription()));
        subjectTable_eventCountColumn.setCellValueFactory(cell -> new SimpleStringProperty(subjectDatabase.getElement(cell.getValue()).get().getEventSummary()));

        //Maybe this behavior is not what client wants.
        subjectTable.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(subjectDatabase.getFinishedSubjectsIDs().toBuffer())));
    }

    private void bindActions() {
        removeSubjectButton.setOnAction(event -> removeSelectedSubject());
    }

    private void removeSelectedSubject() {
        Long sid = subjectTable.getSelectionModel().getSelectedItem();

        if(sid != null){
            subjectTable.getItems().remove(sid);
            for(Event e : JavaConverters.asJavaCollection(subjectDatabase.getElement(sid).get().getEvents()))
                mainController.removeEvent(e);
            subjectDatabase.removeSubject(sid);
        }
    }

}
