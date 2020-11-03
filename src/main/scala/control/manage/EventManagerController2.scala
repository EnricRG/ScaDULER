package control.manage

import app.{AppSettings, FXMLPaths}
import control.form.{CreateEventLikeFormController, EditEventLikeFormController, ShowEventLikeInformationController}
import control.misc.{NameListPrompt, RemoveMode, SoftRemove}
import control.{MainController, SelfInitializedStageController, StageSettings}
import factory.ViewFactory
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control._
import javafx.scene.layout.HBox
import javafx.scene.layout.Region.USE_COMPUTED_SIZE
import javafx.stage.Modality
import misc.Duration
import model.descriptor.EventDescriptor
import model.{Course, Event, Resource, Subject}
import service.AppDatabase
import util.Utils

class EventManagerController2(
  events: Iterable[Event],
  mainController: MainController,
  appDatabase: AppDatabase
) extends EntityManagerController2[Event]
  with SelfInitializedStageController {

  type ED = EventDescriptor[Subject, Course, Resource, Event]

  @FXML protected var nameColumn: TableColumn[Event, String] = _
  @FXML protected var shortNameColumn: TableColumn[Event, String] = _
  @FXML protected var subjectColumn: TableColumn[Event, String] = _
  @FXML protected var resourceColumn: TableColumn[Event, String] = _
  @FXML protected var periodicityColumn: TableColumn[Event, String] = _
  @FXML protected var weekColumn: TableColumn[Event, String] = _
  @FXML protected var durationColumn: TableColumn[Event, String] = _
  @FXML protected var incompatibilitiesColumn: TableColumn[Event, Int] = _
  @FXML protected var detailsColumn: TableColumn[Event, Null] = _

  def this(mainController: MainController, appDatabase: AppDatabase) =
    this(Nil, mainController, appDatabase)

  override def selfInitialize(): Unit =
    initializeWith(
      StageSettings(
        AppSettings.language.getItemOrElse("eventManager_windowTitle", "Manage Events"),
        Some(mainController.getWindow),
        Modality.WINDOW_MODAL),
      FXMLPaths.EntityManagerPanel)

  override protected def initializeContentLanguage(): Unit = {
    table.setPlaceholder(new Label(AppSettings.language.getItemOrElse(
      "eventListPlaceholder", "No events")))

    nameColumn.setText(AppSettings.language.getItemOrElse(
      "eventManager_nameColumnHeader",
      "Name"))

    shortNameColumn.setText(AppSettings.language.getItemOrElse(
      "eventManager_shortNameColumnHeader",
      "Short Name"))

    subjectColumn.setText(AppSettings.language.getItemOrElse(
      "eventManager_subjectColumnHeader",
      "Subject"))

    resourceColumn.setText(AppSettings.language.getItemOrElse(
      "eventManager_resourceColumnHeader",
      "Resource"))

    periodicityColumn.setText(AppSettings.language.getItemOrElse(
      "eventManager_periodicityColumnHeader",
      "Periodicity"))

    weekColumn.setText(AppSettings.language.getItemOrElse(
      "eventManager_weekColumnHeader",
      "Week"))

    durationColumn.setText(AppSettings.language.getItemOrElse(
      "eventManager_durationColumnHeader",
      "Duration"))

    incompatibilitiesColumn.setText(AppSettings.language.getItemOrElse(
      "eventManager_incompatibilitiesColumnHeader",
      "Incompatibilities"))

    detailsColumn.setText(AppSettings.language.getItemOrElse(
      "eventManager_detailsColumnHeader",
      "Details"))

    addButton.setText(AppSettings.language.getItemOrElse(
      "eventManager_addEventButton",
      "Add Event"))

    editButton.setText(AppSettings.language.getItemOrElse(
      "eventManager_editEventButton",
      "Edit Event"))

    removeButton.setText(AppSettings.language.getItemOrElse(
      "eventManager_removeEventButton",
      "Remove Event"))
  }

  override protected def additionalTableSetup(): Unit = {
    addColumns()
    configureColumns()
    fillTable(events)
  }

  private def addColumns(): Unit = {
    nameColumn = new TableColumn
    shortNameColumn = new TableColumn
    subjectColumn = new TableColumn
    resourceColumn = new TableColumn
    periodicityColumn = new TableColumn
    weekColumn = new TableColumn
    durationColumn = new TableColumn
    incompatibilitiesColumn = new TableColumn
    detailsColumn = new TableColumn

    addColumn(nameColumn)
    addColumn(shortNameColumn)
    addColumn(subjectColumn)
    addColumn(resourceColumn)
    addColumn(periodicityColumn)
    addColumn(weekColumn)
    addColumn(durationColumn)
    addColumn(incompatibilitiesColumn)
    addColumn(detailsColumn)
  }

  private def configureColumns(): Unit = {
    nameColumn.setCellValueFactory(cell => new SimpleStringProperty(cell.getValue.name))

    shortNameColumn.setCellValueFactory(cell => new SimpleStringProperty(cell.getValue.shortName))

    subjectColumn.setCellValueFactory(cell =>
      if(cell.getValue.subject.nonEmpty)
        new SimpleStringProperty(cell.getValue.subject.get.name)
      else
        new SimpleStringProperty())

    resourceColumn.setCellValueFactory(cell =>
      if(cell.getValue.neededResource.nonEmpty)
        new SimpleStringProperty(cell.getValue.neededResource.get.name)
      else
        new SimpleStringProperty())

    periodicityColumn.setCellValueFactory(cell =>
      new SimpleStringProperty(cell.getValue.periodicity.toString))

    weekColumn.setCellValueFactory(cell =>
      if(cell.getValue.week.nonEmpty)
        new SimpleStringProperty(cell.getValue.week.get.toString)
      else
        new SimpleStringProperty())

    durationColumn.setCellValueFactory(cell =>
      new SimpleStringProperty(Duration.asPrettyString(cell.getValue.duration)))

    incompatibilitiesColumn.setCellFactory(_ => new TableCell[Event, Int] {
      override protected def updateItem(item: Int, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (!empty) {
          val incompatibilities = getTableView.getItems.get(getIndex).incompatibilities
          setGraphic(generateEventsHyperlink(incompatibilities, AppSettings.language.getItemOrElse(
            "eventManager_incompatibilitiesListPromp",
            "Incompatibilities")))
        }
        else {
          setGraphic(null)
          setText(null)
        }
      }
    })

    detailsColumn.setCellFactory(_ => new TableCell[Event, Null] {
      override protected def updateItem(item: Null, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (!empty) {
          setGraphic(generateDetailsButton(getTableView.getItems.get(getIndex)))
        }
        else {
          setGraphic(null)
          setText(null)
        }
      }
    })

    detailsColumn.setMinWidth(55)
  }

  private def generateEventsHyperlink(events: Iterable[Event], windowTitle: String): Node = {
    val hyperlink = new Hyperlink(events.size.toString)
    hyperlink.setOnAction(_ => showEventList(events, windowTitle))

    val tooltip = new Tooltip(AppSettings.language.getItemOrElse(
      "eventManager_incompatibilitiesHyperlinkTooltip",
      "Click to see the complete list of this event's incompatibilities"))
    hyperlink.setTooltip(tooltip)

    val hBox: HBox = generateHyperlinkHBox
    hBox.getChildren.add(hyperlink)

    hBox
  }

  private def generateHyperlinkHBox: HBox = {
    val hBox: HBox = new HBox

    hBox.setAlignment(Pos.CENTER)
    hBox.setMaxWidth(USE_COMPUTED_SIZE)
    hBox.setMaxHeight(USE_COMPUTED_SIZE)

    hBox
  }

  private def showEventList(events: Iterable[Event], windowTitle: String): Unit = {
    val prompt = new NameListPrompt(
      events.map(_.shortName),
      AppSettings.language.getItemOrElse("eventManager_incompatibilitiesListPlaceholder", "No events"),
      StageSettings(
        windowTitle,
        Some(stage),
        Modality.WINDOW_MODAL))

    prompt.showAndWait()
  }

  private def generateDetailsButton(event: Event): HBox = {
    val hBox = generateHyperlinkHBox

    val button = new Button(AppSettings.language.getItemOrElse(
      "eventManager_detailsButton",
      "show more..."))
    button.setOnAction(actionEvent => {
      promptEventInformation(event)
      actionEvent.consume()
    })
    button.setMaxWidth(USE_COMPUTED_SIZE)

    hBox.getChildren.add(button)

    hBox
  }

  override protected def newEntity: Option[Event] = {
    val formResult = promptNewEventForm

    if(formResult.nonEmpty)
      Some(createEventFromDescriptor(formResult.get))
    else
      None
  }

  private def promptNewEventForm: Option[ED] = {
    val eventForm = new CreateEventLikeFormController(
      appDatabase.subjects,
      appDatabase.courses,
      appDatabase.resources,
      appDatabase.events)

    eventForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("eventForm_windowTitle", "New Event"),
      addButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.EventForm),
      eventForm))

    eventForm.waitFormResult
  }

  private def createEventFromDescriptor(eventDescriptor: ED): Event = {
    val event = appDatabase.createEventFromDescriptor(eventDescriptor)._2

    mainController.notifyEventCreation(event)

    event
  }

  override protected def editEntity(entity: Event): Option[Event] = {
    val editedEvent = promptEditEventForm(entity)

    if(editedEvent.nonEmpty)
      mainController.notifyEventEdition(editedEvent.get)

    editedEvent
  }

  private def promptEditEventForm(event: Event): Option[Event] = {
    val eventForm = new EditEventLikeFormController(
      appDatabase.subjects,
      appDatabase.courses,
      appDatabase.resources,
      appDatabase.events,
      event)

    eventForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("eventForm_edit_windowTitle", "Edit Event"),
      stage,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.EventForm),
      eventForm))

    //This is fine because EditEventLikeFormController(eventLike) specification ensures that
    //if the form result is Some(x), x == event, and that's what we want.
    eventForm.waitFormResult //execution thread stops here.
  }

  override protected def removeEntity(entity: Event, removeMode: RemoveMode): Unit = {
    appDatabase.removeEvent(entity)
    mainController.notifyEventDeletion(entity)
  }

  override protected def askRemoveMode: Option[RemoveMode] =
    Some(SoftRemove)

  protected def promptEventInformation(event: Event): Unit = {
    val eventDetails = new ShowEventLikeInformationController[Subject, Course, Resource, Event](event)

    eventDetails.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("eventForm_show_windowTitle", "Event details"),
      stage,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.EventForm),
      eventDetails))

    eventDetails.showAndWait()
  }

  override protected def notifySingleSelection(): Unit = {
    removeButton.setText(AppSettings.language.getItemOrElse(
      "eventManager_removeEventButton",
      "Remove Event"))
  }

  override protected def notifyMultipleSelection(): Unit = {
    removeButton.setText(AppSettings.language.getItemOrElse(
      "eventManager_removeEventsButton",
      "Remove Events"))
  }
}
