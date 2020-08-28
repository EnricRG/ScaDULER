package control.form

import java.net.URL
import java.util.ResourceBundle

import app.{AppSettings, FXMLPaths}
import factory.ViewFactory
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.stage.{Modality, Stage}
import javafx.util.StringConverter
import misc.{Duration, Warning}
import model.Weeks.Periodicity
import model._
import model.descriptor.EventDescriptor
import util.Utils

import scala.collection.{JavaConverters, mutable}

case class EventLikeFormInitializer[
  S <: SubjectLike[S,C,R,E],
  C <: CourseLike,
  R <: ResourceLike,
  E <: EventLike[S,C,R,E]]( name: Option[String],
                            shortName: Option[String],
                            description: Option[String],
                            course: Option[C],
                            quarter: Option[Quarter],
                            subject: Option[S],
                            duration: Option[Duration],
                            eventType: Option[EventType],
                            periodicity: Option[Periodicity],
                            resource: Option[R],
                            incompatibilities: Option[Iterable[E]] ) {

  def this( name: String,
            shortName: String,
            description: String,
            course: Option[C],
            quarter: Option[Quarter],
            subject: Option[S],
            duration: Duration,
            eventType: EventType,
            periodicity: Periodicity,
            resource: Option[R],
            incompatibilities: Option[Iterable[E]]) =
  this(Some(name), Some(shortName), Some(description), course, quarter, subject, Some(duration),
    Some(eventType), Some(periodicity), resource, incompatibilities)
}

object EventLikeFormInitializer {
  def apply[S <: SubjectLike[S,C,R,E],
    C <: CourseLike,
    R <: ResourceLike,
    E <: EventLike[S,C,R,E]]( name: String,
                              shortName: String,
                              description: String,
                              course: Option[C],
                              quarter: Option[Quarter],
                              subject: Option[S],
                              duration: Duration,
                              eventType: EventType,
                              periodicity: Periodicity,
                              resource: Option[R],
                              incompatibilities: Option[Iterable[E]]): EventLikeFormInitializer[S,C,R,E] =
    new EventLikeFormInitializer(name, shortName, description, course, quarter, subject, duration, eventType,
    periodicity, resource, incompatibilities)

  def fromEventLike[ S <: SubjectLike[S,C,R,E],
    C <: CourseLike,
    R <: ResourceLike,
    E <: EventLike[S,C,R,E]](el: EventLike[S,C,R,E]): EventLikeFormInitializer[S, C, R, E] = {
    EventLikeFormInitializer(el.name, el.shortName, el.description, el.course, el.quarter, el.subject,
      Duration(el.duration), el.eventType, el.periodicity, el.neededResource, Some(el.incompatibilities))
  }
}

