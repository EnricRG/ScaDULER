package control.form

import java.net.URL
import java.util.ResourceBundle

import app.AppSettings
import form.SimpleField
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.stage.Stage
import misc.Warning
import model.CourseLike
import model.descriptor.CourseDescriptor

object CourseFormInitializer {
  def fromCourseLike(c: CourseLike): CourseFormInitializer = {
    CourseFormInitializer(c.name, c.description)
  }
}

case class CourseFormInitializer(name: String, description: String)

abstract class CourseFormController[FR](ocfi: Option[CourseFormInitializer] = None)
  extends FormController[FR] {

  @FXML var courseNameTag: Label = _
  @FXML var courseNameField: TextField = _

  @FXML var courseDescriptionTag: Label = _
  @FXML var courseDescriptionField: TextArea = _
  @FXML var descriptionWrapCheckBox: CheckBox = _

  @FXML var finishFormButton: Button = _

  def this(cfi: Option[CourseFormInitializer], stage: Stage) = {
    this(cfi)
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
  }

  override protected def setupViews(): Unit = {}

  override protected def bindActions(): Unit = {
    descriptionWrapCheckBox.selectedProperty.bindBidirectional(courseDescriptionField.wrapTextProperty)
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

class CreateCourseFormController(ocfi: Option[CourseFormInitializer] = None)
  extends CourseFormController[CourseDescriptor](ocfi) {

  override def initializeContentLanguage(): Unit = {
    super.initializeContentLanguage()

    finishFormButton.setText(AppSettings.language.getItemOrElse(
      "courseForm_createCourseButtonText",
      "Create Course"))
  }

  override def bindActions(): Unit = {
    super.bindActions()

    finishFormButton.setOnAction(actionEvent => {
      if (!warnings) { //create course from form fields
        formResult = Some(createCourse)
        close()
      }
      actionEvent.consume()
    })
  }

  //pre: !warnings
  private def createCourse: CourseDescriptor = {
    val cd = new CourseDescriptor
    cd.name = courseNameField.getText
    cd.description = courseDescriptionField.getText
    cd
  }

}

class EditCourseFormController[C <: CourseLike](course: C)
  extends CourseFormController[C](Some(CourseFormInitializer.fromCourseLike(course))) {

  private class EditCourseFormInformation(c: CourseLike) {
    private val _name: SimpleField[String] = new SimpleField(c.name)
    private val _description: SimpleField[String] = new SimpleField(c.description)

    def name: SimpleField[String] = _name
    def name_=(name: String): Unit = _name.value = name

    def description: SimpleField[String] = _description
    def description_=(description: String): Unit = _description.value = description

    def hasBeenEdited: Boolean = name.edited || description.edited
  }

  private val editInformation: EditCourseFormInformation = new EditCourseFormInformation(course)

  override def initializeContentLanguage(): Unit = {
    super.initializeContentLanguage()

    finishFormButton.setText(AppSettings.language.getItemOrElse(
      "courseForm_editCourseButtonText",
      "Edit Course"))
  }

  override protected def bindActions(): Unit = {
    super.bindActions()

    courseNameField.setOnKeyTyped(keyEvent => {
      editInformation.name = courseNameField.getText()
      //keyEvent.consume()
    })

    courseDescriptionField.setOnKeyTyped(keyEvent => {
      editInformation.description = courseDescriptionField.getText()
      //keyEvent.consume()
    })

    finishFormButton.setOnAction(actionEvent => {
      if (!warnings) { //edit course from form fields
        formResult = modifyEntity(course)
        close()
      }
      actionEvent.consume()
    })
  }

  //if c has been edited, the result will be Some(c), None otherwise.
  private def modifyEntity(c: C): Option[C] = {
    if(editInformation.hasBeenEdited) {
      if(editInformation.name.edited)
        c.name = editInformation.name.value
      if(editInformation.description.edited)
        c.description = editInformation.description.value

      Some(c)
    }
    else
      None
  }
}