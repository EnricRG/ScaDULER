package control.imprt

import app.{AppSettings, FXMLPaths}
import control.StageController
import control.form.{CreateEventFormController, EditEventLikeFormController}
import factory.ViewFactory
import file.imprt.MutableImportJob
import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn}
import javafx.stage.Modality
import misc.Duration
import model.blueprint.EventBlueprint
import util.Utils

class ImportEventsManagerController( importJobEditorController: ImportJobEditorController,
                                     editableImportJob: MutableImportJob             )
  extends ImportEntityManagerController[EventBlueprint]{

  @FXML var shortNameColumn: TableColumn[EventBlueprint, String] = _
  @FXML var typeColumn: TableColumn[EventBlueprint, String] = _
  @FXML var courseColumn: TableColumn[EventBlueprint, String] = _
  @FXML var quarterColumn: TableColumn[EventBlueprint, String] = _
  @FXML var durationColumn: TableColumn[EventBlueprint, String] = _
  @FXML var periodicityColumn: TableColumn[EventBlueprint, String] = _
  @FXML var incompatibilitiesColumn: TableColumn[EventBlueprint, Int] = _

  private val detailsController: ImportEventDetailsController = {
    val controller = new ImportEventDetailsController
    Utils.loadScene(new ViewFactory(FXMLPaths.ImportEventDetailsView), controller)
    controller
  }

  override def additionalInitialization(): Unit = {
    detailBoxContent_=(detailsController.mainBox)

    shortNameColumn = new TableColumn()
    typeColumn = new TableColumn()
    courseColumn = new TableColumn()
    quarterColumn = new TableColumn()
    durationColumn = new TableColumn()
    periodicityColumn = new TableColumn()
    incompatibilitiesColumn = new TableColumn()
  }

  override def initializeContentLanguage(): Unit = {
    newButton.setText(language.getItemOrElse("import_event_newButton", "New Event"))
    editButton.setText(language.getItemOrElse("import_event_editButton", "Edit Event"))
    deleteButton.setText(language.getItemOrElse("import_event_singleDeleteButton", "Delete Event"))

    table.setPlaceholder(new Label(language.getItemOrElse("import_event_tablePlaceholder", "No events")))

    shortNameColumn.setText(language.getItemOrElse("import_event_shortNameColumn", "Short"))
    typeColumn.setText(language.getItemOrElse("import_event_typeColumn", "Type"))
    courseColumn.setText(language.getItemOrElse("import_event_courseColumn", "Course"))
    quarterColumn.setText(language.getItemOrElse("import_event_quarterColumn", "Quarter"))
    durationColumn.setText(language.getItemOrElse("import_event_durationColumn", "Duration"))
    periodicityColumn.setText(language.getItemOrElse("import_event_periodicityColumn", "Periodicity"))
    incompatibilitiesColumn.setText(language.getItemOrElse("import_event_incompatibilitiesColumn", "Nº of Incompat."))
  }

  override def additionalTableSetup(): Unit = {
    shortNameColumn.setCellValueFactory(cell => {
      if (cell.getValue != null)
        new SimpleStringProperty(cell.getValue.shortName)
      else
        new SimpleStringProperty()
    })

    typeColumn.setCellValueFactory(cell => {
      if (cell.getValue != null)
        new SimpleStringProperty(cell.getValue.eventType.toString)
      else
        new SimpleStringProperty()
    })

    courseColumn.setCellValueFactory(cell => {
      if (cell.getValue != null && cell.getValue.course.nonEmpty)
        new SimpleStringProperty(cell.getValue.course.get.name)
      else
        new SimpleStringProperty()
    })

    quarterColumn.setCellValueFactory(cell => {
      if (cell.getValue != null && cell.getValue.quarter.nonEmpty)
        new SimpleStringProperty(cell.getValue.quarter.get.toShortString)
      else
        new SimpleStringProperty()
    })

    durationColumn.setCellValueFactory(cell => {
      if (cell.getValue != null)
        new SimpleStringProperty(Duration.asPrettyString(cell.getValue.duration))
      else
        new SimpleStringProperty()
    })

    periodicityColumn.setCellValueFactory(cell => {
      if (cell.getValue != null)
        new SimpleStringProperty(cell.getValue.periodicity.toString)
      else
        new SimpleStringProperty()
    })

    incompatibilitiesColumn.setCellValueFactory(cell => {
      val cellValue =
        if (cell.getValue != null)
          new SimpleIntegerProperty(cell.getValue.incompatibilities.size)
        else
          new SimpleIntegerProperty()

      cellValue.asInstanceOf[ObservableValue[Int]]
    })

    addColumn(shortNameColumn)
    addColumn(typeColumn)
    addColumn(courseColumn)
    addColumn(quarterColumn)
    addColumn(durationColumn)
    addColumn(periodicityColumn)
    addColumn(incompatibilitiesColumn)

    addContent(editableImportJob.events)

    table.getSortOrder.add(shortNameColumn.asInstanceOf[TableColumn[EventBlueprint, _]])
  }

  override def newEntity: Option[EventBlueprint] = {
    val neb = promptNewEventForm

    if(neb.nonEmpty) importJobEditorController.notifyEventCreation(neb.get)

    neb
  }

  private def promptNewEventForm: Option[EventBlueprint] = {
    val eventForm = new CreateEventFormController(
      None,
      editableImportJob.subjects,
      editableImportJob.courses,
      editableImportJob.resources,
      editableImportJob.events)

    eventForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("eventForm_create_windowTitle", "Create new Event"),
      newButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[StageController](FXMLPaths.EventForm),
      eventForm))

    val oed = eventForm.waitFormResult

    if(oed.nonEmpty) {
      Some(EventBlueprint.fromDescriptor(oed.get))
    }
    else
      None
  }

  override def editEntity(entity: EventBlueprint): Option[EventBlueprint] = {
    promptEditEventForm(entity)
  }

  private def promptEditEventForm(entity: EventBlueprint): Option[EventBlueprint] = {
    val eventForm = new EditEventLikeFormController(
      entity,
      editableImportJob.subjects,
      editableImportJob.courses,
      editableImportJob.resources,
      editableImportJob.events)

    eventForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("eventForm_edit_windowTitle", "Edit Event"),
      newButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[StageController](FXMLPaths.EventForm),
      eventForm))

    //This is fine because EditEventLikeFormController(eventLike) specification ensures that
    //if the form result is Some(x), x == event, and that's what we want.
    eventForm.waitFormResult //execution thread stops here.
  }

  override def deleteEntity(entity: EventBlueprint): Unit = {
    importJobEditorController.notifyEventDeletion(entity)
  }

  override def showAdditionalInformation(entity: EventBlueprint): Unit = {
    detailsController.setFromEventBlueprint(entity)
    showDetailBox()
  }

  override def clearAdditionalInformation(): Unit = {
    detailsController.clear()
  }

  override protected def notifySingleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_event_singleDeleteButton", "Delete Event"))
  }

  override protected def notifyMultipleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_event_multipleDeleteButton", "Delete Events"))
  }
}

