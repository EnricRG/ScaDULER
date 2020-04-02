package control.form;

import app.AppSettings;
import control.MainController;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misc.Warning;
import model.blueprint.CourseBlueprint;

public class CourseFormController extends FormController<CourseBlueprint> {

    public Label courseNameTag;
    public TextField courseNameField;
    public Label courseDescriptionTag;
    public TextArea courseDescriptionField;
    public CheckBox descriptionWrapCheckBox;

    public Button createCourseButton;

    private CourseBlueprint courseBlueprint = null;

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
            if(!warnings()) {
                courseBlueprint = createCourse();
                close();
            }
            actionEvent.consume();
        });

        descriptionWrapCheckBox.selectedProperty().bindBidirectional(courseDescriptionField.wrapTextProperty());
    }

    //pre: !warnings()
    private CourseBlueprint createCourse() {
        CourseBlueprint cb = new CourseBlueprint();

        cb.name_$eq(courseNameField.getText());
        cb.description_$eq(courseDescriptionField.getText());

        return cb;
    }

    @Override
    protected Warning checkWarnings(){
        if(courseNameField.getText().trim().isEmpty())
            return new Warning(AppSettings.language().getItem("warning_courseNameCannotBeEmpty"));
        else return null;
    }

    @Override
    public CourseBlueprint waitFormResult() {
        showAndWait();
        return courseBlueprint;
    }
}
