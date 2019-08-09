package control;

import app.AppSettings;
import app.MainApp;
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

    public TabPane tabPane;

    public Tab firstQuarterTab;
    public TableView<CourseResource> firstQuarter_resourceTable;
    public TableColumn<CourseResource, String> firstQuarter_nameColumn;
    public TableColumn<CourseResource, Integer> firstQuarter_quantityColumn;

    public Tab secondQuarterTab;
    public TableView<CourseResource> secondQuarter_resourceTable;
    public TableColumn<CourseResource, String> secondQuarter_nameColumn;
    public TableColumn<CourseResource, Integer> secondQuarter_quantityColumn;

    public Button addButton;
    public TextField quantityField;
    public Button removeButton;

    public TextField resourceSearchBox;

    public TableView<Resource> generalResourceTable;
    public TableColumn<Resource, String> generalResourceTable_nameColumn;
    public TableColumn<Resource, Integer> generalResourceTable_quantityColumn;

    public Button manageGlobalResources;

    private List<CourseResource> firstQuarterResources;
    private List<CourseResource> secondQuarterResources;
    private List<Resource> allResources =
            new ArrayList<>(
                    JavaConverters.asJavaCollection(
                            MainApp.database().resourceDatabase().getElements())
            );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        setupTableViews();
        bindActions();
    }

    private void setupTableViews() {
        firstQuarter_resourceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        firstQuarter_nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        firstQuarter_quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        secondQuarter_resourceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        secondQuarter_nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        secondQuarter_quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        generalResourceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        generalResourceTable_nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        generalResourceTable_quantityColumn.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));

        generalResourceTable.getItems().addAll(allResources);
    }

    //this arrays will be used as modifiable containers to store new selected resources.
    public void linkResources(List<CourseResource> fqr, List<CourseResource> sqr){
        firstQuarterResources = fqr;
        secondQuarterResources = sqr;
    }

    private void initializeContentLanguage() {
        firstQuarterTab.setText(AppSettings.language().getItem("manageCourseResources_firstQuarter"));
        firstQuarter_resourceTable.setPlaceholder(new Label(AppSettings.language().getItem("resourceTable_placeholder")));
        firstQuarter_nameColumn.setText(AppSettings.language().getItem("manageResources_nameColumn"));
        firstQuarter_quantityColumn.setText(AppSettings.language().getItem("manageResources_quantityColumn"));

        secondQuarterTab.setText(AppSettings.language().getItem("manageCourseResources_secondQuarter"));
        secondQuarter_resourceTable.setPlaceholder(new Label(AppSettings.language().getItem("resourceTable_placeholder")));
        secondQuarter_nameColumn.setText(AppSettings.language().getItem("manageResources_nameColumn"));
        secondQuarter_quantityColumn.setText(AppSettings.language().getItem("manageResources_quantityColumn"));

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
            addSelectedEvents();
            generalResourceTable.getSelectionModel().clearSelection();
            event.consume();
        });
        removeButton.setOnAction(event -> {
            removeSelectedEvents();
            firstQuarter_resourceTable.getSelectionModel().clearSelection();
            secondQuarter_resourceTable.getSelectionModel().clearSelection();
            event.consume();
        });

        generalResourceTable.getSelectionModel().selectedItemProperty().addListener((
                (observable, oldValue, newValue) -> tableSelectionListener(generalResourceTable, newValue)));
        firstQuarter_resourceTable.getSelectionModel().selectedItemProperty().addListener((
                (observable, oldValue, newValue) -> tableSelectionListener(firstQuarter_resourceTable, newValue)));
        secondQuarter_resourceTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tableSelectionListener(secondQuarter_resourceTable, newValue));
    }

    private <E extends QuantifiableResource> void tableSelectionListener(TableView<E> tableView, E lastSelection) {
        if(lastSelection != null && !tableView.getSelectionModel().getSelectedItems().isEmpty()){
            ObservableList<E> selection = tableView.getSelectionModel().getSelectedItems();
            Integer min = selection.get(0).getAvailableQuantity();
            for(int i = 1; i < selection.size(); i++) {
                Integer act = selection.get(i).getAvailableQuantity();
                min = min > act ? act : min;
            }
            //FIXME: this will surely need a fix in the future
            updateQuantityField(min);
        }
    }

    private void updateQuantityField(Integer min) {
        quantityField.setText(String.valueOf(min));
    }

    private void addSelectedEvents() {
        ObservableList<Resource> selection = generalResourceTable.getSelectionModel().getSelectedItems();

        TableView<CourseResource> tableView = tabPane.getSelectionModel().getSelectedItem() == firstQuarterTab ? firstQuarter_resourceTable : secondQuarter_resourceTable;
        List<CourseResource> target = tableView == firstQuarter_resourceTable ? firstQuarterResources : secondQuarterResources;

        final Integer userInputQuantity = getQuantityFieldValue();

        selection.forEach(resource -> {
            Integer maxAvailableQuantity = userInputQuantity <= resource.getAvailableQuantity() ? userInputQuantity : resource.getAvailableQuantity();
            if(maxAvailableQuantity > 0) {
                CourseResource cr;
                Integer crIndex = getIndexWithName(target, resource.getName());

                if (maxAvailableQuantity < userInputQuantity)
                    quantityField.setText(String.valueOf(maxAvailableQuantity));

                if (crIndex < 0) {
                    cr = new CourseResource(resource, maxAvailableQuantity);
                    resource.linkCourseResource(cr);
                    target.add(cr);
                    tableView.getItems().add(cr);
                } else {
                    cr = target.get(crIndex);
                    cr.incrementQuantity(maxAvailableQuantity);
                }
            }
        });

        refreshTables();
    }

    //pre target and string not null
    private Integer getIndexWithName(List<CourseResource> target, String name) {
        if(target.isEmpty()) return -1;

        Integer i = 0;
        boolean found = false;
        while(!found && i<target.size()) {
            if (target.get(i).getName().equals(name)) found = true;
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

    private void removeSelectedEvents() {
        TableView<CourseResource> tableView = tabPane.getSelectionModel().getSelectedItem() == firstQuarterTab ? firstQuarter_resourceTable : secondQuarter_resourceTable;

        ObservableList<CourseResource> selection = tableView.getSelectionModel().getSelectedItems();
        List<CourseResource> source = tableView == firstQuarter_resourceTable ? firstQuarterResources : secondQuarterResources;

        final Integer userInputQuantity = getQuantityFieldValue();

        List<CourseResource> removableCourseResources = new ArrayList<>();

        selection.forEach(courseResource -> {

            Integer maxAvailableQuantity = courseResource.getQuantity()<userInputQuantity ? courseResource.getQuantity() : userInputQuantity;
            courseResource.decrementQuantity(maxAvailableQuantity);

            if(courseResource.getQuantity() <= 0) {
                removableCourseResources.add(courseResource);
                courseResource.getResource().unlinkCourseResource(courseResource);
            }

        });

        source.removeAll(removableCourseResources);
        tableView.getItems().removeAll(removableCourseResources);

        refreshTables();
    }

    private void refreshTables() {
        generalResourceTable.refresh();
        firstQuarter_resourceTable.refresh();
        secondQuarter_resourceTable.refresh();
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
        allResources = new ArrayList<>(JavaConverters.asJavaCollection(MainApp.database().resourceDatabase().getElements()));
        filterResourceTable(resourceSearchBox.getText().trim().toLowerCase());
    }
}
