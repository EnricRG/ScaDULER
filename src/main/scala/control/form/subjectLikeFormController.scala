package control.form

import app.{AppSettings, FXMLPaths}
import control.{SelfInitializedStageController, StageController, StageSettings}
import factory.ViewFactory
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.stage.{Modality, Stage, Window}
import javafx.util.StringConverter
import misc.{Duration, EventTypeIncompatibility, Warning}
import model.Weeks.Periodicity
import model._
import model.descriptor.{EventDescriptor, SubjectDescriptor}
import util.Utils

import java.net.URL
import java.util
import java.util.ResourceBundle
import scala.collection.{JavaConverters, mutable}

/*TODO refactor to take EventLike definitions. It's not a good design that you have to specify
* the ResourceLike type because has nothing to do with SubjectLike definition but with EventLike
* definition. */
trait SubjectLikeForm[C <: CourseLike, R <: ResourceLike] {
  type ED = SubjectLikeFormEventDescriptor[C,R]
  type SD = SubjectDescriptor[C,ED]
}

class SubjectLikeFormEventDescriptor[C,R] extends EventDescriptor[Nothing,C,R,SubjectLikeFormEventDescriptor[C,R]]

case class SubjectLikeFormInitializer[C,E](
  name: Option[String],
  shortName: Option[String],
  description: Option[String],
  color: Option[Color],
  course: Option[C],
  quarter: Option[Quarter],
  typeIncompatibilities: Option[Iterable[EventTypeIncompatibility]],
  events: Option[Iterable[E]],
)

object SubjectLikeFormInitializer {
  def fromSubjectLike[C <: CourseLike, E <: EventLike[_,C,_,E]](s: SubjectLike[_,C,_,E]): SubjectLikeFormInitializer[C,E] = {
    SubjectLikeFormInitializer(
      Some(s.name),
      Some(s.shortName),
      Some(s.description),
      s.color,
      s.course,
      s.quarter,
      if(s.eventTypeIncompatibilities.nonEmpty) Some(s.eventTypeIncompatibilities) else None,
      if(s.events.nonEmpty) Some(s.events) else None
    )
  }
}

