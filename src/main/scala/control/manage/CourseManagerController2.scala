package control.manage

import app.AppSettings
import control.MainController
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.{Hyperlink, Label, TableCell, TableColumn}
import javafx.scene.layout.HBox
import javafx.scene.layout.Region.USE_COMPUTED_SIZE
import model.{Course, Event, Subject}
import service.CourseDatabase

class CourseManagerController2(
  courses: Iterable[Course],
  mainController: MainController,
  courseDatabase: CourseDatabase
) extends EntityManagerController2[Course](mainController) {

  @FXML protected var nameColumn: TableColumn[Course, String] = new TableColumn
  @FXML protected var descriptionColumn: TableColumn[Course, String] = new TableColumn
  @FXML protected var subjectsColumn: TableColumn[Course, Int] = new TableColumn
  @FXML protected var eventsColumn: TableColumn[Course, Int] = new TableColumn

  def this(mainController: MainController, courseDatabase: CourseDatabase) =
    this(Nil, mainController, courseDatabase)

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
    val hBox: HBox = generateHyperlinkHBox

    val hyperlink = new Hyperlink(subjects.size.toString)

    hyperlink.setOnAction(actionEvent => showSubjectList(subjects))

    hBox.getChildren.add(hyperlink)

    hBox
  }

  private def generateEventsHyperlink(events: Iterable[Event]): Node = {
    val hBox: HBox = generateHyperlinkHBox

    val hyperlink = new Hyperlink(events.size.toString)

    hyperlink.setOnAction(actionEvent => showEventList(events))

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

  override protected def addButtonAction(): Unit = ???

  override protected def editButtonAction(entity: Course): Unit = ???

  override protected def removeButtonAction(entities: Iterable[Course]): Unit = ???

  override protected def notifySingleSelection(): Unit = {
    editButton.setDisable(false)

    removeButton.setText(AppSettings.language.getItemOrElse(
      "courseManager_removeCourseButton",
      "Remove Course"))
    removeButton.setDisable(false)
  }

  override protected def notifyMultipleSelection(): Unit = {
    editButton.setDisable(true)

    removeButton.setText(AppSettings.language.getItemOrElse(
      "courseManager_removeCoursesButton",
      "Remove Courses"))
    removeButton.setDisable(false)
  }
}
