package control.form

import java.net.URL
import java.util.ResourceBundle

import app.{AppSettings, FXMLPaths}
import control.schedule.ResourceAvailabilityController
import factory.ViewFactory
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.stage.{Modality, Stage}
import misc.Warning
import model.descriptor.ResourceDescriptor
import model.{ResourceLike, ResourceSchedule}
import util.Utils

import scala.util.{Failure, Success, Try}

/**
 * Container class to hold the initial values of ResourceFormController fields and variables.
 * Used to initialize content on ResourceFormController fields at controller initialization stage.
 * All fields are optional.
 * @param name Optional non-null string for resource's name.
 * @param capacity Optional non-null integer for resource's capacity.
 * @param availability Optional non-null resource schedule for resource's availability.
 */
case class ResourceFormInitializer( name: Option[String],
                                    capacity: Option[Int],
                                    availability: Option[ResourceSchedule] ) {

  /************************************************************************\
   *                                                                      *
   *                       Additional constructors                        *
   *                                                                      *
  \************************************************************************/

  /**
   * Additional constructor that takes non-optional fields.
   * @param name A non-null string for resource's name.
   * @param capacity A non-null integer for resource's capacity.
   * @param availability A non-null resource schedule for resource's availability.
   */
  def this(name: String, capacity: Int, availability: ResourceSchedule) =
    this(Some(name), Some(capacity), Some(availability))
}

/**
 * Companion object to help on creation of new ResourceFormInitializer instances.
 */
object ResourceFormInitializer {

  /**
   * Creates a ResourceFormInitializer given a name, a capacity and a resource availability schedule.
   * @param name A non-null string for the name.
   * @param capacity A non-null integer for the capacity.
   * @param availability A non-null resource schedule for the availability.
   * @return A new instance of ResourceFormInitializer.
   */
  def apply(name: String, capacity: Int, availability: ResourceSchedule) =
    new ResourceFormInitializer(name, capacity, availability)

  /**
   * Creates a ResourceFormInitializer instance from the properties of a given resource.
   * @param resource A non-null resource.
   * @return A new instance of ResourceFormInitializer built from `resource`'s properties.
   */
  def fromResourceLike(resource: ResourceLike): ResourceFormInitializer = {
    ResourceFormInitializer(resource.name, resource.capacity, resource.availability)
  }

}

