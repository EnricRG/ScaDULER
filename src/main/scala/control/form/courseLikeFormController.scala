package control.form

import app.{AppSettings, FXMLPaths}
import control.{SelfInitializedStageController, StageSettings}
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.stage.{Modality, Stage, Window}
import misc.Warning
import model.CourseLike
import model.descriptor.CourseDescriptor

import java.net.URL
import java.util.ResourceBundle

case class CourseFormInitializer( name: Option[String],
                                  description: Option[String] ) {

  def this(name: String, description: String) =
    this(Some(name), Some(description))
}

object CourseFormInitializer {

  def apply(name: String, description: String): CourseFormInitializer =
    new CourseFormInitializer(name, description)

  def fromCourseLike(c: CourseLike): CourseFormInitializer =
    CourseFormInitializer(c.name, c.description)
}

abstract class CourseLikeFormController[FR](formInitializer: Option[CourseFormInitializer])
  extends FormController[FR] {

  @FXML var nameTag: Label = _
  @FXML var nameField: TextField = _
  @FXML var nameFieldTooltip: Tooltip = _

  @FXML var descriptionTag: Label = _
  @FXML var descriptionField: TextArea = _
  @FXML var descriptionFieldTooltip: Tooltip = _
  @FXML var descriptionWrapCheckBox: CheckBox = _
  @FXML var descriptionWrapCheckBoxTooltip: Tooltip = _

  @FXML var finishFormButton: Button = _
  @FXML var finishFormButtonTooltip: Tooltip = _

  def this() = this(None)

  def this(stage: Stage, formInitializer: Option[CourseFormInitializer] = None) = {
    this(formInitializer)
    setStage(stage)
  }

  def fillForm(formInitializer: CourseFormInitializer): Unit = {
    if(formInitializer.name.nonEmpty) nameField.setText(formInitializer.name.get)
    if(formInitializer.description.nonEmpty) descriptionField.setText(formInitializer.description.get)
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    super.initialize(url, resourceBundle)
    if(formInitializer.nonEmpty) fillForm(formInitializer.get)
  }

  override protected def initializeContentLanguage(): Unit = {
    nameTag.setText(AppSettings.language.getItemOrElse(
      "courseForm_courseNameTagText",
      "Course name") + ":")

    nameField.setPromptText(AppSettings.language.getItemOrElse(
      "courseForm_courseNameFieldText",
      "Full Course name"))

    nameFieldTooltip.setText(AppSettings.language.getItemOrElse(
      "courseForm_nameFieldTooltipText",
      "A name to identify this course. Format: a string of characters."))


    descriptionTag.setText(
      AppSettings.language.getItemOrElse("courseForm_courseDescriptionTagText", "Course description") +
      " " +
      AppSettings.language.getItemOrElse("optional_tag", "(Optional)") + ":")

    descriptionField.setPromptText(AppSettings.language.getItemOrElse(
      "courseForm_courseDescriptionFieldText",
      "Enter a course description"))

    descriptionFieldTooltip.setText(AppSettings.language.getItemOrElse(
      "courseForm_descriptionFieldTooltipText",
      "A description about this course. Format: a string of characters. "))

    descriptionWrapCheckBox.setText(AppSettings.language.getItemOrElse(
      "form_wrapDescription",
      "Wrap text on edges"))

    descriptionWrapCheckBoxTooltip.setText(AppSettings.language.getItemOrElse(
      "courseForm_descriptionWrapCheckBoxTooltipText",
      "When checked, the text inside the description field will wrap around the edges " +
        "of the box containing it and jump to next line (without inserting jump characters)."))
  }

  override protected def setupViews(): Unit = {}

  override protected def bindActions(): Unit = {
    descriptionWrapCheckBox.selectedProperty.bindBidirectional(descriptionField.wrapTextProperty)
  }

  override protected def checkWarnings: Option[Warning] = {
    if (nameField.getText.trim.isEmpty)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_courseNameCannotBeEmpty",
        "Course name cannot be empty") + "."))
    else
      None
  }

}

