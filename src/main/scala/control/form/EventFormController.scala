package control.form

import app.{AppSettings, FXMLPaths}
import control.StageController
import factory.ViewFactory
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.stage.{Modality, Stage}
import javafx.util.StringConverter
import misc.{Duration, Warning}
import model._
import model.descriptor.EventDescriptor
import util.Utils

import scala.collection.JavaConverters
import scala.collection.mutable.ArrayBuffer

class EventFormController[S >: Null <: SubjectLike, C <: CourseLike, R >: Null <: ResourceLike, E <: EventLike](
  subjects: Iterable[S],
  courses: Iterable[C],
  resources: Iterable[R],
  events: Iterable[E]) extends FormController2[EventDescriptor[S, C, R, E]] {

  @FXML var eventNameTag: Label = _
  @FXML var eventNameField: TextField = _

  @FXML var eventShortNameTag: Label = _
  @FXML var eventShortNameField: TextField = _

  @FXML var eventDescriptionTag: Label = _
  @FXML var eventDescriptionField: TextArea = _
  @FXML var wrapEventDescriptionCheckbox: CheckBox = _

  @FXML var eventCourseTag: Label = _
  @FXML var eventCourseBox: ComboBox[C] = _

  @FXML var eventQuarterTag: Label = _
  @FXML var eventQuarterBox: ComboBox[Quarter] = _

  @FXML var eventSubjectTag: Label = _
  @FXML var eventSubjectBox: ComboBox[S] = _
  @FXML var unassignSubjectButton: Button = _

  @FXML var eventDurationTag: Label = _
  @FXML var eventDurationBox: ComboBox[Duration] = _

  @FXML var eventTypeTag: Label = _
  @FXML var eventTypeBox: ComboBox[EventType] = _

  @FXML var eventPeriodicityTag: Label = _
  @FXML var eventPeriodicityBox: ComboBox[Weeks.Periodicity] = _

  @FXML var eventResourceTag: Label = _
  @FXML var eventResourceBox: ComboBox[R] = _
  @FXML var unassignResourceButton: Button = _

  @FXML var manageIncompatibilitiesButton: Button = _
  //@FXML var managePrecedencesButton: Button = _

  @FXML var createEventButton: Button = _

  private var eventBlueprint: Option[EventDescriptor[S, C, R, E]] = None
  private val incompatibilities: ArrayBuffer[E] = new ArrayBuffer

  def this(subjects: Iterable[S],
           courses: Iterable[C],
           resources: Iterable[R],
           events: Iterable[E],
           stage: Stage) = {
    this(subjects, courses, resources, events)
    setStage(stage)
  }

  override protected def initializeContentLanguage(): Unit = {
    eventNameTag.setText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventName",
        "Event name") + ":")

    eventNameField.setPromptText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventNameHelp",
        "Full Event name"))

    eventShortNameTag.setText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventShortName",
        "Event short name") + ":")

    eventShortNameField.setPromptText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventShortNameHelp",
        "Event name abbreviation"))

    eventDescriptionTag.setText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventDescription",
        "Event description") + " " +
      AppSettings.language.getItemOrElse(
        "optional_tag",
        "(Optional)") + ":")

    eventDescriptionField.setPromptText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventDescriptionHelp",
        "Detailed Event description"))

    wrapEventDescriptionCheckbox.setText(
      AppSettings.language.getItemOrElse(
        "form_wrapDescription",
        "Wrap text on corners"))

    eventCourseTag.setText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventCourseTag",
        "Course") + ":")

    eventQuarterTag.setText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventQuarterTag",
        "Quarter") + ":")

    eventSubjectTag.setText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventSubjectTag",
        "Subject") + " " +
      AppSettings.language.getItemOrElse(
        "optional_tag",
        "(Optional)") + ":")

    eventDurationTag.setText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventDurationTag",
        "Duration") + ":")

    eventTypeTag.setText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventTypeTag",
        "Event type"))

    eventPeriodicityTag.setText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventPeriodicityTag",
        "Event periodicity") + ":")

    eventResourceTag.setText(
      AppSettings.language.getItemOrElse(
        "eventForm_eventResourceTag",
        "Needed Resource") + " " +
      AppSettings.language.getItemOrElse(
        "optional_tag",
        "(Optional)") + ":")

    manageIncompatibilitiesButton.setText(
      AppSettings.language.getItemOrElse(
        "eventForm_manageIncompatibilities",
        "Manage Incompatibilities") + "...")

    createEventButton.setText(
      AppSettings.language.getItemOrElse(
        "eventForm_confirmationButton",
        "Create Event"))
  }

  override protected def setupViews(): Unit = {
    eventCourseBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(courses)))

    eventQuarterBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(Quarters.quarters)))

    eventSubjectBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(subjects)))
    eventSubjectBox.setConverter(new StringConverter[S]() {
      override def toString(`object`: S): String =
        if (`object` == null) null
        else `object`.getName

      override def fromString(string: String): S = null
    })

    eventDurationBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(Duration.getDurations)))

    eventTypeBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(EventTypes.allEventTypes)))

    eventPeriodicityBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(Weeks.periodicityList)))

    eventResourceBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(resources)))
    eventResourceBox.setConverter(new StringConverter[R]() {
      override def toString(`object`: R): String =
        if (`object` == null) null
        else `object`.getName

      override def fromString(string: String): R = null
    })
  }

  override protected def bindActions(): Unit = {
    wrapEventDescriptionCheckbox.selectedProperty.bindBidirectional(eventDescriptionField.wrapTextProperty)

    unassignSubjectButton.setOnAction(actionEvent => {
      eventSubjectBox.getSelectionModel.clearSelection()
      actionEvent.consume()
    })

    unassignResourceButton.setOnAction(actionEvent => {
      eventResourceBox.getSelectionModel.clearSelection()
      actionEvent.consume()
    })

    manageIncompatibilitiesButton.setOnAction(actionEvent => {
      manageIncompatibilities(incompatibilities)
      actionEvent.consume()
    })

    createEventButton.setOnAction(actionEvent => {
      if (!warnings) {
        eventBlueprint = Some(createEvent)
        close()
      }
      actionEvent.consume()
    })
  }

  private def manageIncompatibilities(incompatibilities: ArrayBuffer[E]): Unit = {
    val managerController: StageController =
      new EventIncompatibilityFormController(incompatibilities, events)

    managerController.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse(
        "eventForm_manageIncompatibilities",
        "Manage Incompatibilities"),
      manageIncompatibilitiesButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[StageController](FXMLPaths.EventIncompatibilityFrom),
      managerController))

    managerController.show()
  }

  private def createEvent: EventDescriptor[S, C, R, E] = {
    val ed: EventDescriptor[S, C, R, E] = new EventDescriptor

    ed.name = eventNameField.getText.trim
    ed.shortName = eventShortNameField.getText.trim
    ed.description = eventDescriptionField.getText.trim
    ed.quarter = eventQuarterBox.getValue
    ed.duration = eventDurationBox.getValue.toInt
    ed.eventType = eventTypeBox.getValue
    ed.periodicity = eventPeriodicityBox.getValue

    ed.incompatibilities ++= incompatibilities

    val resource: R = eventResourceBox.getValue
    if (resource != null) ed.neededResource = Some(resource)

    if (eventCourseBox.getValue != null) ed.course = eventCourseBox.getValue

    val subject: S = eventSubjectBox.getValue
    if (subject != null) ed.subject = Some(subject)

    ed
  }

  //TODO adopt Error/Warning subclasses model
  override protected def checkWarnings: Option[Warning] =
    if (eventNameField.getText.trim.isEmpty) Some(new Warning(
      AppSettings.language.getItemOrElse(
        "warning_eventNameCannotBeEmpty",
        "event name cannot be empty") + "."))
    else if (eventCourseBox.getValue == null) Some(new Warning(
      AppSettings.language.getItemOrElse(
        "warning_courseCannotBeEmpty",
        "course cannot be empty") + "."))
    else if (eventQuarterBox.getValue == null) Some(new Warning(
      AppSettings.language.getItemOrElse(
        "warning_quarterCannotBeEmpty",
        "quarter cannot be empty") + "."))
    else if (eventDurationBox.getValue == null) Some(new Warning(
      AppSettings.language.getItemOrElse(
        "warning_durationCannotBeEmpty",
        "event duration cannot be empty") + "."))
    else if (eventTypeBox.getValue == null) Some(new Warning(
      AppSettings.language.getItemOrElse(
        "warning_eventTypeCannotBeEmpty",
        "event type cannot be empty") + "."))
    else if (eventPeriodicityBox.getValue == null) Some(new Warning(
      AppSettings.language.getItemOrElse(
        "warning_eventPeriodicityCannotBeEmpty",
        "periodicity cannot be empty") + "."))
    else None

  override def waitFormResult: Option[EventDescriptor[S, C, R, E]] = {
    showAndWait()
    eventBlueprint
  }
}
