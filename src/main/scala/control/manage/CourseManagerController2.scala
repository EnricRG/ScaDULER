package control.manage

import app.{AppSettings, FXMLPaths}
import control.MainController
import control.form.{CreateCourseLikeFormController, EditCourseLikeFormController}
import factory.ViewFactory
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.{Hyperlink, Label, TableCell, TableColumn}
import javafx.scene.layout.HBox
import javafx.scene.layout.Region.USE_COMPUTED_SIZE
import javafx.stage.Modality
import model.descriptor.CourseDescriptor
import model.{Course, Event, Subject}
import service.AppDatabase
import util.Utils

class CourseManagerController2(
  courses: Iterable[Course],
  mainController: MainController,
  appDatabase: AppDatabase
) extends EntityManagerController2[Course](mainController) {

  @FXML protected var nameColumn: TableColumn[Course, String] = new TableColumn
  @FXML protected var descriptionColumn: TableColumn[Course, String] = new TableColumn
  @FXML protected var subjectsColumn: TableColumn[Course, Int] = new TableColumn
  @FXML protected var eventsColumn: TableColumn[Course, Int] = new TableColumn

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
          setGraphic(generateEventsHyperlink(events))
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

    //TODO add tooltip to hyperlink

    val hBox: HBox = generateHyperlinkHBox
    hBox.getChildren.add(hyperlink)

    hBox
  }

  private def generateEventsHyperlink(events: Iterable[Event]): Node = {
    val hyperlink = new Hyperlink(events.size.toString)
    hyperlink.setOnAction(actionEvent => showEventList(events))

    //TODO add tooltip to hyperlink

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
    //TODO prompt subject list
  }

  private def showEventList(events: Iterable[Event]): Unit = {
    //TODO prompt event list
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
    val course = appDatabase.createCourse()._2

    Course.setCourseFromDescriptor(course,courseDescriptor)

    course
  }

  override protected def editEntity(entity: Course): Option[Course] = {
    promptEditForm(entity)
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

  override protected def removeEntity(entity: Course): Unit = {
    val hardDelete = promptDeleteModeDialog
    val deletedEvents = appDatabase.removeCourse(entity, hardDelete)._2
    //TODO mainController.notifyCourseDeletion(entity)
    //TODO mainController.notifyEventsDeletion(deletedEvents)
  }

  private def promptDeleteModeDialog: Boolean = {
    //TODO

    sealed trait DeleteMode
    object HardMode extends DeleteMode
    object SoftMode extends DeleteMode

    false
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
