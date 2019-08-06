package control;

import app.AppSettings;
import app.MainApp;
import factory.CourseResourceManagerViewFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import misc.Warning;
import model.CourseResource;
import scala.collection.Traversable;
import scala.collection.mutable.ListBuffer;

import java.io.IOException;
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

    private StringProperty courseName = new SimpleStringProperty();
    private StringProperty courseDescription = new SimpleStringProperty();
    private Traversable<CourseResource> firstQuarterResources = new ListBuffer<>();
    private Traversable<CourseResource> secondQuarterResources = new ListBuffer<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        bindFieldsToValues();
        initializeWarningSystem();
        bindButtonsToActions();
    }

    MainController getMainController(){ return mainController; } //package private
    void setMainController(MainController mc){
        mainController = mc;
    } //package private

    private void initializeContentLanguage() {
        courseNameTag.setText(AppSettings.language().getItem("courseForm_courseNameTagText"));
        courseNameField.setPromptText(AppSettings.language().getItem("courseForm_courseNameFieldText"));

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

        //TODO: remove TODO tag below
        //TODO: manage course resources
    }

    private void initializeWarningSystem() {
        hideWarnings();
        formWarningTag.setText("");
    }

    private void bindButtonsToActions() {
        manageCourseResourcesButton.setOnAction(event -> promptCourseResourcesForm());

        //add course to database and close the window
        createCourseButton.setOnAction(actionEvent -> {
            if(createCourse()) closeWindow();
            //TODO: explain why course cannot be created (ie name collision)
        });

        descriptionWrapCheckBox.selectedProperty().bindBidirectional(courseDescriptionField.wrapTextProperty());
    }

    private void promptCourseResourcesForm() {
        Stage prompt = new Stage();
        Scene scene;

        try{
            scene = new Scene((Parent)CourseResourceManagerViewFactory.load(this));
        } catch (IOException ioe){
            ioe.printStackTrace();
            scene = new Scene(new VBox());
        }

        prompt.initModality(Modality.WINDOW_MODAL);
        prompt.initOwner(createCourseButton.getScene().getWindow());
        prompt.setTitle(AppSettings.language().getItem("courseForm_manageCourseResources"));
        prompt.setScene(scene);

        prompt.show();
    }

    private void closeWindow() {
        ((Stage)createCourseButton.getScene().getWindow()).close();
    }

    private Warning courseCanBeCreated(){
        //TODO: check this method
        //TODO: abstract this method with parameters
        if(courseName.getValue().isEmpty())
            return new Warning(AppSettings.language().getItem("warning_courseNameCannotBeEmpty"));
        else if(firstQuarterResources.isEmpty()){
            return new Warning(AppSettings.language().getItem("warning_firstQuarterResourcesCannotBeEmpty"));
        }
        else if(secondQuarterResources.isEmpty()){
            return new Warning(AppSettings.language().getItem("warning_secondQuarterResourcesCannotBeEmpty"));
        }
        else return null;
    }

    private boolean createCourse(){
        Warning warning = courseCanBeCreated();
        boolean finished = false;

        //TODO: check if the course is already created
        if(warning == null){ //no warning
            hideWarnings();
            mainController.addCourseTab(
                MainApp.database().courseDatabase().createCourse( //We know here that courseQuarter value cannot be null
                    courseName.getValueSafe(), courseDescription.getValueSafe(), firstQuarterResources, secondQuarterResources)
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

    public void updateResources() {
        //TODO
    }
}