abstract class ResourceFormController[FR](formInitializer: Option[ResourceFormInitializer])
  extends FormController[FR] {

  /** Form fields. Store basic user input information */

  @FXML var nameTag: Label = _
  @FXML var nameField: TextField = _
  @FXML var nameFieldTooltip: Tooltip = _

  @FXML var capacityTag: Label = _
  @FXML var capacityField: TextField = _
  @FXML var capacityFieldTooltip: Tooltip = _

  @FXML var descriptionTag: Label = _
  @FXML var descriptionField: TextArea = _
  @FXML var descriptionFieldTooltip: Tooltip = _
  @FXML var descriptionWrapCheckBox: CheckBox = _
  @FXML var descriptionWrapCheckBoxTooltip: Tooltip = _

  @FXML var manageAvailabilityButton: Button = _
  @FXML var manageAvailabilityButtonTooltip: Tooltip = _

  @FXML var finishFormButton: Button = _
  @FXML var finishFormButtonTooltip: Tooltip = _

  /** Local form variables. Store information beyond simple fields */

  protected val availability: ResourceSchedule =
    if(formInitializer.nonEmpty && formInitializer.get.availability.nonEmpty)
      new ResourceSchedule(formInitializer.get.availability.get)
    else
      ResourceSchedule.newDefaultSchedule

  protected lazy val availabilityManagerController: ResourceAvailabilityController = {
    val controller = new ResourceAvailabilityController(availability)

    controller.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse(
        "manageResources_availabilityPrompt",
        "Manage Availability"),
      manageAvailabilityButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[ResourceAvailabilityController](FXMLPaths.ResourceAvailabilityManager),
      controller))

    controller
  }

  /** Constructors and Initializers */

  def this() = this(None)

  def this(stage: Stage, formInitializer: Option[ResourceFormInitializer] = None) = {
    this(formInitializer)
    setStage(stage)
  }

  protected def fillForm(formInitializer: ResourceFormInitializer): Unit = {
    if (formInitializer.name.nonEmpty) nameField.setText(formInitializer.name.get)
    if (formInitializer.capacity.nonEmpty) capacityField.setText(formInitializer.capacity.get.toString)
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    super.initialize(url, resourceBundle)
    if(formInitializer.nonEmpty) fillForm(formInitializer.get)
  }

  override protected def initializeContentLanguage(): Unit = {
    nameTag.setText(AppSettings.language.getItemOrElse(
      "resourceForm_nameTagText",
      "Resource name") + ":")

    nameField.setPromptText(AppSettings.language.getItemOrElse(
      "resourceForm_nameFieldText",
      "Full Resource name"))

    nameFieldTooltip.setText(AppSettings.language.getItemOrElse(
      "resourceForm_nameFieldTooltipText",
      "A name to identify this resource. Format: a string of characters."))


    capacityTag.setText(AppSettings.language.getItemOrElse(
      "resourceForm_capacityTagText",
      "Resource capacity") + ":")

    capacityField.setPromptText(AppSettings.language.getItemOrElse(
      "resourceForm_capacityFieldText",
      "Resource capacity"))

    capacityFieldTooltip.setText(AppSettings.language.getItemOrElse(
      "resourceForm_capacityFieldTooltipText",
      "The number of people this resource can handle at once. Format: a positive integer."))


    descriptionTag.setText(
      AppSettings.language.getItemOrElse("resourceForm_descriptionTagText", "Resource description") +
      " " +
      AppSettings.language.getItemOrElse("optional_tag", "(Optional)") + ":")

    descriptionField.setPromptText(AppSettings.language.getItemOrElse(
      "resourceForm_descriptionFieldText",
      "Enter resource description"))

    descriptionFieldTooltip.setText(AppSettings.language.getItemOrElse(
      "resourceForm_descriptionFieldTooltipText",
      "A description of what this resource represents, or a list of its traits, for example. " +
        "Format: a string of characters."))

    descriptionWrapCheckBox.setText(AppSettings.language.getItemOrElse(
      "form_wrapDescription",
      "Wrap text on borders"))

    descriptionWrapCheckBoxTooltip.setText(AppSettings.language.getItemOrElse(
      "resourceForm_descriptionWrapCheckBoxTooltipText",
      "When checked, the text inside the description field will wrap around the edges " +
        "of the box containing it and jump to next line (without inserting jump characters)."))


    manageAvailabilityButton.setText(AppSettings.language.getItemOrElse(
      "resourceForm_manageAvailabilityButtonText",
      "Manage Availability"))

    manageAvailabilityButtonTooltip.setText(AppSettings.language.getItemOrElse(
      "resourceForm_manageAvailabilityButtonTooltipText",
      "Opens a new window containing an editable schedule view of resource's availability."))
  }

  override protected def setupViews(): Unit = {}

  override protected def bindActions(): Unit = {
    descriptionWrapCheckBox.selectedProperty.bindBidirectional(descriptionField.wrapTextProperty)

    manageAvailabilityButton.setOnAction(actionEvent => {
      showAndWaitAvailabilityManager()
      actionEvent.consume()
    })
  }

  //inheritable method to enable child classes to extend and
  //use more information about availabilityManagerController
  protected def showAndWaitAvailabilityManager(): Unit = {
    availabilityManagerController.showAndWait()
  }


  /************************************************************************\
   *                                                                      *
   *                    Error checking for form fields                    *
   *                                                                      *
  \************************************************************************/


  /**
   *  Checks if all fields are well formatted.
   *  @note   `nameField` and `capacityField` cannot be null,
   *          i.e. the controller must be properly initialized before this call.
   *  @return Some(warning) related to the first wrong field found if any, None otherwise.
   */
  override protected def checkWarnings: Option[Warning] = {
    val capacity: Try[Int] = getCapacityFieldValue

    if (nameField.getText.trim.isEmpty) //if name field is empty
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_resourceNameCannotBeEmpty",
        "Resource name cannot be empty.")))
    else if (capacity.isFailure) //if capacity field is not well formatted
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_resourceCapacityNaN",
        "The capacity is not a number.")))
    else if (capacity.get.compareTo(AppSettings.minCapacityPerResource) < 0)
      //if capacity is lower than minimum required.
      //get is safe, if we're here capacity is not a Failure
      Some(new Warning(capacity + AppSettings.language.getItemOrElse(
        "warning_resourceCapacityMin",
        " is lower than the minimum allowed quantity") +
        " (" + AppSettings.minCapacityPerResource + ")."))
    else
      None
  }


  /************************************************************************\
   *                                                                      *
   *      Getters for form field that require some extra processing       *
   *                                                                      *
  \************************************************************************/


  /**
   * Gets the numeric value typed in capacityField.
   * @note `capacityField` cannot be null, i.e. the controller must be properly initialized before this call.
   * @return  Success(0)          if the field is empty,
   *          Success(c)          where c is the integer capacity typed in the field
   *                              if it was well formatted (a positive integer or a 0), or
   *          Failure(exception)  if the characters typed in the field don't represent an integer number.
   */
  protected def getCapacityFieldValue: Try[Int] = {
    if(capacityField.getText.trim.isEmpty)
      Success(0)
    else {
      try Success(capacityField.getText.toInt)
      catch { case nfe: NumberFormatException => Failure(nfe) }
    }
  }

}

