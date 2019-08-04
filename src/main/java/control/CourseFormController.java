package control;

import app.AppSettings;
import app.MainApp;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misc.Quarters;
import misc.Warning;
import model.Course;
import model.CourseResource;
import scala.collection.Traversable;
import scala.collection.mutable.ListBuffer;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseFormController implements Initializable {

    private MainController mainController;

    public Label courseNameTag;
    public TextField courseNameField;
    public Label courseDescriptionTag;
    public TextArea courseDescriptionField;
    public CheckBox descriptionWrapCheckBox;

    public Button manageCourseResourcesButton;
    public Label manageCourseResourcesInfo;

    public Label formWarningTag;
    public Button createCourseButton;

    //TODO: this shouldn't be implemented here
    public Label courseQuarterTag;
    public ComboBox<Quarters.Quarter> courseQuarterSelector;

    private StringProperty courseName = new SimpleStringProperty();
    private Property<Quarters.Quarter> courseQuarter = new SimpleObjectProperty<>();
    private StringProperty courseDescription = new SimpleStringProperty();
    private Traversable<CourseResource> resources = new ListBuffer<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void customInit(MainController mc){
        mainController = mc;
        initializeContentLanguage();
        bindFieldsToValues();
        initializeWarningSystem();
        bindButtonsToActions();
    }

    private void initializeContentLanguage() {
        courseNameTag.setText(AppSettings.language().getItem("courseForm_courseNameTagText"));
        courseNameField.setPromptText(AppSettings.language().getItem("courseForm_courseNameFieldText"));

        courseQuarterTag.setText(AppSettings.language().getItem("courseForm_courseQuarterTagText"));
        courseQuarterSelector.getItems().addAll(Quarters.FirstQuarter$.MODULE$, Quarters.SecondQuarter$.MODULE$);

        courseDescriptionTag.setText(AppSettings.language().getItem("courseForm_courseDescriptionTagText") + " " + AppSettings.language().getItem("optional_tag") + ":");
        courseDescriptionField.setPromptText(AppSettings.language().getItem("courseForm_courseDescriptionFieldText"));
        descriptionWrapCheckBox.setText(AppSettings.language().getItem("form_wrapDescription"));

        manageCourseResourcesButton.setText(AppSettings.language().getItem("courseForm_manageCourseResourcesButtonText"));
        manageCourseResourcesInfo.setText(AppSettings.language().getItem("courseForm_manageCourseResourcesInfo"));

        createCourseButton.setText(AppSettings.language().getItem("courseForm_createCourseButtonText"));
    }

    private void bindFieldsToValues() {
        courseName.bind(courseNameField.textProperty());

        courseDescription.bind(courseDescriptionField.textProperty());

        courseQuarter.bind(courseQuarterSelector.getSelectionModel().selectedItemProperty());

        //TODO: manage course resources
    }

    private void initializeWarningSystem() {
        hideWarnings();
        formWarningTag.setText("");
    }

    private void bindButtonsToActions() {
        //TODO: bind manage resources button

        //add course to database and close the window
        createCourseButton.setOnAction(actionEvent -> {
            if(createCourse()) closeWindow();
        });

        descriptionWrapCheckBox.selectedProperty().bindBidirectional(courseDescriptionField.wrapTextProperty());
    }

    private void closeWindow() {
        ((Stage)createCourseButton.getScene().getWindow()).close();
    }

    private Warning courseCanBeCreated(){
        if(courseName.getValue().isEmpty())
            return new Warning(AppSettings.language().getItem("warning_courseNameCannotBeEmpty"));
        else if(courseQuarter.getValue() == null){
            return new Warning(AppSettings.language().getItem("warning_courseQuarterCannotBeEmpty"));
        }
        else return null;
    }

    private boolean createCourse(){
        Warning warning = courseCanBeCreated();
        boolean finished = false;

        if(warning == null){ //no warning
            hideWarnings();
            mainController.addCourseTab(
                MainApp.database().courseDatabase().createCourse( //We know here that courseQuarter value cannot be null
                    courseName.getValueSafe(), courseDescription.getValueSafe(), courseQuarter.getValue() , resources)
                );

            finished = true;
        }
        else{
            popUpWarning(warning);
        }

        return finished;
    }

    private void hideWarnings(){ formWarningTag.setVisible(false); }
    private void showWarnings(){ formWarningTag.setVisible(true); }

    private void popUpWarning(Warning warning) {
        formWarningTag.setText(warning.toString());
        showWarnings();
    }

}