//invariant: if present, `oefi`'s fields have to meet this requirements:
//           - `course` has to be part of `courses`
//           - `subject` has to be part of `subject`
//           - `resource` has to be part of `resources`
//           - all `incompatibilities`' elements have to be part of `events`
abstract class EventLikeFormController[FR,
  S >: Null <: SubjectLike[S,C,R,E],
  C <: CourseLike,
  R >: Null <: ResourceLike,
  E <: EventLike[S,C,R,E]](oefi: Option[EventLikeFormInitializer[S,C,R,E]] = None,
                           subjects: Iterable[S],
                           courses: Iterable[C],
                           resources: Iterable[R],
                           events: Iterable[E])
  extends FormController[FR] {

  /** Form fields. Store basic user input information */

  @FXML var nameTag: Label = _
  @FXML var nameField: TextField = _

  @FXML var shortNameTag: Label = _
  @FXML var shortNameField: TextField = _

  @FXML var descriptionTag: Label = _
  @FXML var descriptionField: TextArea = _
  @FXML var descriptionWrapCheckBox: CheckBox = _

  @FXML var courseTag: Label = _
  @FXML var courseBox: ComboBox[C] = _

  @FXML var quarterTag: Label = _
  @FXML var quarterBox: ComboBox[Quarter] = _

  @FXML var subjectTag: Label = _
  @FXML var subjectBox: ComboBox[S] = _
  @FXML var unselectSubjectButton: Button = _

  @FXML var durationTag: Label = _
  @FXML var durationBox: ComboBox[Duration] = _

  @FXML var eventTypeTag: Label = _
  @FXML var eventTypeBox: ComboBox[EventType] = _

  @FXML var periodicityTag: Label = _
  @FXML var periodicityBox: ComboBox[Weeks.Periodicity] = _

  @FXML var resourceTag: Label = _
  @FXML var resourceBox: ComboBox[R] = _
  @FXML var unselectResourceButton: Button = _

  @FXML var manageIncompatibilitiesButton: Button = _

  //@FXML var managePrecedencesButton: Button = _

  @FXML var finishFormButton: Button = _

  /** Local form variables. Store information beyond simple fields */

  //TODO adapt to return (new incompatibilities, removed incompatibilities)
  protected lazy val incompatibilityManagerController: EventIncompatibilityFormController[E] = {
    val initialIncompatibilities: mutable.Set[E] = {
      val hashSet = new mutable.HashSet[E]

      if(oefi.nonEmpty && oefi.get.incompatibilities.nonEmpty)
        hashSet ++= oefi.get.incompatibilities.get

      hashSet
    }

    val controller = new EventIncompatibilityFormController(initialIncompatibilities, events)

    controller.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse(
        "eventForm_manageIncompatibilities_windowTitle",
        "Manage Incompatibilities"),
      manageIncompatibilitiesButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[EventIncompatibilityFormController[E]](FXMLPaths.EventIncompatibilityFrom),
      controller))

    controller
  }

  /** Constructors and Initializers */

  def this(oefi: Option[EventLikeFormInitializer[S,C,R,E]],
           subjects: Iterable[S],
           courses: Iterable[C],
           resources: Iterable[R],
           events: Iterable[E],
           stage: Stage) = {
    this(oefi, subjects, courses, resources, events)
    setStage(stage)
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    super.initialize(url, resourceBundle)
    if(oefi.nonEmpty) fillForm(oefi.get)
  }

  protected def fillForm(efi: EventLikeFormInitializer[S,C,R,E]): Unit = {
    if(efi.name.nonEmpty) nameField.setText(efi.name.get)
    if(efi.shortName.nonEmpty) shortNameField.setText(efi.shortName.get)
    if(efi.description.nonEmpty) descriptionField.setText(efi.description.get)
    if(efi.course.nonEmpty) courseBox.getSelectionModel.select(efi.course.get)
    if(efi.quarter.nonEmpty) quarterBox.getSelectionModel.select(efi.quarter.get)
    if(efi.subject.nonEmpty) subjectBox.getSelectionModel.select(efi.subject.get)
    if(efi.duration.nonEmpty) durationBox.getSelectionModel.select(efi.duration.get)
    if(efi.eventType.nonEmpty) eventTypeBox.getSelectionModel.select(efi.eventType.get)
    if(efi.periodicity.nonEmpty) periodicityBox.getSelectionModel.select(efi.periodicity.get)
    if(efi.resource.nonEmpty) resourceBox.getSelectionModel.select(efi.resource.get)

    //incompatibilities are initialized at object construction stage.
  }

  override protected def initializeContentLanguage(): Unit = {
    nameTag.setText(AppSettings.language.getItemOrElse(
      "eventForm_eventName",
      "Event name") + ":")

    nameField.setPromptText(AppSettings.language.getItemOrElse(
      "eventForm_eventNameHelp",
      "Full Event name"))

    shortNameTag.setText(AppSettings.language.getItemOrElse(
      "eventForm_eventShortName",
      "Event short name") + ":")

    shortNameField.setPromptText(AppSettings.language.getItemOrElse(
      "eventForm_eventShortNameHelp",
      "Event name abbreviation"))

    descriptionTag.setText(
      AppSettings.language.getItemOrElse("eventForm_eventDescription", "Event description") + " " +
      AppSettings.language.getItemOrElse("optional_tag", "(Optional)") + ":")

    descriptionField.setPromptText(AppSettings.language.getItemOrElse(
      "eventForm_eventDescriptionHelp",
      "Detailed Event description"))

    descriptionWrapCheckBox.setText(AppSettings.language.getItemOrElse(
      "form_wrapDescription",
      "Wrap text on edges"))

    courseTag.setText(AppSettings.language.getItemOrElse("eventForm_courseTag", "Course") + ":")

    quarterTag.setText(AppSettings.language.getItemOrElse("eventForm_eventQuarterTag", "Quarter") + ":")

    subjectTag.setText(
      AppSettings.language.getItemOrElse("eventForm_eventSubjectTag", "Subject") + " " +
      AppSettings.language.getItemOrElse("optional_tag", "(Optional)") + ":")

    durationTag.setText(AppSettings.language.getItemOrElse("eventForm_eventDurationTag", "Duration") + ":")

    eventTypeTag.setText(AppSettings.language.getItemOrElse("eventForm_eventTypeTag", "Event type"))

    periodicityTag.setText(AppSettings.language.getItemOrElse(
      "eventForm_eventPeriodicityTag",
      "Event periodicity") + ":")

    resourceTag.setText(
      AppSettings.language.getItemOrElse("eventForm_eventResourceTag", "Needed Resource") + " " +
      AppSettings.language.getItemOrElse("optional_tag", "(Optional)") + ":")

    manageIncompatibilitiesButton.setText(AppSettings.language.getItemOrElse(
      "eventForm_manageIncompatibilities",
      "Manage Incompatibilities") + "...")
  }

  override protected def setupViews(): Unit = {
    courseBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(courses)))

    courseBox.setCellFactory(param => new ListCell[C] {
      override protected def updateItem(item: C, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null) setGraphic(null)
        else setText(item.name)
      }
    })

    quarterBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(Quarters.quarters)))

    subjectBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(subjects)))

    subjectBox.setConverter(new StringConverter[S]() {
      override def toString(`object`: S): String =
        if (`object` == null) null
        else `object`.name

      override def fromString(string: String): S = null
    })

    durationBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(Duration.getDurations)))

    eventTypeBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(EventTypes.allEventTypes)))

    periodicityBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(Weeks.periodicityList)))

    resourceBox.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(resources)))

    resourceBox.setConverter(new StringConverter[R]() {
      override def toString(`object`: R): String =
        if (`object` == null) null
        else `object`.name

      override def fromString(string: String): R = null
    })
  }

  override protected def bindActions(): Unit = {
    descriptionWrapCheckBox.selectedProperty.bindBidirectional(descriptionField.wrapTextProperty)

    unselectSubjectButton.setOnAction(actionEvent => {
      subjectBox.getSelectionModel.clearSelection()
      actionEvent.consume()
    })

    unselectResourceButton.setOnAction(actionEvent => {
      resourceBox.getSelectionModel.clearSelection()
      actionEvent.consume()
    })

    manageIncompatibilitiesButton.setOnAction(actionEvent => {
      manageIncompatibilities()
      actionEvent.consume()
    })
  }

  //inheritable method to enable child classes to extend and
  //use more information about `incompatibilityManagerController`
  protected def manageIncompatibilities(): Unit = {
    incompatibilityManagerController.showAndWait()
  }

  /************************************************************************\
   *                                                                      *
   *                    Error checking for form fields                    *
   *                                                                      *
  \************************************************************************/

  override protected def checkWarnings: Option[Warning] =
    if (nameField.getText.trim.isEmpty)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_eventNameCannotBeEmpty",
        "event name cannot be empty") + "."))
    else if (courseBox.getValue == null)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_courseCannotBeEmpty",
        "course cannot be empty") + "."))
    else if (quarterBox.getValue == null)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_quarterCannotBeEmpty",
        "quarter cannot be empty") + "."))
    else if (durationBox.getValue == null)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_durationCannotBeEmpty",
        "event duration cannot be empty") + "."))
    else if (eventTypeBox.getValue == null)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_eventTypeCannotBeEmpty",
        "event type cannot be empty") + "."))
    else if (periodicityBox.getValue == null)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_eventPeriodicityCannotBeEmpty",
        "periodicity cannot be empty") + "."))
    else
      None
}

