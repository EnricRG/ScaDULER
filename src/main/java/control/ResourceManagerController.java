package control;

import app.AppSettings;
import app.MainApp;
import factory.DualWeekScheduleViewFactory;
import factory.ScheduleViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import misc.Warning;
import model.Resource;
import scala.collection.JavaConverters;
import util.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class ResourceManagerController implements Initializable {

    public TextField searchResourceField;

    public TableView<Resource> resourceTable;
    public TableColumn<Resource, String> resourceTable_nameColumn;
    public TableColumn<Resource, Integer> resourceTable_quantityColumn;
    public TableColumn<Resource, Void> resourceTable_availabilityColumn;

    public Button addResourceButton;
    public TextField resourceNameField;
    public TextField resourceQuantityField;

    public Button deleteResourceButton;
    public Button minusOneButton;
    public Button plusOneButton;

    public Label warningTag;

    private List<Resource> resources = new ArrayList<>(JavaConverters.asJavaCollection(MainApp.database().resourceDatabase().getElements()));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        setupTableView();
        initializeWarningSystem();
        bindActions();
    }

    private void initializeContentLanguage() {
        searchResourceField.setPromptText(AppSettings.language().getItem("manageResources_searchResourceField"));

        resourceTable_nameColumn.setText(AppSettings.language().getItem("manageResources_nameColumn"));
        resourceTable_quantityColumn.setText(AppSettings.language().getItem("manageResources_quantityColumn"));
        resourceTable_availabilityColumn.setText(AppSettings.language().getItem("manageResources_availabilityColumn"));

        addResourceButton.setText(AppSettings.language().getItem("manageResources_addButton"));
        deleteResourceButton.setText(AppSettings.language().getItem("manageResources_deleteButton"));

        resourceQuantityField.setPromptText(AppSettings.language().getItem("manageResources_quantityField"));
        minusOneButton.setText(AppSettings.language().getItem("manageResources_subButton"));
        plusOneButton.setText(AppSettings.language().getItem("manageResources_sumButton"));

        resourceTable.setPlaceholder(new Label(AppSettings.language().getItem("resourceTable_placeholder")));
    }

    private Node generateResourceManageButton(TableCell<Resource, Void> c){
        HBox hbox = new HBox();
        Button button = new Button(AppSettings.language().getItem("manage"));
        hbox.setAlignment(Pos.CENTER);
        hbox.setMaxWidth(USE_COMPUTED_SIZE);
        hbox.getChildren().add(button);
        HBox.setHgrow(button, Priority.ALWAYS);
        button.setPadding(new Insets(1));
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);
        button.setOnAction(event -> manageResourceAvailability(c.getTableRow().getItem()));
        return hbox;
    }

    private void setupTableView() {
        resourceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        resourceTable_nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        resourceTable_quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        resourceTable_availabilityColumn.setCellFactory(param ->  new TableCell<>(){


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if(!empty){
                    this.setGraphic(generateResourceManageButton(this));
                }
                else{
                    setGraphic(null);
                    setText(null);
                }
            }
        });

        resourceTable.getItems().addAll(resources);
    }

    private void manageResourceAvailability(Resource resource) {
        Stage manager = Utils.promptBoundWindow(
                AppSettings.language().getItem("manageResources_availabilityPrompt"),
                resourceTable.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new DualWeekScheduleViewFactory<>(new ResourceScheduleController(resource))
        );

        manager.show();
    }

    private void initializeWarningSystem() {
        hideWarnings();
        warningTag.setText("");
    }

    private void bindActions() {
        searchResourceField.setOnKeyTyped(event -> {
            filterResourceTable(searchResourceField.getText().trim().toLowerCase());
            event.consume();
        });
        addResourceButton.setOnAction(event -> {
            addResource();
            event.consume();
        });
        deleteResourceButton.setOnAction(event -> {
            deleteResource();
            event.consume();
        });
        minusOneButton.setOnAction(event -> {
            decrementByOne();
            event.consume();
        });
        plusOneButton.setOnAction(event -> {
            incrementByOne();
            event.consume();
        });
    }

    private void incrementByOne() {
        ObservableList<Resource> selection = resourceTable.getSelectionModel().getSelectedItems();
        selection.forEach(resource -> resource.incrementQuantity(1));
        updateTableView();
    }

    private void decrementByOne() {
        ObservableList<Resource> selection = resourceTable.getSelectionModel().getSelectedItems();
        selection.forEach(resource -> resource.decrementQuantity(1));
        updateTableView();
    }

    //pre: text not null
    private void filterResourceTable(String text) {
        ObservableList<Resource> filteredResources = FXCollections.observableArrayList(resources);

        //if search field is not blank, remove all rows that resource's name does not contain fields content as a substring
        if(!text.isBlank()) filteredResources.removeIf(resource -> !resource.name().toLowerCase().contains(text));

        resourceTable.setItems(filteredResources);
    }

    private void addResource() {
        Warning warning = null;
        String name = resourceNameField.getText().trim();
        Integer quantity = Integer.MIN_VALUE;
        try{
            quantity = Integer.parseInt(resourceQuantityField.getText());
        } catch (NumberFormatException nfe){
            warning = new Warning(AppSettings.language().getItem("warning_resourceQuantityNaN"));
        }

        //if Integer parsing went ok, check if input is correctly formatted.
        warning = (warning == null) ? resourceCanBeCreated(name, quantity) : warning;

        if (warning == null) { //if no errors
            hideWarnings(); //no warnings to be shown
            updateCourseInTableView(
                    MainApp.database().resourceDatabase().createResource(name, quantity)
            );
            clearInputFields();
        }
        else popUpWarning(warning);
    }

    private void clearInputFields() {
        resourceNameField.clear();
        resourceQuantityField.clear();
    }

    private void updateTableView(){
        resourceTable.refresh();
    }

    //pre: cr not null
    private void updateCourseInTableView(Resource cr) {
        if(resourceTable.getItems().contains(cr)) updateTableView();
        else {
            resources.add(cr);
            resourceTable.getItems().add(cr);
        }
    }

    private void deleteResource() {
        Warning warning = null;

        ObservableList<Resource> selection = resourceTable.getSelectionModel().getSelectedItems();

        warning = resourcesCanBeDeleted(selection);

        if (warning == null) { //if no errors
            hideWarnings();
            //this order is mandatory, if changed resources will not update properly.
            selection.forEach(resource -> MainApp.database().resourceDatabase().removeElement(resource));
            resources.removeAll(selection);
            resourceTable.getItems().removeAll(selection);
        }
        else popUpWarning(warning);
    }

    private Warning resourcesCanBeDeleted(ObservableList<Resource> selection) {
        if(selection.isEmpty()){
            return new Warning(AppSettings.language().getItem("warning_resourcesNotSelected"));
        }
        else return null;
    }

    //pre: name and quantity not null
    private Warning resourceCanBeCreated(String name, Integer quantity) {
        if(name.isBlank())
            return new Warning(AppSettings.language().getItem("warning_resourceNameCannotBeEmpty"));
        else if(quantity.compareTo(AppSettings.minQuantityPerResource()) < 0)  //if quantity is lower than minimum required.
            return new Warning(
                    quantity +
                    AppSettings.language().getItem("warning_resourceQuantityMin") +
                    " (" + AppSettings.minQuantityPerResource() + ").");
        else return null;
    }

    private void hideWarnings(){ warningTag.setVisible(false); }
    private void showWarnings(){ warningTag.setVisible(true); }

    private void popUpWarning(Warning warning) {
        warningTag.setText(warning.toString());
        showWarnings();
    }


}
