package control.form;

import app.AppSettings;
import app.MainApp;
import control.MainController;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misc.Warning;
import model.Course;
import service.CourseDatabase;

public class CourseFormController extends FormController {

    public Label courseNameTag;
    public TextField courseNameField;
    public Label courseDescriptionTag;
    public TextArea courseDescriptionField;
    public CheckBox descriptionWrapCheckBox;

    public Button createCourseButton;

    private CourseDatabase courseDatabase = MainApp.getDatabase().courseDatabase();

    public CourseFormController(MainController mainController){
        super(mainController);
    }
    public CourseFormController(Stage stage, MainController mainController){
        super(stage, mainController);
    }

    @Override
    protected void initializeContentLanguage() {
        courseNameTag.setText(AppSettings.language().getItem("courseForm_courseNameTagText"));
        courseNameField.setPromptText(AppSettings.language().getItem("courseForm_courseNameFieldText"));

        courseDescriptionTag.setText(AppSettings.language().getItem("courseForm_courseDescriptionTagText") + " " + AppSettings.language().getItem("optional_tag") + ":");
        courseDescriptionField.setPromptText(AppSettings.language().getItem("courseForm_courseDescriptionFieldText"));
        descriptionWrapCheckBox.setText(AppSettings.language().getItem("form_wrapDescription"));

        createCourseButton.setText(AppSettings.language().getItem("courseForm_createCourseButtonText"));
    }

    @Override
    protected void setupViews(){}

    @Override
    protected void bindActions() {
        //add course to database and close the window
        createCourseButton.setOnAction(actionEvent -> {
            if(createCourse()) close();
            actionEvent.consume();
        });

        descriptionWrapCheckBox.selectedProperty().bindBidirectional(courseDescriptionField.wrapTextProperty());
    }

    private boolean createCourse() {
        if(!warnings()){
            Course c = courseDatabase.createCourse()._2;

            c.setName(courseNameField.getText());
            c.setDescription(courseDescriptionField.getText());

            getMainController().addCourseTab(c, false);

            return true;
        }
        return false;
    }

    @Override
    protected Warning checkWarnings(){
        if(courseNameField.getText().trim().isEmpty())
            return new Warning(AppSettings.language().getItem("warning_courseNameCannotBeEmpty"));
        else return null;
    }
}
