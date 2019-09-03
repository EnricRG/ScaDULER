package control.manage;

import app.AppSettings;
import app.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Event;
import scala.collection.JavaConverters;
import service.EventDatabase;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EventIncompatibilityManagerController implements Initializable {

    private final ArrayList<Event> incompatibilities;

    public Label assignedIncompatibilitiesTag;
    public TableView<Event> incompatibilityTable;
    public TableColumn<Event, String> incompatibilityTable_nameColumn;
    public Button selectAllAssigned;

    public Button addButton;
    public Button removeButton;

    public Label generalEventListTag;
    public TextField eventSearchBox;
    public TableView<Event> generalEventTable;
    public TableColumn<Event, String> generalEventTable_nameColumn;
    public Button selectAllUnassigned;

    private final EventDatabase eventDatabase = MainApp.getDatabase().eventDatabase();
    private final ArrayList<Event> allEvents = new ArrayList<>(JavaConverters.asJavaCollection(eventDatabase.getElements()));

    public EventIncompatibilityManagerController(ArrayList<Event> incompatibilities){
        this.incompatibilities = incompatibilities;
        allEvents.removeAll(incompatibilities);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        setupViews();
        bindActions();
    }

    private void initializeContentLanguage() {
        assignedIncompatibilitiesTag.setText(AppSettings.language().getItem("manageIncompat_assignedIncompat"));
        incompatibilityTable.setPlaceholder(new Label(AppSettings.language().getItem("manageIncompat_incompatTablePlaceholder")));
        incompatibilityTable_nameColumn.setText(AppSettings.language().getItem("manageIncompat_nameColumnHeader"));
        selectAllAssigned.setText(AppSettings.language().getItem("manageIncompat_selectAllIncompatibilities"));

        generalEventListTag.setText(AppSettings.language().getItem("manageIncompat_allEventsHeader"));
        eventSearchBox.setPromptText(AppSettings.language().getItem("manageIncompat_searchEvent"));
        generalEventTable.setPlaceholder(new Label(AppSettings.language().getItem("eventListPlaceholder")));
        generalEventTable_nameColumn.setText(AppSettings.language().getItem("manageIncompat_nameColumnHeader"));
        selectAllUnassigned.setText(AppSettings.language().getItem("manageIncompat_selectAllEvents"));
    }

    private void setupViews() {
        incompatibilityTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        incompatibilityTable_nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        generalEventTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        generalEventTable_nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        incompatibilityTable.getItems().addAll(incompatibilities);
        generalEventTable.getItems().addAll(allEvents);
    }

    //pre: text not null
    private void filterGeneralEventTable(String text) {
        ObservableList<Event> filteredResources = FXCollections.observableArrayList(allEvents);

        //if search field is not blank, remove all rows that event's name does not contain field's content as a substring
        if(!text.isBlank()) filteredResources.removeIf(event -> !event.getName().toLowerCase().contains(text));

        generalEventTable.setItems(filteredResources);
    }

    private void bindActions() {
        selectAllAssigned.setOnAction(event -> {
            incompatibilityTable.getSelectionModel().selectAll();
            event.consume();
        });
        selectAllUnassigned.setOnAction(event -> {
            generalEventTable.getSelectionModel().selectAll();
            event.consume();
        });
        eventSearchBox.setOnKeyTyped(event -> {
            filterGeneralEventTable(eventSearchBox.getText().trim().toLowerCase());
            event.consume();
        });
        addButton.setOnAction(event -> {
            addSelectedIncompatibilities();
            //generalEventTable.getSelectionModel().clearSelection();
            event.consume();
        });
        removeButton.setOnAction(event -> {
            removeSelectedIncompatibilities();
            //incompatibilityTable.getSelectionModel().clearSelection();
            event.consume();
        });
    }

    private void addSelectedIncompatibilities() {
        ObservableList<Event> selection = generalEventTable.getSelectionModel().getSelectedItems();

        //generalEventTable.getItems().removeAll(selection);
        allEvents.removeAll(selection);

        incompatibilityTable.getItems().addAll(selection);
        incompatibilities.addAll(selection);

        filterGeneralEventTable(eventSearchBox.getText().trim());
    }

    private void removeSelectedIncompatibilities() {
        ObservableList<Event> selection = incompatibilityTable.getSelectionModel().getSelectedItems();

        //generalEventTable.getItems().addAll(selection);
        allEvents.addAll(selection);

        incompatibilities.removeAll(selection);
        incompatibilityTable.getItems().removeAll(selection);

        filterGeneralEventTable(eventSearchBox.getText().trim());
    }
}
