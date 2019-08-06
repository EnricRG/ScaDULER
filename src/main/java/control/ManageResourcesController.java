package control;

import app.AppSettings;
import app.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import misc.Warning;
import model.CourseResource;

import java.net.URL;
import java.util.ResourceBundle;

public class ManageResourcesController implements Initializable {

    private CourseFormController courseFormController = null;

    public TextField searchResourceField;

    public TableView<CourseResource> resourceTable;
    public TableColumn<CourseResource, String> resourceTable_nameColumn;
    public TableColumn<CourseResource, Integer> resourceTable_quantityColumn;

    public Button addResourceButton;
    public TextField resourceNameField;
    public TextField resourceQuantityField;

    public Button deleteResourceButton;
    public Button minusOneButton;
    public Button plusOneButton;

    public Label warningTag;

    //TODO: delet dis
    private ObservableList<CourseResource> allResources =
            FXCollections.observableArrayList(
                    //JavaConverters.asJavaCollection(MainApp.database().courseResourceDatabase().getElements())
                    new CourseResource("Labmao", 1),
                    new CourseResource("PcLabo", 2)
            );


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        setupTableView();
        initializeWarningSystem();
        bindActions();
    }

    public void setCourseFormController(CourseFormController cfc) { courseFormController = cfc;}

    private void initializeContentLanguage() {
        searchResourceField.setPromptText(AppSettings.language().getItem("manageResources_searchResourceField"));

        resourceTable_nameColumn.setText(AppSettings.language().getItem("manageResources_nameColumn"));
        resourceTable_quantityColumn.setText(AppSettings.language().getItem("manageResources_quantityColumn"));

        addResourceButton.setText(AppSettings.language().getItem("manageResources_addButton"));
        deleteResourceButton.setText(AppSettings.language().getItem("manageResources_deleteButton"));

        resourceQuantityField.setPromptText(AppSettings.language().getItem("manageResources_quantityField"));
        minusOneButton.setText(AppSettings.language().getItem("manageResources_subButton"));
        plusOneButton.setText(AppSettings.language().getItem("manageResources_sumButton"));

        resourceTable.setPlaceholder(new Label(AppSettings.language().getItem("resourceTable_placeholder")));
    }

    private void setupTableView() {
        resourceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        resourceTable_nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        resourceTable_quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        resourceTable.getItems().addAll(allResources);
    }

    private void initializeWarningSystem() {
        hideWarnings();
        warningTag.setText("");
    }

    private void bindActions() {
        //TODO: bind all buttons
        searchResourceField.setOnKeyTyped(event -> filterResourceTable(searchResourceField.getText()));
        addResourceButton.setOnAction(event -> addResource());
        deleteResourceButton.setOnAction(event -> deleteResource());
        //FIXME: uncomment this, makes FXML loading crash
        //resourceTable.getScene().getWindow().setOnCloseRequest(event -> courseFormController.updateResources());
    }

    private void filterResourceTable(String text) {
        ObservableList<CourseResource> filteredResources;

        if(text.isBlank()) filteredResources = allResources;
        else filteredResources = allResources.filtered(resource -> resource.name().toLowerCase().contains(text.toLowerCase()));

        resourceTable.setItems(filteredResources);
    }

    private void addResource() {
        Warning warning = null;
        String name = resourceNameField.getText();
        Integer quantity = Integer.MIN_VALUE;
        try{
            quantity = Integer.parseInt(resourceQuantityField.getText());
        } catch (NumberFormatException nfe){
            warning = new Warning(AppSettings.language().getItem("warning_resourceQuantityNaN"));
        }

        //if Integer parsing went ok, check if input is correctly formatted.
        warning = (warning == null) ? resourceCanBeCreated(name, quantity) : warning;

        if (warning == null) { //if no errors
            hideWarnings();
            updateCourseInTableView(
                    MainApp.database().courseResourceDatabase().createCourseResourceOrElseIncrement(name, quantity)
            );
        }
        else popUpWarning(warning);
    }

    private void updateTableView(){
        resourceTable.refresh();
    }

    private void updateCourseInTableView(CourseResource cr) {
        if(resourceTable.getItems().contains(cr)) updateTableView();
        else resourceTable.getItems().add(cr);
    }

    private void deleteResource() {
        Warning warning = null;

        ObservableList<CourseResource> selection = resourceTable.getSelectionModel().getSelectedItems();

        warning = resourcesCanBeDeleted(selection);

        if (warning == null) { //if no errors
            hideWarnings();
            selection.forEach(resource -> MainApp.database().courseResourceDatabase().removeElement(resource.getName()));
            resourceTable.getItems().removeAll(selection);
        }
        else popUpWarning(warning);
    }

    private Warning resourcesCanBeDeleted(ObservableList<CourseResource> selection) {
        if(selection.isEmpty()){
            return new Warning(AppSettings.language().getItem("warning_resourcesNotSelected"));
        }
        else return null;
    }

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