class CreateResourceFormController(formInitializer: Option[ResourceFormInitializer] = None)
  extends ResourceFormController[ResourceDescriptor](formInitializer) {

  /** Initializers */

  override protected def initializeContentLanguage(): Unit = {
    super.initializeContentLanguage()

    finishFormButton.setText(AppSettings.language.getItemOrElse(
      "resourceForm_createButtonText",
      "Create Resource"))

    finishFormButtonTooltip.setText(AppSettings.language.getItemOrElse(
      "resourceForm_createButtonTooltipText",
      "Click this button to finish this form and create the resource. " + "\n" +
        "To cancel it and don't create the resource, close this window instead."))
  }

  override protected def bindActions(): Unit = {
    super.bindActions()

    finishFormButton.setOnAction(actionEvent => {
      if (!warnings) { //create resource from form fields
        formResult = Some(createResource)
        close()
      }
      actionEvent.consume()
    })
  }

  /** Entity creation */

  //pre: !warnings
  private def createResource: ResourceDescriptor = {
    val cd = new ResourceDescriptor
    cd.name = nameField.getText
    cd.capacity = getCapacityFieldValue.get
    //cd.description = descriptionField.getText
    cd.availability = availability //Aliasing is ok, won't be used anymore here.
    cd
  }

}

class EditResourceFormController[R <: ResourceLike](resource: R)
  extends ResourceFormController[R](Some(ResourceFormInitializer.fromResourceLike(resource))) {

  object EditInformation {
    //This object keeps track of what fields have been edited.

    var nameFieldChanged: Boolean = false
    var capacityFieldChanged: Boolean = false
    //var descriptionFieldChanged: Boolean = false
    var availabilityChanged: Boolean = false

    def changed: Boolean =
      nameFieldChanged        ||
      capacityFieldChanged    ||
      //descriptionFieldChanged ||
      availabilityChanged
  }

  /** Initializers */

  override def initializeContentLanguage(): Unit = {
    super.initializeContentLanguage()

    finishFormButton.setText(AppSettings.language.getItemOrElse(
      "resourceForm_editButtonText",
      "Edit Resource"))

    finishFormButtonTooltip.setText(AppSettings.language.getItemOrElse(
      "resourceForm_editButtonTooltipText",
      "Click this button to finish this form and save changes of this resource." + "\n" +
        "To cancel it and don't save the changes, close this window instead."))
  }

  override protected def bindActions(): Unit = {
    super.bindActions()

    bindChangeReporters()

    finishFormButton.setOnAction(actionEvent => {
      if (!warnings) { //edit resource from form fields
        formResult = modifyResource(resource)
        close()
      }
      actionEvent.consume()
    })
  }

  private def bindChangeReporters(): Unit = {
    nameField.textProperty().addListener((observable, oldValue, newValue) => {
      EditInformation.nameFieldChanged = true
    })

    capacityField.textProperty().addListener((observable, oldValue, newValue) => {
      EditInformation.capacityFieldChanged = true
    })

    /*descriptionField.textProperty().addListener((observable, oldValue, newValue) => {
      EditInformation.descriptionFieldChanged = true
    })*/
  }

  override def showAndWaitAvailabilityManager(): Unit = {
    super.showAndWaitAvailabilityManager()
    EditInformation.availabilityChanged = availabilityManagerController.availabilityChanged
  }

  /** Entity creation */

  //pre: !warnings
  //post: if r has been edited in this form, the result will be Some(r), None otherwise.
  private def modifyResource(r: R): Option[R] = {

    if(EditInformation.changed) {
      //Assigning strings directly is faster than checking if there's any change.
      r.name = nameField.getText.trim
      r.capacity = getCapacityFieldValue.get
      //r.description = descriptionField.getText
      r.availability = availability //Aliasing is ok, won't be used anymore here.
      Some(r)
    }
    else
      None
  }
}
