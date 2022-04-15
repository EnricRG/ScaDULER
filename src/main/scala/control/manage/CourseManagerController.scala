package control.manage

import app.{AppSettings, FXMLPaths}
import control.form.{CreateCourseLikeFormController, EditCourseLikeFormController, ShowCourseLikeInformationController}
import control.misc.{HardRemove, NameListPrompt, RemoveMode, RemoveModePrompt}
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
import model.descriptor.CourseDescriptor
import model.{Course, Event, Subject}
import service.AppDatabase
import util.Utils

class CourseManagerController(
  courses: Iterable[Course],
  mainController: MainController,
  appDatabase: AppDatabase
) extends EntityManagerController2[Course]
  with SelfInitializedStageController {

  @FXML protected var nameColumn: TableColumn[Course, String] = _
  @FXML protected var subjectsColumn: TableColumn[Course, Int] = _
  @FXML protected var eventsColumn: TableColumn[Course, Int] = _
  @FXML protected var assignedEventsColumn: TableColumn[Course, Int] = _
  @FXML protected var detailsColumn: TableColumn[Course, Null] = _

  def this(mainController: MainController, appDatabase: AppDatabase) =
    this(Nil, mainController, appDatabase)

  override def selfInitialize(): Unit =
    initializeWith(
      StageSettings(
        AppSettings.language.getItemOrElse("courseManager_windowTitle", "Manage Courses"),
        Some(mainController.getWindow),
        Modality.WINDOW_MODAL),
      FXMLPaths.EntityManagerPanel)

  override protected def initializeContentLanguage(): Unit = {
    table.setPlaceholder(new Label(AppSettings.language.getItemOrElse(
      "courseTable_placeholder",
      "No courses")))

    nameColumn.setText(AppSettings.language.getItemOrElse(
      "courseManager_nameColumnHeader",
      "Name"))

    subjectsColumn.setText(AppSettings.language.getItemOrElse(
      "courseManager_subjectsColumnHeader",
      "Subjects"))

    eventsColumn.setText(AppSettings.language.getItemOrElse(
      "courseManager_eventsColumnHeader",
      "Events"))

    assignedEventsColumn.setText(AppSettings.language.getItemOrElse(
      "courseManager_assignedEventsColumnHeader",
      "Assigned events"))

    detailsColumn.setText(AppSettings.language.getItemOrElse(
      "courseManager_detailsColumnHeader",
      "Details"))

    addButton.setText(AppSettings.language.getItemOrElse(
      "courseManager_addCourseButton",
      "Create Course"))

    editButton.setText(AppSettings.language.getItemOrElse(
      "courseManager_editCourseButton",
      "Edit Course"))

    removeButton.setText(AppSettings.language.getItemOrElse(
      "courseManager_removeCourseButton",
      "Remove Course"))
  }

  override protected def additionalTableSetup(): Unit = {
    addColumns()
    configureColumns()
    fillTable(courses)
  }

  private def addColumns(): Unit = {
    nameColumn = new TableColumn
    subjectsColumn = new TableColumn
    eventsColumn = new TableColumn
    assignedEventsColumn = new TableColumn
    detailsColumn = new TableColumn

    addColumn(nameColumn)
    addColumn(subjectsColumn)
    addColumn(eventsColumn)
    addColumn(assignedEventsColumn)
    addColumn(detailsColumn)
  }

  private def configureColumns(): Unit = {
    nameColumn.setCellValueFactory(cell => new SimpleStringProperty(cell.getValue.name))

    subjectsColumn.setCellFactory(_ => new TableCell[Course, Int] {
      override protected def updateItem(item: Int, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (!empty) {
          val subjects = getTableView.getItems.get(getIndex).subjects
          setGraphic(generateSubjectsHyperlink(subjects))
        }
        else {
          setGraphic(null)
          setText(null)
        }
      }
    })

    eventsColumn.setCellFactory(_ => new TableCell[Course, Int] {
      override protected def updateItem(item: Int, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (!empty) {
          val events = getTableView.getItems.get(getIndex).events
          setGraphic(generateEventsHyperlink(events, AppSettings.language.getItemOrElse(
            "courseManager_eventListPromp",
            "Events")))
        }
        else {
          setGraphic(null)
          setText(null)
        }
      }
    })

    assignedEventsColumn.setCellFactory(_ => new TableCell[Course, Int] {
      override protected def updateItem(item: Int, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (!empty) {
          val events = getTableView.getItems.get(getIndex).assignedEvents
          setGraphic(generateEventsHyperlink(events, AppSettings.language.getItemOrElse(
            "courseManager_assignedEventListPromp",
            "Assigned events")))
        }
        else {
          setGraphic(null)
          setText(null)
        }
      }
    })

    detailsColumn.setCellFactory(_ => new TableCell[Course, Null] {
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

  private def generateSubjectsHyperlink(subjects: Iterable[Subject]): Node = {
    val hyperlink = new Hyperlink(subjects.size.toString)
    hyperlink.setOnAction(_ => showSubjectList(subjects))

    val tooltip = new Tooltip(AppSettings.language.getItemOrElse(
      "courseManager_subjectHyperlinkTooltip",
      "Click to see the complete list of this course's subjects"))
    hyperlink.setTooltip(tooltip)

    val hBox: HBox = generateHyperlinkHBox
    hBox.getChildren.add(hyperlink)

    hBox
  }

  private def generateEventsHyperlink(events: Iterable[Event], windowTitle: String): Node = {
    val hyperlink = new Hyperlink(events.size.toString)
    hyperlink.setOnAction(_ => showEventList(events, windowTitle))

    val tooltip = new Tooltip(AppSettings.language.getItemOrElse(
      "courseManager_eventHyperlinkTooltip",
      "Click to see the complete list of this course's events"))
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

  private def generateDetailsButton(course: Course): HBox = {
    val hBox = generateHyperlinkHBox

    val button = new Button(AppSettings.language.getItemOrElse(
      "courseManager_detailsButton",
      "show more..."))
    button.setOnAction(actionEvent => {
      promptCourseInformation(course)
      actionEvent.consume()
    })
    button.setMaxWidth(USE_COMPUTED_SIZE)

    hBox.getChildren.add(button)

    hBox
  }

  private def showSubjectList(subjects: Iterable[Subject]): Unit = {
    val prompt = new NameListPrompt(
      subjects.map(_.shortName),
      AppSettings.language.getItemOrElse("courseManager_subjectListPlaceholder", "No subjects"),
      StageSettings(
        AppSettings.language.getItemOrElse("courseManager_subjectListPromp", "Subjects"),
        Some(stage),
        Modality.WINDOW_MODAL))

    prompt.showAndWait()
  }

  private def showEventList(events: Iterable[Event], windowTitle: String): Unit = {
    val prompt = new NameListPrompt(
      events.map(_.shortName),
      AppSettings.language.getItemOrElse("courseManager_eventListPlaceholder", "No events"),
      StageSettings(
        windowTitle,
        Some(stage),
        Modality.WINDOW_MODAL))

    prompt.showAndWait()
  }

  override protected def newEntity: Option[Course] = {
    val formResult = promptNewCourseForm

    if(formResult.nonEmpty)
      Some(createCourseFromDescriptor(formResult.get))
    else
      None
  }

  private def promptNewCourseForm: Option[CourseDescriptor] = {
    val courseForm = new CreateCourseLikeFormController()

    courseForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("courseForm_windowTitle", "Create new Course"),
      addButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.CourseForm),
      courseForm))

    courseForm.waitFormResult
  }

  private def createCourseFromDescriptor(courseDescriptor: CourseDescriptor): Course = {
    val course = appDatabase.createCourseFromDescriptor(courseDescriptor)._2

    mainController.notifyCourseCreation(course)

    course
  }

  override protected def editEntity(entity: Course): Option[Course] = {
    val editedCourse = promptEditForm(entity)

    if(editedCourse.nonEmpty)
      mainController.notifyCourseEdition(editedCourse.get)

    editedCourse
  }

  private def promptEditForm(course: Course): Option[Course] = {
    val courseForm = new EditCourseLikeFormController(course)

    courseForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("courseForm_edit_windowTitle", "Edit Course"),
      stage,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.CourseForm),
      courseForm))

    //This is fine because EditCourseLikeFormController(course) specification ensures that if the form result is
    //Some(x), x == course, and that's what we want.
    courseForm.waitFormResult //execution thread stops here.
  }

  override protected def removeEntity(entity: Course, removeMode: RemoveMode): Unit = {
    if(removeMode == HardRemove) {
      val deletedEvents = appDatabase.removeCourse(entity, hardDelete = true)._2
      mainController.notifyEventsDeletion(deletedEvents)
    }
    else {
      appDatabase.removeCourse(entity, hardDelete = false)
    }

    mainController.notifyCourseDeletion(entity)
  }

  override protected def askRemoveMode: Option[RemoveMode] = {
    new RemoveModePrompt(
      AppSettings.language.getItemOrElse(
        "courseManager_removeCoursePrompt_explanation",
        "Deleting a course could leave some subjects and events dangling around.\n" +
          "Would you like to keep them, or also remove them?"),
      AppSettings.language.getItemOrElse(
        "courseManager_removeCoursePrompt_softRemove",
        "Keep"),
      AppSettings.language.getItemOrElse(
        "courseManager_removeCoursePrompt_hardRemove",
        "Remove"),
      StageSettings("removeMode", Some(stage), Modality.WINDOW_MODAL)).waitChoice()
  }

  protected def promptCourseInformation(course: Course): Unit =
    new ShowCourseLikeInformationController(course, stage).showAndWait()


  override protected def notifySingleSelection(): Unit = {
    removeButton.setText(AppSettings.language.getItemOrElse(
      "courseManager_removeCourseButton",
      "Remove Course"))
  }

  override protected def notifyMultipleSelection(): Unit = {
    removeButton.setText(AppSettings.language.getItemOrElse(
      "courseManager_removeCoursesButton",
      "Remove Courses"))
  }
}