abstract class SubjectLikeFormController[
  FR,
  C <: CourseLike,
  R <: ResourceLike,
  E <: EventLike[_,C,R,_]
]( courses: Iterable[C],
   resources: Iterable[R],
   formInitializer: Option[SubjectLikeFormInitializer[C,E]]
) extends FormController[FR]
  with SubjectLikeForm[C,R] {

  /** Form fields. Store basic user input information */

  @FXML var subjectNameTag: Label = _
  @FXML var subjectNameField: TextField = _

  @FXML var subjectShortNameTag: Label = _
  @FXML var subjectShortNameField: TextField = _

  @FXML var subjectDescriptionTag: Label = _
  @FXML var subjectDescriptionField: TextArea = _

  @FXML var subjectColorTag: Label = _
  @FXML var subjectColorExplanation: Label = _
  @FXML var subjectColorPicker: ColorPicker = _

  @FXML var subjectCourseTag: Label = _
  @FXML var subjectCoursePicker: ComboBox[C] = _

  @FXML var subjectQuarterTag: Label = _
  @FXML var subjectQuarterPicker: ComboBox[Quarter] = _

  @FXML var generateEventsTag: Label = _
  @FXML var generateEvents_eventTypeSelector: ComboBox[EventType] = _
  @FXML var generateEvents_rangeTag: Label = _
  @FXML var generateEvents_rangeLowerBound: TextField = _
  @FXML var generateEvents_rangeUpperBound: TextField = _
  @FXML var generateEvents_equalButton: Button = _
  @FXML var generateEvents_periodicitySelector: ComboBox[Periodicity] = _
  @FXML var generateEvents_durationSelector: ComboBox[Duration] = _
  @FXML var generationExampleTag: Label = _
  @FXML var generationExampleLabel: Label = _

  @FXML var selectResourceTag: Label = _
  @FXML var selectResourceSearchBar: TextField = _
  @FXML var selectResourceListView: ListView[R] = _

  @FXML var generateEventsButton: Button = _

  @FXML var manageEventTypeIncompatibilitiesButton: Button = _

  @FXML var eventTable: TableView[Either[ED,E]] = _
  @FXML var eventTable_nameColumn: TableColumn[Either[ED,E], String] = _
  @FXML var eventTable_resourceColumn: TableColumn[Either[ED,E], String] = _

  @FXML var deleteSelectedEventsButton: Button = _
  @FXML var deleteAllEventsButton: Button = _

  @FXML var finishFormButton: Button = _

  /** Local form variables. Store information beyond simple fields */

  protected val addedEventTypeIncompatibilities: mutable.Set[EventTypeIncompatibility] =
    new mutable.HashSet

  protected val removedEventTypeIncompatibilities: mutable.Set[EventTypeIncompatibility] =
    new mutable.HashSet

  protected val addedEventDescriptors: mutable.Set[ED] =
    new mutable.HashSet

  protected val removedEvents: mutable.Set[E] =
    new mutable.HashSet

  /** Constructors and Initializers */

  def this(courses: Iterable[C], resources: Iterable[R]) =
    this(courses, resources, None)

  def this( courses: Iterable[C],
            resources: Iterable[R],
            stage: Stage,
            formInitializer: Option[SubjectLikeFormInitializer[C,E]] = None) = {
    this(courses, resources, formInitializer)
    setStage(stage)
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    super.initialize(url, resourceBundle)
    if (formInitializer.nonEmpty) fillForm(formInitializer.get)
  }

  protected def fillForm(sfi: SubjectLikeFormInitializer[C,E]): Unit = {
    if (sfi.name.nonEmpty) subjectNameField.setText(sfi.name.get)
    if (sfi.shortName.nonEmpty) subjectShortNameField.setText(sfi.shortName.get)
    if (sfi.description.nonEmpty) subjectDescriptionField.setText(sfi.description.get)
    if (sfi.color.nonEmpty) subjectColorPicker.setValue(sfi.color.get.toJFXColor)
    if (sfi.course.nonEmpty) subjectCoursePicker.getSelectionModel.select(sfi.course.get)
    if (sfi.quarter.nonEmpty) subjectQuarterPicker.getSelectionModel.select(sfi.quarter.get)
    //Event type incompatibilities are handled separately
    if (sfi.events.nonEmpty) fillTable(sfi.events.get)
  }

  override protected def initializeContentLanguage(): Unit = {
    subjectNameTag.setText(AppSettings.language.getItemOrElse(
      "subjectForm_subjectNameTag",
      "Subject Name") + ":")

    subjectNameField.setPromptText(AppSettings.language.getItemOrElse(
      "subjectForm_subjectNameField",
      "Enter full Subject name"))

    subjectShortNameTag.setText(AppSettings.language.getItemOrElse(
      "subjectForm_subjectShortNameTag",
      "Subject Short Name") + ":")

    subjectShortNameField.setPromptText(AppSettings.language.getItemOrElse(
      "subjectForm_subjectShortNameField",
      "Subject name abbreviation"))

    subjectDescriptionTag.setText(
      AppSettings.language.getItemOrElse(
        "subjectForm_subjectDescriptionTag",
        "Subject Description") + " " +
        AppSettings.language.getItemOrElse(
          "optional_tag",
          "(Optional)") + ":")

    subjectDescriptionField.setPromptText(AppSettings.language.getItemOrElse(
      "subjectForm_subjectDescriptionField",
      "Write a description about the contents of this subject."))

    subjectColorTag.setText(AppSettings.language.getItemOrElse(
      "subjectForm_subjectColorTag",
      "Subject Color") + ":")

    subjectColorExplanation.setText(AppSettings.language.getItemOrElse(
      "subjectForm_subjectColorExplanation",
      "This color will be used to draw a thin frame around the events of the subject"))

    subjectCourseTag.setText(AppSettings.language.getItemOrElse(
      "subjectForm_subjectCourseTag",
      "Course") + ":")

    subjectQuarterTag.setText(AppSettings.language.getItemOrElse(
      "subjectForm_subjectQuarterTag",
      "Quarter"))

    generateEventsTag.setText(
      AppSettings.language.getItemOrElse(
        "subjectForm_generateEventsTag",
        "Generate Subject Events") + " " +
        AppSettings.language.getItemOrElse(
          "optional_tag",
          "(Optional)") + ":")

    generateEvents_eventTypeSelector.setPromptText(AppSettings.language.getItemOrElse(
      "subjectForm_eventType",
      "Event type"))

    generateEvents_rangeTag.setText(AppSettings.language.getItemOrElse(
      "subjectForm_rangeTag",
      "Range") + ":")

    generateEvents_rangeLowerBound.setPromptText(AppSettings.language.getItemOrElse(
      "subjectForm_rangeLowerBound",
      "Start"))

    generateEvents_rangeUpperBound.setPromptText(AppSettings.language.getItemOrElse(
      "subjectForm_rangeUpperBound",
      "End"))

    generateEvents_periodicitySelector.setPromptText(AppSettings.language.getItemOrElse(
      "subjectForm_eventPeriodicity",
      "Periodicity"))

    generateEvents_durationSelector.setPromptText(AppSettings.language.getItemOrElse(
      "subjectForm_eventDuration",
      "Event Duration"))

    generationExampleTag.setText(AppSettings.language.getItemOrElse(
      "subjectForm_generationExampleTag",
      "Generation example") + ": ")

    generationExampleLabel.setText("")

    selectResourceTag.setText(AppSettings.language.getItemOrElse(
      "subjectForm_selectResourceTag",
      "Select the resource that these generated events will need") + ":")

    selectResourceSearchBar.setPromptText(AppSettings.language.getItemOrElse(
      "subjectForm_resourceSearchBar",
      "filter resources by name"))

    selectResourceListView.setPlaceholder(new Label(AppSettings.language.getItemOrElse(
      "subjectForm_resourcePlaceholder",
      "No resources")))

    generateEventsButton.setText(AppSettings.language.getItemOrElse(
      "subjectForm_generateEventsButton",
      "Generate Events"))

    manageEventTypeIncompatibilitiesButton.setText(AppSettings.language.getItemOrElse(
      "subjectForm_manageEventTypeIncompatibilitiesButton",
      "Manage Event Type Incompatibilities") + "...")

    eventTable.setPlaceholder(new Label(AppSettings.language.getItemOrElse(
      "subjectForm_evenTablePlaceHolder",
      "No Events created yet")))

    eventTable_nameColumn.setText(AppSettings.language.getItemOrElse(
      "subjectForm_eventTableNameColumn",
      "Name"))

    eventTable_resourceColumn.setText(AppSettings.language.getItemOrElse(
      "subjectForm_eventTableResourceColumn",
      "Resource"))

    deleteSelectedEventsButton.setText(AppSettings.language.getItemOrElse(
      "subjectForm_deleteSelectedEventsButton",
      "Delete Selected Events"))

    deleteAllEventsButton.setText(AppSettings.language.getItemOrElse(
      "subjectForm_deleteAllEventsButton",
      "Delete All Events"))
  }

  override protected def setupViews(): Unit = {
    subjectCoursePicker.setCellFactory(_ => new ListCell[C] {
      override protected def updateItem(item: C, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null) {
          setGraphic(null)
          setText(null)
        }
        else {
          setText(item.name)
        }
      }
    })
    subjectCoursePicker.setConverter(new StringConverter[C]() {
      override def toString(`object`: C): String =
        if (`object` == null) null
        else `object`.name

      override def fromString(string: String): C = null.asInstanceOf[C]
    })
    subjectCoursePicker.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(courses)))

    subjectQuarterPicker.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(Quarters.quarters)))

    generateEvents_eventTypeSelector.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(EventTypes.commonEventTypes)))

    generateEvents_durationSelector.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(Duration.getDurations)))

    generateEvents_durationSelector.setCellFactory(_ => new ListCell[Duration] {
      override protected def updateItem(item: Duration, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null) setGraphic(null)
        else setText(item.toString)
      }
    })

    generateEvents_periodicitySelector.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(Weeks.periodicityList)))

    selectResourceListView.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
    selectResourceListView.setCellFactory(_ => new ListCell[R] {
      override protected def updateItem(resource: R, empty: Boolean): Unit = {
        super.updateItem(resource, empty)
        if (empty || resource == null) setText(null)
        else setText(resource.name)
      }
    })

    selectResourceListView.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(resources)))

    eventTable.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
    eventTable_nameColumn.setCellValueFactory(cell =>
      if (cell.getValue.isLeft) //ED
        new SimpleStringProperty(cell.getValue.left.get.name)
      else //E
        new SimpleStringProperty(cell.getValue.right.get.name)
    )
    eventTable_resourceColumn.setCellValueFactory(cell =>
      if (cell.getValue.isLeft && cell.getValue.left.get.neededResource.nonEmpty) { //ED.
        new SimpleStringProperty(cell.getValue.left.get.neededResource.get.name)
      }
      else if (cell.getValue.isRight && cell.getValue.right.get.neededResource.nonEmpty) { //E.
        new SimpleStringProperty(cell.getValue.right.get.neededResource.get.name)
      }
      else //No needed resource.
        new SimpleStringProperty()
    )
  }

  override protected def bindActions(): Unit = {
    subjectNameField.textProperty.addListener(_ => {
      computeBasicGenerationExample()
    })

    generateEvents_eventTypeSelector.setOnAction(actionEvent => {
      computeBasicGenerationExample()
      actionEvent.consume()
    })

    generateEvents_periodicitySelector.setOnAction(actionEvent => {
      computeBasicGenerationExample()
      actionEvent.consume()
    })

    generateEvents_equalButton.setOnAction(actionEvent => {
      equalizeRangeValues()
      actionEvent.consume()
    })

    selectResourceSearchBar.textProperty.addListener(_ => {
      filterResourceList(selectResourceSearchBar.getText)
    })

    generateEventsButton.setOnAction(actionEvent => {
      generateEvents()
      actionEvent.consume()
    })

    manageEventTypeIncompatibilitiesButton.setOnAction(actionEvent => {
      manageEventTypeIncompatibilities()
      actionEvent.consume()
    })

    deleteSelectedEventsButton.setOnAction(actionEvent => {
      deleteSubjectEvents(eventTable.getSelectionModel.getSelectedItems)
      actionEvent.consume()
    })

    deleteAllEventsButton.setOnAction(actionEvent => {
      deleteSubjectEvents(eventTable.getItems)
      actionEvent.consume()
    })
  }

  protected def manageEventTypeIncompatibilities(): Unit = {

    val incompatibilityFormController = new EventTypeIncompatibilitiesSelectorController(
      if (formInitializer.nonEmpty && formInitializer.get.typeIncompatibilities.nonEmpty)
        formInitializer.get.typeIncompatibilities.get.toSet --
          removedEventTypeIncompatibilities ++
          addedEventTypeIncompatibilities
      else if (addedEventTypeIncompatibilities.nonEmpty)
        addedEventTypeIncompatibilities
      else
        Nil
    )

    incompatibilityFormController.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse(
        "eventForm_manageIncompatibilities",
        "Manage Incompatibilities"),
      manageEventTypeIncompatibilitiesButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[StageController](FXMLPaths.SubjectEventIncompatibilityForm),
      incompatibilityFormController))

    val formResult = incompatibilityFormController.waitFormResult //execution thread stops here.

    if (formResult.nonEmpty) {
      addedEventTypeIncompatibilities --= formResult.get._2
      addedEventTypeIncompatibilities ++= formResult.get._1

      removedEventTypeIncompatibilities --= formResult.get._1
      removedEventTypeIncompatibilities ++= formResult.get._2
    }
  }


  private def fillTable(events: Iterable[E]): Unit =
    events.foreach(addEventToEventTable)

  protected def addEventDescriptorToEventTable(ed: ED): Unit = {
    eventTable.getItems.add(Left(ed))
  }

  protected def addEventToEventTable(e: E): Unit = {
    eventTable.getItems.add(Right(e))
  }

  private def canGenerateExample(subjectName: String, eventType: EventType, periodicity: Periodicity): Boolean =
    subjectName != null && eventType != null && periodicity != null

  private def equalizeRangeValues(): Unit = {
    generateEvents_rangeUpperBound.setText(generateEvents_rangeLowerBound.getText)
  }

  private def computeBasicGenerationExample(): Unit = computeGenerationExample(
    subjectNameField.getText,
    generateEvents_eventTypeSelector.getValue,
    generateEvents_periodicitySelector.getSelectionModel.getSelectedItem,
    1)

  private def computeGenerationExample(subjectName: String, eventType: EventType, periodicity: Periodicity, number: Int): Unit = {
    if (canGenerateExample(subjectName, eventType, periodicity))
      generationExampleLabel.setText("%s (%s-%d) (%s)".format(
        subjectName,
        eventType.toString,
        number,
        periodicity.toShortString))
  }

  protected def deleteSubjectEvents(selectedItems: util.Collection[Either[ED,E]]): Unit = {
    selectedItems.forEach(either =>
      if (either.isLeft)
        addedEventDescriptors -= either.left.get
      else
        removedEvents += either.right.get )

    eventTable.getItems.removeAll(selectedItems)
  }

  private def getRangeLowerBound: Int = getNumberFromTextField(generateEvents_rangeLowerBound)

  private def getRangeUpperBound: Int = getNumberFromTextField(generateEvents_rangeUpperBound)

  //pre filter not null
  private def filterResourceList(filter: String): Unit = if (filter.trim.nonEmpty) {
    selectResourceListView.setItems(
      FXCollections.observableArrayList(JavaConverters.asJavaCollection(
        resources.filter(_.name.toLowerCase.contains(filter.trim.toLowerCase)))))
  }

  private def getNumberFromTextField(textField: TextField): Int = {
    val number: Int = if (!textField.getText.trim.isEmpty) {
      try
        textField.getText.toInt
      catch {
        case _: NumberFormatException =>
          -1
      }
    }
    else -1

    if (number < 1) {
      textField.setText(String.valueOf(1))
      1
    }
    else number
  }

  protected def generateEvents(): Unit = {
    val generatedEvents = generateEvents(
      subjectNameField.getText,
      subjectShortNameField.getText,
      generateEvents_eventTypeSelector.getValue,
      getRangeLowerBound,
      getRangeUpperBound,
      generateEvents_periodicitySelector.getSelectionModel.getSelectedItem,
      generateEvents_durationSelector.getSelectionModel.getSelectedItem,
      selectResourceListView.getSelectionModel.getSelectedItem)

    addEventDescriptors(generatedEvents)
  }

  private def generateEvents(subjectName: String,
                             subjectShortName: String,
                             eventType: EventType,
                             rangeStart: Int, rangeEnd: Int,
                             periodicity: Periodicity, duration: Duration,
                             neededResource: R
                            ): Iterable[ED] = {
    if (!warnings(checkEventGenerationWarnings(eventType, rangeStart, rangeEnd, periodicity, duration, neededResource)))
      for (i <- rangeStart to rangeEnd) yield {
        val event = new ED

        event.name = "%s (%s-%d) (%s)".format(subjectName, eventType.toString, i, periodicity.toShortString)
        event.shortName = "%s (%s %d) (%s)".format(subjectShortName, eventType.toShortString, i, periodicity.toShortString)
        event.eventType = eventType
        event.neededResource = Some(neededResource)
        event.periodicity = periodicity
        event.duration = duration.toInt

        event
      }
    else
      Nil
  }

  protected def addEventDescriptors(eventDescriptors: Iterable[ED]): Unit = {
    addedEventDescriptors ++= eventDescriptors
    eventDescriptors.foreach(addEventDescriptorToEventTable)
  }

  override protected def checkWarnings: Option[Warning] =
    checkSubjectCreationWarnings

  private def checkSubjectCreationWarnings: Option[Warning] = {
    if (subjectNameField.getText.trim.isEmpty)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_subjectNameCannotBeEmpty",
        "Subject Name cannot be empty") + "."))
    else if (subjectShortNameField.getText.trim.isEmpty)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_subjectShortNameCannotBeEmpty",
        "Subject Short Name cannot be empty") + "."))
    else if (subjectCoursePicker.getValue == null)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_courseCannotBeEmpty",
        "course cannot be empty") + "."))
    else if (subjectQuarterPicker.getValue == null)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_quarterCannotBeEmpty",
        "quarter cannot be empty") + "."))
    else
      None
  }

  private def checkEventGenerationWarnings(eventType: EventType,
                                           rangeStart: Int, rangeEnd: Int,
                                           periodicity: Periodicity, duration: Duration,
                                           neededResource: R
                                          ): Option[Warning] = {
    if (neededResource == null)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_resourcesNotSelected",
        "No resource has been selected") + "."))
    else if (rangeStart > rangeEnd)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_descendingRange",
        "Range start has to be lower or equal to range end") + "."))
    else if (eventType == null)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_eventTypeNotSelected",
        "No event type has been selected") + "."))
    else if (periodicity == null)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_periodicityNotSelected",
        "No periodicity has been selected") + "."))
    else if (duration == null)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_durationNotSelected",
        "No duration has been selected") + "."))
    else
      checkSubjectCreationWarnings
  }
}



