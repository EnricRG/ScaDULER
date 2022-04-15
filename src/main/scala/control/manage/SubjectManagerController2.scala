package control.manage

import app.{AppSettings, FXMLPaths}
import control.form.{CreateSubjectLikeFormController, ShowSubjectLikeInformationController, SubjectLikeForm}
import control.misc.{HardRemove, NameListPrompt, RemoveMode}
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
import javafx.util.Callback
import model.descriptor.{EventDescriptor, SubjectDescriptor}
import model._
import service.AppDatabase
import util.Utils

import scala.collection.mutable

class SubjectManagerController2(
  subjects: Iterable[Subject],
  mainController: MainController,
  appDatabase: AppDatabase
) extends EntityManagerController2[Subject]
  with SelfInitializedStageController {

  type SD = SubjectLikeForm[Course,Resource]#SD

  @FXML protected var nameColumn = new TableColumn[Subject, String]
  @FXML protected var shortNameColumn = new TableColumn[Subject, String]
  @FXML protected var theoryEventsColumn = new TableColumn[Subject, String]
  @FXML protected var problemsEventsColumn = new TableColumn[Subject, String]
  @FXML protected var laboratoryEventsColumn = new TableColumn[Subject, String]
  @FXML protected var computerEventsColumn = new TableColumn[Subject, String]
  @FXML protected var detailsColumn = new TableColumn[Subject, Null]

  def this(mainController: MainController, appDatabase: AppDatabase) =
    this(Nil, mainController, appDatabase)

  override def selfInitialize(): Unit =
    initializeWith(
      StageSettings(
        AppSettings.language.getItemOrElse("subjectManager_windowTitle", "Manage subjects"),
        Some(mainController.getWindow),
        Modality.WINDOW_MODAL),
      FXMLPaths.EntityManagerPanel)

  override protected def initializeContentLanguage(): Unit = {
    table.setPlaceholder(new Label(AppSettings.language.getItemOrElse(
      "subjectTable_placeholder",
      "No subjects")))

    nameColumn.setText(AppSettings.language.getItemOrElse(
      "subjectManager_nameColumnHeader",
      "Name"))

    shortNameColumn.setText(AppSettings.language.getItemOrElse(
      "subjectManager_shortNameColumnHeader",
      "Short Name"))

    theoryEventsColumn.setText(AppSettings.language.getItemOrElse(
      "subjectManager_theoryEventColumnHeader",
      "Theory"
    ))

    problemsEventsColumn.setText(AppSettings.language.getItemOrElse(
      "subjectManager_problemsEventColumnHeader",
      "Problems"
    ))

    laboratoryEventsColumn.setText(AppSettings.language.getItemOrElse(
      "subjectManager_laboratoryEventColumnHeader",
      "Laboratory"
    ))

    computerEventsColumn.setText(AppSettings.language.getItemOrElse(
      "subjectManager_computerEventColumnHeader",
      "Computer"
    ))

    addButton.setText(AppSettings.language.getItem("subjectManager_addSubjectButton"))
    editButton.setText(AppSettings.language.getItem("subjectManager_editSubjectButton"))
    removeButton.setText(AppSettings.language.getItem("subjectManager_removeSubjectButton"))

  }

  override protected def additionalTableSetup(): Unit = {
    addColumns()
    configureColumns()
    fillTable(subjects)
  }

  private def addColumns(): Unit = {
    nameColumn = new TableColumn
    shortNameColumn = new TableColumn
    theoryEventsColumn = new TableColumn
    problemsEventsColumn = new TableColumn
    laboratoryEventsColumn = new TableColumn
    computerEventsColumn = new TableColumn
    detailsColumn = new TableColumn

    addColumn(nameColumn)
    addColumn(shortNameColumn)
    addColumn(theoryEventsColumn)
    addColumn(problemsEventsColumn)
    addColumn(laboratoryEventsColumn)
    addColumn(computerEventsColumn)
    addColumn(detailsColumn)
  }

  private def configureColumns(): Unit = {
    nameColumn.setCellValueFactory(cell => new SimpleStringProperty(cell.getValue.name))

    shortNameColumn.setCellValueFactory(cell => new SimpleStringProperty(cell.getValue.shortName))

    theoryEventsColumn.setCellFactory(eventColumnCellFactory(TheoryEvent))

    problemsEventsColumn.setCellFactory(eventColumnCellFactory(ProblemsEvent))

    laboratoryEventsColumn.setCellFactory(eventColumnCellFactory(LaboratoryEvent))

    computerEventsColumn.setCellFactory(eventColumnCellFactory(ComputerEvent))

    detailsColumn.setCellFactory(_ => new TableCell[Subject, Null] {
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

  private def eventColumnCellFactory[T](eventType: EventType): Callback[TableColumn[Subject,T], TableCell[Subject,T]] =
    _ => new TableCell[Subject,T] {
      override protected def updateItem(item: T, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (!empty) {
          val events = getTableView.getItems.get(getIndex).events.filter(_.eventType == eventType)
          setGraphic(generateEventsHyperlink(events, AppSettings.language.getItemOrElse(
            "subjectManager_theoryEventListPromp",
            "Theory events")))
        }
        else {
          setGraphic(null)
          setText(null)
        }
      }
    }

  private def generateEventsHyperlink(events: Iterable[Event], windowTitle: String): Node = {
    val hyperlink = new Hyperlink(events.size.toString + " events")
    hyperlink.setOnAction(_ => showEventList(events, windowTitle))

    val tooltip = new Tooltip(AppSettings.language.getItemOrElse(
      "subjectManager_eventListHyperlinkTooltip",
      "Click to see the complete list of events"))
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
      AppSettings.language.getItemOrElse("subjectManager_eventListPlaceholder", "No events"),
      StageSettings(
        windowTitle,
        Some(stage),
        Modality.WINDOW_MODAL))

    prompt.showAndWait()
  }

  private def generateDetailsButton(subject: Subject): HBox = {
    val hBox = generateHyperlinkHBox

    val button = new Button(AppSettings.language.getItemOrElse(
      "courseManager_detailsButton",
      "show more..."))
    button.setOnAction(actionEvent => {
      promptSubjectInformation(subject)
      actionEvent.consume()
    })
    button.setMaxWidth(USE_COMPUTED_SIZE)

    hBox.getChildren.add(button)

    hBox
  }

  override protected def newEntity: Option[Subject] = {
    val formResult = promptNewSubjectForm

    if(formResult.nonEmpty)
      Some(createSubjectFromDescriptor(formResult.get))
    else
      None
  }

  private def promptNewSubjectForm: Option[SD] = {
    val subjectForm = new CreateSubjectLikeFormController(appDatabase.courses, appDatabase.resources)

    subjectForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("subjectForm_create_windowTitle", "New Subject"),
      stage,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.SubjectForm),
      subjectForm))

    subjectForm.waitFormResult
  }

  private def createSubjectFromDescriptor(subjectDescriptor: SD): Subject = {
    type ED = EventDescriptor[Nothing, Course, Resource, EventDescriptor[Nothing, Course, Resource, _]]

    val eventDescriptorMapping = new mutable.HashMap[SubjectLikeForm[Course, Resource]#ED, ED]

    val eventDescriptors = subjectDescriptor.events.map(sled => {
      val ed = new ED

      ed.name = sled.name
      ed.shortName = sled.shortName
      ed.description = sled.description
      ed.eventType = sled.eventType
      ed.duration = sled.duration
      ed.periodicity = sled.periodicity

      ed.course = sled.course
      ed.quarter = sled.quarter
      ed.neededResource = sled.neededResource
      //ed.incompatibilities ++= sled.incompatibilities

      eventDescriptorMapping += (sled, ed)

      ed
    })

    subjectDescriptor.events.foreach(sled => {
      eventDescriptorMapping.get(sled).foreach(_.incompatibilities = sled.incompatibilities.map(eventDescriptorMapping))
    })

    val adaptedSubjectDescriptor = new SubjectDescriptor[Course, ED]

    adaptedSubjectDescriptor.name = subjectDescriptor.name
    adaptedSubjectDescriptor.shortName = subjectDescriptor.shortName
    adaptedSubjectDescriptor.description = subjectDescriptor.description
    adaptedSubjectDescriptor.course = subjectDescriptor.course
    adaptedSubjectDescriptor.quarter = subjectDescriptor.quarter
    adaptedSubjectDescriptor.color = subjectDescriptor.color
    adaptedSubjectDescriptor.additionalFields ++= subjectDescriptor.additionalFields
    adaptedSubjectDescriptor.eventTypeIncompatibilities ++= subjectDescriptor.eventTypeIncompatibilities
    adaptedSubjectDescriptor.events ++= eventDescriptors

    val subject = appDatabase.createSubjectFromDescriptor(adaptedSubjectDescriptor)._2

    mainController.notifySubjectCreation(subject)

    subject
  }

  override protected def editEntity(entity: Subject): Option[Subject] = ???

  override protected def removeEntity(entity: Subject, removeMode: RemoveMode): Unit = ???

  override protected def askRemoveMode: Option[RemoveMode] = Some(HardRemove)

  protected def promptSubjectInformation(subject: Subject): Unit =
    new ShowSubjectLikeInformationController[Subject, Course, Resource, Event](subject, stage).showAndWait()

  override protected def notifySingleSelection(): Unit = {
    removeButton.setText(AppSettings.language.getItemOrElse(
      "subjectManager_removeSubjectButton",
      "Remove Subject"))
  }

  override protected def notifyMultipleSelection(): Unit = {
    removeButton.setText(AppSettings.language.getItemOrElse(
      "subjectManager_removeSubjectsButton",
      "Remove Subjects"))
  }


}
