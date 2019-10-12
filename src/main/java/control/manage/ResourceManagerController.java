package control.manage;

import app.AppSettings;
import app.MainApp;
import control.MainController;
import control.form.FormController;
import control.schedule.ResourceScheduleController;
import factory.DualWeekScheduleViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import service.ResourceDatabase;
import util.Utils;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class ResourceManagerController extends FormController {

    private ResourceDatabase resourceDatabase = MainApp.getDatabase().resourceDatabase();

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

    private List<Resource> resources = new ArrayList<>(JavaConverters.asJavaCollection(MainApp.getDatabase().resourceDatabase().getElements()));

    public ResourceManagerController(MainController mainController){
        super(mainController);
    }
    public ResourceManagerController(Stage stage, MainController mainController){
        super(stage, mainController);
    }

    @Override
    protected void initializeContentLanguage() {
        searchResourceField.setPromptText(AppSettings.language().getItem("manageResources_searchResourceField"));

        resourceTable.setPlaceholder(new Label(AppSettings.language().getItem("resourceTable_placeholder")));

        resourceTable_nameColumn.setText(AppSettings.language().getItem("manageResources_nameColumn"));
        resourceTable_quantityColumn.setText(AppSettings.language().getItem("manageResources_quantityColumn"));
        resourceTable_availabilityColumn.setText(AppSettings.language().getItem("manageResources_availabilityColumn"));

        addResourceButton.setText(AppSettings.language().getItem("manageResources_addButton"));
        deleteResourceButton.setText(AppSettings.language().getItem("manageResources_deleteButton"));

        resourceQuantityField.setPromptText(AppSettings.language().getItem("manageResources_quantityField"));
        minusOneButton.setText(AppSettings.language().getItem("manageResources_subButton"));
        plusOneButton.setText(AppSettings.language().getItem("manageResources_sumButton"));
    }

    @Override
    protected void setupViews() {
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

    @Override
    protected void bindActions() {
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

    private void manageResourceAvailability(Resource resource) {
        Utils.promptBoundWindow(
                AppSettings.language().getItem("manageResources_availabilityPrompt"),
                resourceTable.getScene().getWindow(),
                Modality.WINDOW_MODAL,
                new DualWeekScheduleViewFactory<>(new ResourceScheduleController(resource))
        ).show();
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
        if(!text.isBlank()) filteredResources.removeIf(resource -> !resource.getName().toLowerCase().contains(text));

        resourceTable.setItems(filteredResources);
    }

    private void addResource(){
        if(!warnings()){
            Resource r = resourceDatabase.createResource()._2;
            r.setName(resourceNameField.getText().trim());
            r.setQuantity(getQuantityFieldValue());

            updateCourseInTableView(r);
            clearInputFields();
        }
    }

    //post: return quantity field value if it is a number, Integer.MIN_VALUE otherwise
    private int getQuantityFieldValue(){
        try{
            int quantity;
            quantity = Integer.parseInt(resourceQuantityField.getText());
            return quantity;
        } catch (NumberFormatException nfe){
            return Integer.MIN_VALUE;
        }
    }

    private void clearInputFields() {
        resourceNameField.clear();
        resourceQuantityField.clear();
    }

    private void updateTableView(){
        resourceTable.refresh();
    }

    //pre: r not null
    private void updateCourseInTableView(Resource r) {
        if(resourceTable.getItems().contains(r)) updateTableView();
        else {
            resources.add(r);
            resourceTable.getItems().add(r);
        }
    }

    private void deleteResource(){
        ObservableList<Resource> selection = resourceTable.getSelectionModel().getSelectedItems();

        if(!warnings(resourcesCanBeDeleted(selection))){
            selection.forEach(resource -> MainApp.getDatabase().resourceDatabase().removeElement(resource));
            resources.removeAll(selection);
            resourceTable.getItems().removeAll(selection);
        }
    }

    @Override
    protected Warning checkWarnings(){
        return resourceCanBeCreated();
    }

    //pre: name and quantity not null
    private Warning resourceCanBeCreated() {
        Integer quantity = getQuantityFieldValue();
        if(resourceNameField.getText().trim().isBlank())
            return new Warning(AppSettings.language().getItem("warning_resourceNameCannotBeEmpty"));
        else if(quantity.equals(Integer.MIN_VALUE)){
            return new Warning(AppSettings.language().getItem("warning_resourceQuantityNaN"));
        }
        else if(quantity.compareTo(AppSettings.minQuantityPerResource()) < 0)  //if quantity is lower than minimum required.
            return new Warning(
                    quantity +
                    AppSettings.language().getItem("warning_resourceQuantityMin") +
                    " (" + AppSettings.minQuantityPerResource() + ").");
        else return null;
    }

    private Warning resourcesCanBeDeleted(ObservableList<Resource> selection) {
        if(selection.isEmpty()){
            return new Warning(AppSettings.language().getItem("warning_resourcesNotSelected"));
        }
        else return null;
    }
}
