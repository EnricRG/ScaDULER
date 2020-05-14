package control.form

import app.{AppSettings, FXMLPaths}
import control.StageController
import factory.ViewFactory
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.stage.{Modality, Stage}
import misc.{Duration, EventTypeIncompatibility, Warning}
import model.Weeks.Periodicity
import model._
import util.Utils
import java.util

import scala.collection.mutable.ArrayBuffer
import scala.collection.{JavaConverters, mutable}

object SubjectFormController2{
  type C = Course
  type R = Resource
  type SD = SubjectDescriptor[SubjectDescriptor[Nothing,C,R,ED], C, R, ED]
  type ED = EventDescriptor2[Nothing,C,R]
}

class SubjectFormController2(
  courses: Iterable[Course],
  resources: Iterable[Resource]) extends AbstractSubjectFormController[
    SubjectFormController2.C,
    SubjectFormController2.R,
    SubjectFormController2.SD,
    SubjectFormController2.ED
  ](courses, resources) {

  def newEventDescriptor: SubjectFormController2.ED = {
    new EventDescriptor2
  }

  def newSubjectDescriptor: SubjectFormController2.SD = {
    new SubjectDescriptor
  }
}

abstract class AbstractSubjectFormController[
  C <: CourseLike,
  R <: ResourceLike,
  SD <: SubjectDescriptor[SD,C,R,EventDescriptor2[SD,C,R]],
  ED <: EventDescriptor2[SD,C,R]](
  courses: Iterable[C],
  resources: Iterable[R])
  extends FormController2[SD] {

  def newEventDescriptor: ED
  def newSubjectDescriptor: SD

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

  @FXML var eventTable: TableView[ED] = _
  @FXML var eventTable_nameColumn: TableColumn[ED, String] = _
  @FXML var eventTable_resourceColumn: TableColumn[ED, String] = _

  @FXML var deleteSelectedEventsButton: Button = _
  @FXML var deleteAllEventsButton: Button = _

  @FXML var createSubjectButton: Button = _

  private var subjectBlueprint: Option[SD] = None
  private val eventTypeIncompatibilities: util.Collection[EventTypeIncompatibility] = new util.HashSet[EventTypeIncompatibility]

  def this(courses: Iterable[C], resources: Iterable[R], stage: Stage) {
    this(courses, resources)
    setStage(stage)
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

    createSubjectButton.setText(AppSettings.language.getItemOrElse(
      "subjectForm_createSubjectButton",
      "Create Subject"))
  }

  override protected def setupViews(): Unit = {
    subjectCoursePicker.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(courses)))
    subjectCoursePicker.setCellFactory(param => new ListCell[C] {
      override protected def updateItem(item: C, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null) setGraphic(null)
        else setText(item.name)
      }
    })

    subjectQuarterPicker.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(Quarters.quarters)))

    generateEvents_eventTypeSelector.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(EventTypes.commonEventTypes)))

    generateEvents_durationSelector.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(Duration.getDurations)))

    generateEvents_durationSelector.setCellFactory(param => new ListCell[Duration] {
      override protected def updateItem(item: Duration, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null) setGraphic(null)
        else setText(item.toString)
      }
    })

    generateEvents_periodicitySelector.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(Weeks.periodicityList)))

    selectResourceListView.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
    selectResourceListView.setCellFactory(param => new ListCell[R] {
      override protected def updateItem(resource: R, empty: Boolean): Unit = {
        super.updateItem(resource, empty)
        if (empty || resource == null) setText(null)
        else setText(resource.getName)
      }
    })

    selectResourceListView.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(resources)))

    eventTable.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
    eventTable_nameColumn.setCellValueFactory((cell: TableColumn.CellDataFeatures[ED, String]) =>
      new SimpleStringProperty(cell.getValue.name))
    eventTable_resourceColumn.setCellValueFactory((cell: TableColumn.CellDataFeatures[ED, String]) =>
      if(cell.getValue.neededResource.nonEmpty)
        new SimpleStringProperty(cell.getValue.neededResource.get.name)
      else new SimpleStringProperty()
    )
  }

  override protected def bindActions(): Unit = {
    subjectNameField.textProperty.addListener((observable, oldValue, newValue) => {
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

    selectResourceSearchBar.textProperty.addListener((observable, oldValue, newValue) => {
      filterResourceList(selectResourceSearchBar.getText)
    })

    generateEventsButton.setOnAction(actionEvent => {
      generateEvents(
        subjectNameField.getText,
        subjectShortNameField.getText,
        generateEvents_eventTypeSelector.getValue,
        getRangeLowerBound,
        getRangeUpperBound,
        generateEvents_periodicitySelector.getSelectionModel.getSelectedItem,
        generateEvents_durationSelector.getSelectionModel.getSelectedItem,
        selectResourceListView.getSelectionModel.getSelectedItem)
      actionEvent.consume()
    })

    manageEventTypeIncompatibilitiesButton.setOnAction(actionEvent => {
      val incompatibilityFormController: StageController =
        new SubjectEventIncompatibilityFormController(eventTypeIncompatibilities)

      incompatibilityFormController.setStage(Utils.promptBoundWindow(
        AppSettings.language.getItemOrElse(
          "eventForm_manageIncompatibilities",
          "Manage Incompatibilities"),
        manageEventTypeIncompatibilitiesButton.getScene.getWindow,
        Modality.WINDOW_MODAL,
        new ViewFactory[StageController](FXMLPaths.SubjectIncompatibilityForm),
        incompatibilityFormController))

      incompatibilityFormController.show()
    })

    deleteSelectedEventsButton.setOnAction(actionEvent => {
      deleteSubjectEvents(eventTable.getSelectionModel.getSelectedItems)
      actionEvent.consume()
    })

    deleteAllEventsButton.setOnAction(actionEvent => {
      deleteSubjectEvents(eventTable.getItems)
      actionEvent.consume()
    })

    createSubjectButton.setOnAction(actionEvent => {
      if (!warnings) {
        subjectBlueprint = Some(createSubject)
        close()
      }
      actionEvent.consume()
    })
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

  private def deleteSubjectEvents(selectedItems: util.Collection[ED]): Unit = {
    eventTable.getItems.removeAll(selectedItems)
  }

  //pre filter not null
  private def filterResourceList(filter: String): Unit = if (filter.trim.nonEmpty) {
    selectResourceListView.setItems(
      FXCollections.observableArrayList(JavaConverters.asJavaCollection(
        resources.filter(_.getName.toLowerCase.contains(filter.trim.toLowerCase)))))
  }

  private def getNumberFromField(textField: TextField): Int = {
    val number: Int = if (!textField.getText.trim.isEmpty) {
      try
        textField.getText.toInt
      catch {
        case npe: NumberFormatException =>
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

  private def getRangeLowerBound: Int = getNumberFromField(generateEvents_rangeLowerBound)

  private def getRangeUpperBound: Int = getNumberFromField(generateEvents_rangeUpperBound)

  private def generateEvents(subjectName: String,
                             subjectShortName: String,
                             eventType: EventType,
                             rangeStart: Int, rangeEnd: Int,
                             periodicity: Periodicity, duration: Duration,
                             neededResource: R
                            ): Unit = {
    if (!warnings(checkEventGenerationWarnings(eventType, rangeStart, rangeEnd, periodicity, duration, neededResource)))
      for (i <- rangeStart to rangeEnd) {
        val event = newEventDescriptor

        event.name = "%s (%s-%d) (%s)".format(subjectName, eventType.toString, i, periodicity.toShortString)
        event.shortName = "%s (%s %d) (%s)".format(subjectShortName, eventType.toShortString, i, periodicity.toShortString)
        event.eventType = eventType
        event.neededResource = neededResource
        event.periodicity = periodicity
        event.duration = duration.toInt

        eventTable.getItems.add(event)
      }
  }

  private def createSubject: SD = {
    val subject = newSubjectDescriptor

    subject.name = subjectNameField.getText
    subject.shortName = subjectShortNameField.getText
    subject.description = subjectDescriptionField.getText
    subject.color = new Color(subjectColorPicker.getValue)
    subject.course = subjectCoursePicker.getValue
    subject.quarter = subjectQuarterPicker.getValue

    val eventsByType: mutable.Map[EventType, ArrayBuffer[ED]] = new mutable.HashMap
    EventTypes.commonEventTypes.foreach(eventsByType.put(_, new ArrayBuffer))

    eventTable.getItems.forEach(e => {
      subject.events_+=(e)
      e.subject = subject
      e.course = subject.course
      e.quarter = subject.quarter
      eventsByType(e.eventType) += e
      //TODO remove getMainController().addUnassignedEvent(e);
    })

    eventTypeIncompatibilities.forEach(eti => {
      eventsByType(eti.getFirstType).foreach(e1 => {
        eventsByType(eti.getSecondType).foreach(e2 => {
          e1.addIncompatibility(e2)
        })
      })
    })

    subject
  }

  override protected def checkWarnings: Option[Warning] = checkSubjectCreationWarnings

  override def waitFormResult: Option[SD] = {
    showAndWait()
    subjectBlueprint
  }

  //TODO update to Warning subtyping model
  private def checkSubjectCreationWarnings: Option[Warning] = {
    if (subjectNameField.getText.trim.isEmpty)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_subjectNameCannotBeEmpty",
        "Subject Name cannot be empty") + "."))
    else if (subjectShortNameField.getText.trim.isEmpty)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_subjectShortNameCannotBeEmpty",
        "Subject Short Name cannot be empty") + "."))
    else
      None
  }

  //TODO update to Warning subtyping model
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
