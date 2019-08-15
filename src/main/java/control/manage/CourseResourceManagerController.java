package control.manage;

import app.AppSettings;
import app.MainApp;
import control.form.CourseFormController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.CourseResource;
import model.QuantifiableResource;
import model.Resource;
import scala.collection.JavaConverters;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CourseResourceManagerController implements Initializable {


    private CourseFormController courseFormController;

    public TableView<CourseResource> courseResourceTable;
    public TableColumn<CourseResource, String> courseResourceTable_nameColumn;
    public TableColumn<CourseResource, String> courseResourceTable_quantityColumn;

    public Button addButton;
    public TextField quantityField;
    public Button removeButton;

    public TextField resourceSearchBox;

    public TableView<Resource> generalResourceTable;
    public TableColumn<Resource, String> generalResourceTable_nameColumn;
    public TableColumn<Resource, Integer> generalResourceTable_quantityColumn;

    public Button manageGlobalResources;

    private List<CourseResource> courseResources = new ArrayList<>();
    private List<Resource> allResources =
            new ArrayList<>(
                    JavaConverters.asJavaCollection(
                            MainApp.getDatabase().resourceDatabase().getElements())
            );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        setupTableViews();
        bindActions();
    }

    private void setupTableViews() {
        courseResourceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        courseResourceTable_nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        courseResourceTable_quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        generalResourceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        generalResourceTable_nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        generalResourceTable_quantityColumn.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));

        generalResourceTable.getItems().addAll(allResources);
    }

    //this arrays will be used as modifiable containers to store new selected resources.
    public void linkResources(List<CourseResource> courseResources){
        this.courseResources = courseResources;
        courseResourceTable.setItems(FXCollections.observableArrayList(courseResources));
    }

    private void initializeContentLanguage() {
        courseResourceTable.setPlaceholder(new Label(AppSettings.language().getItem("resourceTable_placeholder")));
        courseResourceTable_nameColumn.setText(AppSettings.language().getItem("manageResources_nameColumn"));
        courseResourceTable_quantityColumn.setText(AppSettings.language().getItem("manageResources_quantityColumn"));

        resourceSearchBox.setPromptText(AppSettings.language().getItem("manageResources_searchResourceField"));

        generalResourceTable.setPlaceholder(new Label(AppSettings.language().getItem("resourceTable_placeholder")));
        generalResourceTable_nameColumn.setText(AppSettings.language().getItem("manageResources_nameColumn"));
        generalResourceTable_quantityColumn.setText(AppSettings.language().getItem("manageResources_availableQuantityColumn"));

        manageGlobalResources.setText(AppSettings.language().getItem("manageCourseResources_manageGlobalResources"));
    }

    private void bindActions() {
        manageGlobalResources.setOnAction(event -> {
            courseFormController.getMainController().promptResourceManager(manageGlobalResources.getScene().getWindow(), this);
            event.consume();
        });
        resourceSearchBox.setOnKeyTyped(event -> {
            filterResourceTable(resourceSearchBox.getText().trim().toLowerCase());
            event.consume();
        });
        addButton.setOnAction(event -> {
            addSelectedResources();
            generalResourceTable.getSelectionModel().clearSelection();
            event.consume();
        });
        removeButton.setOnAction(event -> {
            removeSelectedResources();
            courseResourceTable.getSelectionModel().clearSelection();
            event.consume();
        });

        generalResourceTable.getSelectionModel().selectedItemProperty().addListener((
                (observable, oldValue, newValue) -> tableSelectionListener(generalResourceTable, newValue)));
        courseResourceTable.getSelectionModel().selectedItemProperty().addListener((
                (observable, oldValue, newValue) -> tableSelectionListener(courseResourceTable, newValue)));
    }

    private <E extends QuantifiableResource> void tableSelectionListener(TableView<E> tableView, E lastSelection) {
        if(lastSelection != null && !tableView.getSelectionModel().getSelectedItems().isEmpty()){
            ObservableList<E> selection = tableView.getSelectionModel().getSelectedItems();
            Integer min = selection.get(0).getAvailableQuantity();
            for(int i = 1; i < selection.size(); i++) {
                Integer act = selection.get(i).getAvailableQuantity();
                min = min > act ? act : min;
            }
            updateQuantityField(min);
        }
    }

    private void updateQuantityField(Integer min) {
        quantityField.setText(String.valueOf(min));
    }

    private void addSelectedResources() {
        ObservableList<Resource> selection = generalResourceTable.getSelectionModel().getSelectedItems();

        final Integer userInputQuantity = getQuantityFieldValue();

        selection.forEach(resource -> {
            //TODO change availableQuantity for quantity
            Integer maxAvailableQuantity = userInputQuantity <= resource.getAvailableQuantity() ? userInputQuantity : resource.getAvailableQuantity();
            if(maxAvailableQuantity > 0) {
                CourseResource cr;
                Integer crIndex = getIndexWithResource(courseResources, resource);

                if (maxAvailableQuantity < userInputQuantity)
                    quantityField.setText(String.valueOf(maxAvailableQuantity));

                if (crIndex < 0) {
                    cr = new CourseResource(resource, maxAvailableQuantity);
                    resource.linkCourseResource(cr);
                    courseResources.add(cr);
                    courseResourceTable.getItems().add(cr);
                } else {
                    cr = courseResources.get(crIndex);
                    cr.incrementQuantity(maxAvailableQuantity);
                }
            }
        });

        refreshTables();
    }

    //pre target and string not null
    private Integer getIndexWithResource(List<CourseResource> target, Resource resource) {
        if(target.isEmpty()) return -1;

        Integer i = 0;
        boolean found = false;
        while(!found && i<target.size()) {
            if (target.get(i).getResource() == resource) found = true;
            else i++;
        }
        return found? i : -1;
    }

    private Integer getQuantityFieldValue() {
        Integer quantity;
        try{
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException nfe){
            quantity = -1;
        }

        if(quantity < 1) {
            quantity = 1;
            quantityField.setText(String.valueOf(quantity));
        }

        return quantity;
    }

    private void removeSelectedResources() {
        ObservableList<CourseResource> selection = courseResourceTable.getSelectionModel().getSelectedItems();

        final Integer userInputQuantity = getQuantityFieldValue();

        List<CourseResource> removableCourseResources = new ArrayList<>();

        selection.forEach(courseResource -> {

            Integer maxAvailableQuantity = courseResource.getQuantity() < userInputQuantity ? courseResource.getQuantity() : userInputQuantity;
            courseResource.decrementQuantity(maxAvailableQuantity);

            if(courseResource.getQuantity() <= 0) {
                removableCourseResources.add(courseResource);
                courseResource.getResource().unlinkCourseResource(courseResource);
            }

        });

        courseResources.removeAll(removableCourseResources);
        courseResourceTable.getItems().removeAll(removableCourseResources);

        refreshTables();
    }

    private void refreshTables() {
        generalResourceTable.refresh();
        courseResourceTable.refresh();
    }

    public void setCourseController(CourseFormController cfc){
        courseFormController = cfc;
    }

    //pre: text not null
    private void filterResourceTable(String text) {
        ObservableList<Resource> filteredResources = FXCollections.observableArrayList(allResources);

        //if search field is not blank, remove all rows that resource's name does not contain fields content as a substring
        if(!text.isBlank()) filteredResources.removeIf(resource -> !resource.name().toLowerCase().contains(text));

        generalResourceTable.setItems(filteredResources);
    }

    public void updateResources() {
        allResources = new ArrayList<>(JavaConverters.asJavaCollection(MainApp.getDatabase().resourceDatabase().getElements()));
        filterResourceTable(resourceSearchBox.getText().trim().toLowerCase());
    }
}