class CreateSubjectLikeFormController[
  C <: CourseLike,
  R <: ResourceLike,
]( courses: Iterable[C],
   resources: Iterable[R],
   formInitializer: Option[SubjectLikeFormInitializer[C,Nothing]] = None,
) extends SubjectLikeFormController[SubjectLikeForm[C,R]#SD,C,R,Nothing](
    courses, resources, formInitializer) {

  override protected def initializeContentLanguage(): Unit = {
    super.initializeContentLanguage()

    finishFormButton.setText(AppSettings.language.getItemOrElse(
      "subjectForm_createSubjectButton",
      "Create Subject"))
  }

  override protected def bindActions(): Unit = {
    super.bindActions()

    finishFormButton.setOnAction(actionEvent => {
      if (!warnings) {
        formResult = Some(createSubject)
        close()
      }
      actionEvent.consume()
    })
  }

  //pre: no warnings
  private def createSubject: SD = {
    val subjectDescriptor = new SD

    subjectDescriptor.name = subjectNameField.getText
    subjectDescriptor.shortName = subjectShortNameField.getText
    subjectDescriptor.description = subjectDescriptionField.getText
    subjectDescriptor.color = Some(new Color(subjectColorPicker.getValue))
    subjectDescriptor.course = Some(subjectCoursePicker.getValue)
    subjectDescriptor.quarter = Some(subjectQuarterPicker.getValue)

    // Classify events by type to ease event incompatibility generation.
    val eventsByType: mutable.Map[EventType, mutable.Set[ED]] = new mutable.HashMap
    EventTypes.commonEventTypes.foreach(eventsByType.put(_, new mutable.HashSet))

    //getting the event descriptors and adding them to the subject descriptor and event type mapping
    eventTable.getItems.forEach(tableElement => if (tableElement.isLeft) {
      val ed = tableElement.left.get

      subjectDescriptor.events.add(ed)
      //e.subject = subject
      ed.course = subjectDescriptor.course
      ed.quarter = subjectDescriptor.quarter

      eventsByType(ed.eventType) += ed
    })

    val actualEventTypeIncompatibilities = actualTypeIncompatibilities
    subjectDescriptor.eventTypeIncompatibilities ++= actualEventTypeIncompatibilities

    //TODO remove this
    actualEventTypeIncompatibilities.foreach(eti => {
      eventsByType(eti.getFirstType).foreach(e1 => {
        eventsByType(eti.getSecondType).foreach(e2 => {
          e1.incompatibilities.add(e2)
          e2.incompatibilities.add(e1) //only for completeness, this is redundant job
        })
      })
    })

    subjectDescriptor
  }

  private def actualTypeIncompatibilities: Iterable[EventTypeIncompatibility] =
    if (formInitializer.nonEmpty && formInitializer.get.typeIncompatibilities.nonEmpty)
      formInitializer.get.typeIncompatibilities.get.toSet --
        removedEventTypeIncompatibilities ++
        addedEventTypeIncompatibilities
    else {
      addedEventTypeIncompatibilities
    }
}