class CreateCourseLikeFormController(formInitializer: Option[CourseFormInitializer] = None)
  extends CourseLikeFormController[CourseDescriptor](formInitializer) {

  override def initializeContentLanguage(): Unit = {
    super.initializeContentLanguage()

    finishFormButton.setText(AppSettings.language.getItemOrElse(
      "courseForm_createCourseButtonText",
      "Create Course"))

    finishFormButtonTooltip.setText(AppSettings.language.getItemOrElse(
      "courseForm_createButtonTooltipText",
      "Click this button to finish this form and create the course. " + "\n" +
        "To cancel it and don't create the course, close this window instead."))
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
    cd.name = nameField.getText
    cd.description = descriptionField.getText
    cd
  }

}

class ShowCourseLikeInformationController(courseLike: CourseLike, owner: Option[Window])
  extends CourseLikeFormController[Nothing](Some(CourseFormInitializer.fromCourseLike(courseLike)))
  with SelfInitializedStageController {

  def this(courseLike: CourseLike, owner: Window) =
    this(courseLike, Some(owner))

  override protected def selfInitialize(): Unit = {
    initializeWith(
      StageSettings(
        AppSettings.language.getItemOrElse("courseForm_show_windowTitle", "Course details"),
        owner,
        Modality.WINDOW_MODAL),
      FXMLPaths.CourseForm
    )
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    super.initialize(url, resourceBundle)
    lockFields()
  }

  private def lockFields(): Unit = {
    nameField.setEditable(false)
    descriptionField.setEditable(false)
  }

  override def initializeContentLanguage(): Unit = {
    super.initializeContentLanguage()

    finishFormButton.setText(AppSettings.language.getItemOrElse(
      "courseForm_closeWindowButton",
      "Close window"))
  }

  override protected def bindActions(): Unit = {
    super.bindActions()

    finishFormButton.setOnAction(actionEvent => {
      close()
      actionEvent.consume()
    })
  }
}

class EditCourseLikeFormController[C <: CourseLike](course: C)
  extends CourseLikeFormController[C](Some(CourseFormInitializer.fromCourseLike(course))) {

  object EditInformation {
    var nameFieldChanged: Boolean = false
    var descriptionFieldChanged: Boolean = false

    def changed: Boolean = nameFieldChanged || descriptionFieldChanged
  }

  override def initializeContentLanguage(): Unit = {
    super.initializeContentLanguage()

    finishFormButton.setText(AppSettings.language.getItemOrElse(
      "courseForm_editCourseButtonText",
      "Edit Course"))

    finishFormButtonTooltip.setText(AppSettings.language.getItemOrElse(
      "courseForm_editButtonTooltipText",
      "Click this button to finish this form and save changes of this course." + "\n" +
        "To cancel it and don't save the changes, close this window instead."))
  }

  override protected def bindActions(): Unit = {
    super.bindActions()

    bindChangeReporters()

    finishFormButton.setOnAction(actionEvent => {
      if (!warnings) { //edit course from form fields
        formResult = modifyEntity(course)
        close()
      }
      actionEvent.consume()
    })
  }

  private def bindChangeReporters(): Unit = {
    nameField.textProperty().addListener(_ => {
      EditInformation.nameFieldChanged = true
    })

    descriptionField.textProperty().addListener(_ => {
      EditInformation.descriptionFieldChanged = true
    })
  }

  //if c has been edited in the form, persists the changes and returns Some(c), returns None otherwise.
  private def modifyEntity(c: C): Option[C] = {
    if(EditInformation.changed) {
      if(EditInformation.nameFieldChanged) c.name = nameField.getText
      if(EditInformation.descriptionFieldChanged) c.description = descriptionField.getText

      Some(c)
    }
    else
      None
  }
}