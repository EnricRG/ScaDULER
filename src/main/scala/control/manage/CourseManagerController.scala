package control.manage

import app.{AppSettings, FXMLPaths}
import control.form.{CreateCourseLikeFormController, EditCourseLikeFormController}
import control.misc.{HardRemove, NameListPrompt, RemoveMode, RemoveModePrompt}
import control.{MainController, StageSettings}
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
) extends EntityManagerController2[Course](mainController) {

  @FXML protected var nameColumn: TableColumn[Course, String] = new TableColumn
  @FXML protected var descriptionColumn: TableColumn[Course, String] = new TableColumn
  @FXML protected var subjectsColumn: TableColumn[Course, Int] = new TableColumn
  @FXML protected var eventsColumn: TableColumn[Course, Int] = new TableColumn
  @FXML protected var assignedEventsColumn: TableColumn[Course, Int] = new TableColumn

  def this(mainController: MainController, appDatabase: AppDatabase) =
    this(Nil, mainController, appDatabase)

  override protected def initializeContentLanguage(): Unit = {
    table.setPlaceholder(new Label(AppSettings.language.getItemOrElse(
      "courseTable_placeholder",
      "No Courses")))

    nameColumn.setText(AppSettings.language.getItemOrElse(
      "courseManager_nameColumnHeader",
      "Name"))

    descriptionColumn.setText(AppSettings.language.getItemOrElse(
      "courseManager_descriptionColumnHeader",
      "Description"))

    subjectsColumn.setText(AppSettings.language.getItemOrElse(
      "courseManager_subjectsColumnHeader",
      "Subjects"))

    eventsColumn.setText(AppSettings.language.getItemOrElse(
      "courseManager_eventsColumnHeader",
      "Events"))

    assignedEventsColumn.setText(AppSettings.language.getItemOrElse(
      "courseManager_assignedEventsColumnHeader",
      "Assigned events"))

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
    addColumn(nameColumn)
    addColumn(descriptionColumn)
    addColumn(subjectsColumn)
    addColumn(eventsColumn)
    addColumn(assignedEventsColumn)
  }

  private def configureColumns(): Unit = {
    nameColumn.setCellValueFactory(cell => new SimpleStringProperty(cell.getValue.name))

    descriptionColumn.setCellValueFactory(cell => new SimpleStringProperty(cell.getValue.description))

    subjectsColumn.setCellFactory(column => new TableCell[Course, Int] {
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

    eventsColumn.setCellFactory(column => new TableCell[Course, Int] {
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

    assignedEventsColumn.setCellFactory(column => new TableCell[Course, Int] {
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
  }

  private def generateSubjectsHyperlink(subjects: Iterable[Subject]): Node = {
    val hyperlink = new Hyperlink(subjects.size.toString)
    hyperlink.setOnAction(actionEvent => showSubjectList(subjects))

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
    hyperlink.setOnAction(actionEvent => showEventList(events, windowTitle))

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

    //TODO mainController.notifyCourseCreation(course)

    course
  }

  override protected def editEntity(entity: Course): Option[Course] = {
    val editedCourse = promptEditForm(entity)

    //TODO if(editedCourse.nonEmpty) mainController.notifyCourseEdition(editedCourse.get)

    editedCourse
  }

  private def promptEditForm(course: Course): Option[Course] = {
    val courseForm = new EditCourseLikeFormController(course)

    courseForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("courseForm_edit_windowTitle", "Edit Course"),
      editButton.getScene.getWindow,
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
      //TODO mainController.notifyEventsDeletion(deletedEvents)
    }
    else {
      appDatabase.removeCourse(entity, hardDelete = false)
    }
    //TODO mainController.notifyCourseDeletion(entity)
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
