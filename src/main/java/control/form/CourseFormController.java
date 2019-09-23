package control.form;

import app.AppSettings;
import app.MainApp;
import control.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misc.Warning;
import model.Course;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseFormController implements Initializable {

    private MainController mainController;

    public Label courseNameTag;
    public TextField courseNameField;
    public Label courseDescriptionTag;
    public TextArea courseDescriptionField;
    public CheckBox descriptionWrapCheckBox;

    public Label formWarningTag;
    public Button createCourseButton;

    private StringProperty courseName = new SimpleStringProperty();
    private StringProperty courseDescription = new SimpleStringProperty();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        bindFieldsToValues();
        initializeWarningSystem();
        bindButtonsToActions();
    }

    public MainController getMainController(){ return mainController; }
    public void setMainController(MainController mc){
        mainController = mc;
    }

    private void initializeContentLanguage() {
        courseNameTag.setText(AppSettings.language().getItem("courseForm_courseNameTagText"));
        courseNameField.setPromptText(AppSettings.language().getItem("courseForm_courseNameFieldText"));

        courseDescriptionTag.setText(AppSettings.language().getItem("courseForm_courseDescriptionTagText") + " " + AppSettings.language().getItem("optional_tag") + ":");
        courseDescriptionField.setPromptText(AppSettings.language().getItem("courseForm_courseDescriptionFieldText"));
        descriptionWrapCheckBox.setText(AppSettings.language().getItem("form_wrapDescription"));

        createCourseButton.setText(AppSettings.language().getItem("courseForm_createCourseButtonText"));
    }

    private void bindFieldsToValues() {
        courseName.bind(courseNameField.textProperty());

        courseDescription.bind(courseDescriptionField.textProperty());
    }

    private void initializeWarningSystem() {
        hideWarnings();
        formWarningTag.setText("");
    }

    private void bindButtonsToActions() {
        //add course to database and close the window
        createCourseButton.setOnAction(actionEvent -> {
            if(createCourse()) closeWindow();
        });

        descriptionWrapCheckBox.selectedProperty().bindBidirectional(courseDescriptionField.wrapTextProperty());
    }

    private void closeWindow() {
        ((Stage)createCourseButton.getScene().getWindow()).close();
    }

    private Warning courseCanBeCreated(String name){
        if(name.isEmpty())
            return new Warning(AppSettings.language().getItem("warning_courseNameCannotBeEmpty"));
        else return null;
    }

    private boolean createCourse(){
        String name = courseNameField.getText().trim();
        Warning warning = courseCanBeCreated(name);
        boolean finished = false;

        if(warning == null){ //no warning
            hideWarnings();

            Course c = MainApp.getDatabase().courseDatabase().createCourse()._2;
            c.setName(courseName.getValueSafe());
            c.setDescription(courseDescription.getValueSafe());

            mainController.addCourseTab(c, false);

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
