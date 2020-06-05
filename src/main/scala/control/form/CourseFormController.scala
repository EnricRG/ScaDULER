package control.form

import app.AppSettings
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.stage.Stage
import misc.Warning
import model.descriptor.CourseDescriptor

class CourseFormController extends FormController[CourseDescriptor] {

  @FXML var courseNameTag: Label = _
  @FXML var courseNameField: TextField = _
  @FXML var courseDescriptionTag: Label = _
  @FXML var courseDescriptionField: TextArea = _
  @FXML var descriptionWrapCheckBox: CheckBox = _

  @FXML var createCourseButton: Button = _

  def this(stage: Stage) = {
    this()
    setStage(stage)
  }

  override protected def initializeContentLanguage(): Unit = {
    courseNameTag.setText(AppSettings.language.getItemOrElse(
      "courseForm_courseNameTagText",
      "Course name") + ":")

    courseNameField.setPromptText(AppSettings.language.getItemOrElse(
      "courseForm_courseNameFieldText",
      "Full Course name"))

    courseDescriptionTag.setText(
      AppSettings.language.getItemOrElse("courseForm_courseDescriptionTagText", "Course description") + " " +
      AppSettings.language.getItemOrElse("optional_tag", "(Optional)") + ":")

    courseDescriptionField.setPromptText(AppSettings.language.getItemOrElse(
      "courseForm_courseDescriptionFieldText",
      "Enter a course description"))

    descriptionWrapCheckBox.setText(AppSettings.language.getItemOrElse(
      "form_wrapDescription",
      "Wrap text on corners"))

    createCourseButton.setText(AppSettings.language.getItemOrElse(
      "courseForm_createCourseButtonText",
      "Create Course"))
  }

  override protected def setupViews(): Unit = {}

  override protected def bindActions(): Unit = {
    createCourseButton.setOnAction(actionEvent => {
        if (!warnings) { //create course from form fields
          formResult = Some(createCourse)
          close()
        }
        actionEvent.consume()
    })

    descriptionWrapCheckBox.selectedProperty.bindBidirectional(courseDescriptionField.wrapTextProperty)
  }

  //pre: !warnings
  private def createCourse: CourseDescriptor = {
    val cd = new CourseDescriptor
    cd.name = courseNameField.getText
    cd.description = courseDescriptionField.getText
    cd
  }

  override protected def checkWarnings: Option[Warning] = {
    if (courseNameField.getText.trim.isEmpty)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_courseNameCannotBeEmpty",
        "Course name cannot be empty") + "."))
    else
      None
  }

}