class CreateEventFormController[S >: Null <: SubjectLike[S,C,R,E],
  C <: CourseLike,
  R >: Null <: ResourceLike,
  E <: EventLike[S,C,R,E]]( oefi: Option[EventLikeFormInitializer[S,C,R,E]] = None,
                            subjects: Iterable[S],
                            courses: Iterable[C],
                            resources: Iterable[R],
                            events: Iterable[E])
  extends EventLikeFormController[EventDescriptor[S,C,R,E], S, C, R, E](
    oefi, subjects, courses, resources, events) {

  /** Initializers */

  override protected def initializeContentLanguage(): Unit = {
    super.initializeContentLanguage()

    finishFormButton.setText(AppSettings.language.getItemOrElse(
      "eventForm_createButtonText",
      "Create Event"))
  }

  override protected def bindActions(): Unit = {
    super.bindActions()

    finishFormButton.setOnAction(actionEvent => {
      if (!warnings) { //create event from form fields
        formResult = Some(createEvent)
        close()
      }
      actionEvent.consume()
    })
  }

  /** Entity creation */

  //pre: no warnings
  private def createEvent: EventDescriptor[S, C, R, E] = {
    val ed: EventDescriptor[S, C, R, E] = new EventDescriptor

    ed.name = nameField.getText.trim
    ed.shortName = shortNameField.getText.trim
    ed.description = descriptionField.getText.trim
    if(courseBox.getValue != null) ed.course = Some(courseBox.getValue)
    if(quarterBox.getValue != null) ed.quarter = Some(quarterBox.getValue)
    ed.duration = durationBox.getValue.toInt
    ed.eventType = eventTypeBox.getValue
    ed.periodicity = periodicityBox.getValue

    if (subjectBox.getValue != null) ed.subject = Some(subjectBox.getValue)
    if (resourceBox.getValue != null) ed.neededResource = Some(resourceBox.getValue)

    ed.incompatibilities ++= incompatibilityManagerController.incompatibilities._1

    ed
  }
}