class ShowSubjectLikeInformationController[
  S <: SubjectLike[S,C,R,E],
  C <: CourseLike,
  R <: ResourceLike,
  E <: EventLike[S,C,R,E]
](subjectLike: S,
  owner: Option[Window]
) extends SubjectLikeFormController[Nothing, C, R, E](
  if(subjectLike.course.nonEmpty) List(subjectLike.course.get) else Nil,
  Nil,
  Some(SubjectLikeFormInitializer.fromSubjectLike(subjectLike)))
    with SelfInitializedStageController {

  def this(subjectLike: S, owner: Window) =
    this(subjectLike, Some(owner))

  override protected def selfInitialize(): Unit = {
    initializeWith(
      StageSettings(
        AppSettings.language.getItemOrElse("subjectForm_show_windowTitle", "Subject details"),
        owner,
        Modality.WINDOW_MODAL),
      FXMLPaths.SubjectForm
    )
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    super.initialize(url, resourceBundle)
    lockFields()
  }

  private def lockFields(): Unit = {
    subjectNameField.setEditable(false)
    subjectShortNameField.setEditable(false)
    subjectDescriptionField.setEditable(false)
    subjectColorPicker.setEditable(false)
    subjectQuarterPicker.getItems.retainAll(subjectQuarterPicker.getSelectionModel.getSelectedItem)
    manageEventTypeIncompatibilitiesButton.setDisable(true)
    deleteSelectedEventsButton.setDisable(true)
    deleteAllEventsButton.setDisable(true)
  }

  override def initializeContentLanguage(): Unit = {
    super.initializeContentLanguage()

    finishFormButton.setText(AppSettings.language.getItemOrElse(
      "subjectForm_closeWindowButton",
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

class EditSubjectLikeFormController[
  S <: SubjectLike[S,C,R,E],
  C <: CourseLike,
  R <: ResourceLike,
  E <: EventLike[S,C,R,E]
](
  subject: S,
  courses: Iterable[C],
  resources: Iterable[R]
) extends SubjectLikeFormController[(S,Iterable[SubjectLikeForm[C,R]#ED],Iterable[E]),C,R,E](courses, resources,
    Some(SubjectLikeFormInitializer.fromSubjectLike(subject))) {

  object EditInformation {
    var nameFieldChanged: Boolean = false
    var shortNameFieldChanged: Boolean = false
    var descriptionFieldChanged: Boolean = false
    var colorPickerSelectionChanged: Boolean = false
    var coursePickerSelectionChanged: Boolean = false
    var quarterPickerSelectionChanged: Boolean = false
    var eventTypeIncompatibilitiesChanged: Boolean = false
    var eventDescriptorsHaveBeenAdded: Boolean = false
    var eventsHaveBeenRemoved: Boolean = false

    def changed: Boolean =
      nameFieldChanged                  ||
      shortNameFieldChanged             ||
      descriptionFieldChanged           ||
      colorPickerSelectionChanged       ||
      coursePickerSelectionChanged      ||
      quarterPickerSelectionChanged     ||
      eventTypeIncompatibilitiesChanged ||
      eventDescriptorsHaveBeenAdded     ||
      eventsHaveBeenRemoved
  }

  override protected def initializeContentLanguage(): Unit = {
    super.initializeContentLanguage()

    finishFormButton.setText(AppSettings.language.getItemOrElse(
      "subjectForm_editSubjectButton",
      "Edit Subject"))
  }

  override protected def bindActions(): Unit = {
    super.bindActions()

    bindChangeReporters()

    finishFormButton.setOnAction(actionEvent => {
      if (!warnings) { //edit subject from form fields
        formResult = modifyEntity(subject)
        close()
      }
      actionEvent.consume()
    })
  }

  private def bindChangeReporters(): Unit = {
    subjectNameField.textProperty().addListener(_ => {
      EditInformation.nameFieldChanged = true
    })

    subjectShortNameField.textProperty().addListener(_ => {
      EditInformation.shortNameFieldChanged = true
    })

    subjectDescriptionField.textProperty().addListener(_ => {
      EditInformation.descriptionFieldChanged = true
    })

    subjectColorPicker.valueProperty().addListener(_ => {
      EditInformation.colorPickerSelectionChanged = true
    })

    subjectCoursePicker.getSelectionModel.selectedItemProperty().addListener(_ => {
      EditInformation.coursePickerSelectionChanged = true
    })

    subjectQuarterPicker.getSelectionModel.selectedItemProperty().addListener(_ => {
      EditInformation.coursePickerSelectionChanged = true
    })

    manageEventTypeIncompatibilitiesButton.setOnAction(actionEvent => {
      val preAddIncompCount = addedEventTypeIncompatibilities.size //Number of added incompatibilities before handling
      val preRemIncompCount = removedEventTypeIncompatibilities.size //Number of removed incompatibilities before handling

      manageEventTypeIncompatibilities()

      if (preAddIncompCount != addedEventTypeIncompatibilities.size ||
          preRemIncompCount != removedEventTypeIncompatibilities.size) {
        EditInformation.eventTypeIncompatibilitiesChanged = true
      }

      actionEvent.consume()
    })

    //Addition and removal of events is handled by overriding method behavior and not by binding listeners.
  }

  override protected def addEventDescriptors(eventDescriptors: Iterable[ED]): Unit = {
    super.addEventDescriptors(eventDescriptors)
    if(eventDescriptors.nonEmpty) EditInformation.eventDescriptorsHaveBeenAdded = true
  }

  override protected def deleteSubjectEvents(selectedItems: util.Collection[Either[ED, E]]): Unit = {
    if(!selectedItems.isEmpty) EditInformation.eventsHaveBeenRemoved = true
    super.deleteSubjectEvents(selectedItems)
  }

  //pre: no warnings
  // returns (s, addedEventDescriptors, removedEvents)
  private def modifyEntity(s: S): Option[(S,Iterable[ED],Iterable[E])] = {
    if (EditInformation.changed) {
      if (EditInformation.nameFieldChanged) s.name = subjectNameField.getText.trim
      if (EditInformation.shortNameFieldChanged) s.shortName = subjectShortNameField.getText.trim
      if (EditInformation.descriptionFieldChanged) s.description = subjectDescriptionField.getText.trim
      if (EditInformation.colorPickerSelectionChanged) s.color = Color(subjectColorPicker.getValue)
      if (EditInformation.coursePickerSelectionChanged) s.course = Some(subjectCoursePicker.getValue)
      if (EditInformation.quarterPickerSelectionChanged) s.quarter = Some(subjectQuarterPicker.getValue)

      if (EditInformation.eventsHaveBeenRemoved) {
        removedEvents.foreach(e => e.incompatibilities.foreach(_.removeIncompatibility(e)))
        subject.events_--=(removedEvents)
      }

      if (EditInformation.eventTypeIncompatibilitiesChanged) {
        removedEventTypeIncompatibilities.foreach(s.eventTypeIncompatibilities_-=)
        addedEventTypeIncompatibilities.foreach(s.eventTypeIncompatibilities_+=)

        val eventsByType = s.events.groupBy(_.eventType)

        addedEventTypeIncompatibilities.foreach(eti =>
          eventsByType.getOrElse(eti.getFirstType, Nil).foreach(e1 =>
            eventsByType.getOrElse(eti.getSecondType, Nil).foreach(e2 =>
              e1.addIncompatibility(e2))))

        //FIXME we cannot know if this incompatibility was created as a discrete event incompatibility or event type incompatibility
        removedEventTypeIncompatibilities.foreach(eti =>
          eventsByType.getOrElse(eti.getFirstType, Nil).foreach(
            e1 => eventsByType.getOrElse(eti.getSecondType, Nil).foreach(e2 =>
              e1.removeIncompatibility(e2))))
      }

      Some((
        s,
        if(EditInformation.eventDescriptorsHaveBeenAdded) addedEventDescriptors else Nil,
        if(EditInformation.eventsHaveBeenRemoved) removedEvents else Nil))
    }
    else
      None
  }

}
