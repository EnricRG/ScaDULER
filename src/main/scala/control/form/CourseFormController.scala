package control.form

import java.net.URL
import java.util.ResourceBundle

import app.AppSettings
import control.form.FormModes.{Edit, FormMode}
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.stage.Stage
import misc.Warning
import model.descriptor.CourseDescriptor

case class CourseFormInitializer(name: String, description: String)

class CourseFormController(ocfi: Option[CourseFormInitializer] = None,
                           mode: FormMode = FormModes.Create)
  extends FormController[CourseDescriptor] {

  @FXML var courseNameTag: Label = _
  @FXML var courseNameField: TextField = _
  @FXML var courseDescriptionTag: Label = _
  @FXML var courseDescriptionField: TextArea = _
  @FXML var descriptionWrapCheckBox: CheckBox = _

  @FXML var createCourseButton: Button = _

  def this(cfi: Option[CourseFormInitializer], mode: FormMode, stage: Stage) = {
    this(cfi, mode)
    setStage(stage)
  }

  def fillForm(cfi: CourseFormInitializer): Unit = {
    courseNameField.setText(cfi.name)
    courseDescriptionField.setText(cfi.description)
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    super.initialize(url, resourceBundle)
    if(ocfi.nonEmpty) fillForm(ocfi.get)
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

    if(mode == Edit)
      createCourseButton.setText(AppSettings.language.getItemOrElse(
        "courseForm_editCourseButtonText",
        "Edit Course"))
    else
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