class EditEventLikeFormController[ EE <: EventLike[S,C,R,E],
  S >: Null <: SubjectLike[S,C,R,E],
  C <: CourseLike,
  R >: Null <: ResourceLike,
  E <: EventLike[S,C,R,E]](eventLike: EE,
                           subjects: Iterable[S],
                           courses: Iterable[C],
                           resources: Iterable[R],
                           events: Iterable[E])
  extends EventLikeFormController[EE, S, C, R, E](Some(EventLikeFormInitializer.fromEventLike(eventLike)),
    subjects, courses, resources, events) {

  object EditInformation {
    //This object keeps track of what fields have been edited.

    var nameFieldChanged: Boolean = false
    var shortNameFieldChanged: Boolean = false
    var descriptionFieldChanged: Boolean = false
    var courseBoxSelectionChanged: Boolean = false
    var quarterBoxSelectionChanged: Boolean = false
    var subjectBoxSelectionChanged: Boolean = false
    var durationBoxSelectionChanged: Boolean = false
    var eventTypeBoxSelectionChanged: Boolean = false
    var periodicityBoxSelectionChanged: Boolean = false
    var resourceBoxSelectionChanged: Boolean = false
    var incompatibilitiesChanged: Boolean = false

    def changed: Boolean =
      nameFieldChanged                ||
      shortNameFieldChanged           ||
      descriptionFieldChanged         ||
      courseBoxSelectionChanged       ||
      quarterBoxSelectionChanged      ||
      subjectBoxSelectionChanged      ||
      durationBoxSelectionChanged     ||
      eventTypeBoxSelectionChanged    ||
      periodicityBoxSelectionChanged  ||
      resourceBoxSelectionChanged     ||
      incompatibilitiesChanged
  }

  /** Initializers */

  override def initializeContentLanguage(): Unit = {
    super.initializeContentLanguage()

    finishFormButton.setText(AppSettings.language.getItemOrElse(
      "eventForm_editButtonText",
      "Edit Event"))
  }

  override protected def bindActions(): Unit = {
    super.bindActions()

    bindChangeReporters()

    finishFormButton.setOnAction(actionEvent => {
      if (!warnings) { //edit event from form fields
        formResult = modifyEvent(eventLike)
        close()
      }
      actionEvent.consume()
    })
  }

  private def bindChangeReporters(): Unit = {
    nameField.textProperty().addListener((observable, oldValue, newValue) => {
      EditInformation.nameFieldChanged = true
    })

    shortNameField.textProperty().addListener((observable, oldValue, newValue) => {
      EditInformation.shortNameFieldChanged = true
    })

    descriptionField.textProperty().addListener((observable, oldValue, newValue) => {
      EditInformation.descriptionFieldChanged = true
    })

    courseBox.getSelectionModel.selectedItemProperty().addListener(changeListener => {
      EditInformation.courseBoxSelectionChanged = true
    })

    quarterBox.getSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) => {
      EditInformation.quarterBoxSelectionChanged = true
    })

    subjectBox.getSelectionModel.selectedItemProperty().addListener(changeListener => {
      EditInformation.subjectBoxSelectionChanged = true
    })

    durationBox.getSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) => {
      EditInformation.durationBoxSelectionChanged = true
    })

    eventTypeBox.getSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) => {
      EditInformation.eventTypeBoxSelectionChanged = true
    })

    periodicityBox.getSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) => {
      EditInformation.periodicityBoxSelectionChanged = true
    })

    resourceBox.getSelectionModel.selectedItemProperty().addListener(changeListener => {
      EditInformation.resourceBoxSelectionChanged = true
    })
  }

  override protected def manageIncompatibilities(): Unit = {
    super.manageIncompatibilities()
    EditInformation.incompatibilitiesChanged = incompatibilityManagerController.incompatibilitiesChanged
  }

  /** Entity creation */

  //pre: !warnings
  //post: if r has been edited in this form, the result will be Some(r), None otherwise.
  private def modifyEvent(e: EE): Option[EE] = {

    if(EditInformation.changed) {
      if(EditInformation.nameFieldChanged)                e.name = nameField.getText.trim
      if(EditInformation.shortNameFieldChanged)           e.shortName = shortNameField.getText.trim
      if(EditInformation.descriptionFieldChanged)         e.description = descriptionField.getText.trim
      if(EditInformation.courseBoxSelectionChanged)       e.course = Some(courseBox.getValue)
      if(EditInformation.quarterBoxSelectionChanged)      e.quarter = Some(quarterBox.getValue)
      if(EditInformation.durationBoxSelectionChanged)     e.duration = durationBox.getValue.toInt
      if(EditInformation.eventTypeBoxSelectionChanged)    e.eventType = eventTypeBox.getValue
      if(EditInformation.periodicityBoxSelectionChanged)  e.periodicity = periodicityBox.getValue

      if(EditInformation.subjectBoxSelectionChanged)      e.subject = Some(subjectBox.getValue)
      if(EditInformation.resourceBoxSelectionChanged)     e.neededResource = Some(resourceBox.getValue)

      if(EditInformation.incompatibilitiesChanged){
        val (newIncompatibilities, removedIncompatibilities) =
          incompatibilityManagerController.incompatibilities

        removedIncompatibilities.foreach(e.removeIncompatibility)
        newIncompatibilities.foreach(e.addIncompatibility)
      }

      Some(e)
    }
    else
      None
  }
}