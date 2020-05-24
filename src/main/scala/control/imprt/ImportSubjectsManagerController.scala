package control.imprt

import app.FXMLPaths
import factory.ViewFactory
import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn}
import model.blueprint.SubjectBlueprint
import util.Utils

class ImportSubjectsManagerController(/*importJobController: ImportJobEditorController*/)
  extends ImportEntityManagerController[SubjectBlueprint]{

  @FXML var shortNameColumn: TableColumn[SubjectBlueprint, String] = _
  @FXML var courseColumn: TableColumn[SubjectBlueprint, String] = _
  @FXML var quarterColumn: TableColumn[SubjectBlueprint, String] = _
  @FXML var eventsColumn: TableColumn[SubjectBlueprint, Int] = _

  private val detailsController: ImportSubjectDetailsController = {
    val controller = new ImportSubjectDetailsController
    Utils.loadScene(new ViewFactory(FXMLPaths.ImportSubjectDetailsView), controller)
    controller
  }

  def additionalInitialization(): Unit = {
    detailBoxContent_=(detailsController.mainBox)

    shortNameColumn = new TableColumn()
    courseColumn = new TableColumn()
    quarterColumn = new TableColumn()
    eventsColumn = new TableColumn()
  }

  override def initializeContentLanguage(): Unit = {
    newButton.setText(language.getItemOrElse("import_subject_newButton", "New Subject"))
    editButton.setText(language.getItemOrElse("import_subject_editButton", "Edit Subject"))
    deleteButton.setText(language.getItemOrElse("import_subject_singleDeleteButton", "Delete Subject"))

    table.setPlaceholder(new Label(language.getItemOrElse("import_subject_tablePlaceholder", "No subjects")))

    shortNameColumn.setText(language.getItemOrElse("import_subject_shortNameColumn", "Short"))
    courseColumn.setText(language.getItemOrElse("import_subject_courseColumn", "Course"))
    quarterColumn.setText(language.getItemOrElse("import_subject_quarterColumn", "Quarter"))
    eventsColumn.setText(language.getItemOrElse("import_subject_eventsColumn", "Event Nº"))
  }

  def additionalTableSetup(): Unit = {
    shortNameColumn.setCellValueFactory(cell => {
      if (cell.getValue != null) new SimpleStringProperty(cell.getValue.shortName)
      else new SimpleStringProperty()
    })

    courseColumn.setCellValueFactory(cell => {
      if (cell.getValue != null) new SimpleStringProperty(cell.getValue.course.name)
      else new SimpleStringProperty()
    })

    quarterColumn.setCellValueFactory(cell => {
      if (cell.getValue != null) new SimpleStringProperty(cell.getValue.quarter.toShortString)
      else new SimpleStringProperty()
    })

    eventsColumn.setCellValueFactory(cell => {
      val cellValue =
        if (cell.getValue != null) new SimpleIntegerProperty(cell.getValue.events.size)
        else new SimpleIntegerProperty()

      cellValue.asInstanceOf[ObservableValue[Int]]
    })

    addColumn(shortNameColumn)
    addColumn(courseColumn)
    addColumn(quarterColumn)
    addColumn(eventsColumn)
  }

  def newEntity: Option[SubjectBlueprint] = {
    //promptSubjectForm
    ???
  }

  /*private def promptSubjectForm: Option[SubjectBlueprint] = {
    val subjectForm = new SubjectFormController2(courses, resources)

    subjectForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("subjectForm_windowTitle", "Create new Subject"),
      newButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.SubjectForm),
      subjectForm))

    subjectForm.waitFormResult
  }*/

  def editEntity(entity: SubjectBlueprint): Option[SubjectBlueprint] = {
    ???
  }

  def deleteEntity(entity: SubjectBlueprint): Unit = {
    ???
  }

  def showAdditionalInformation(entity: SubjectBlueprint): Unit = {
    //TODO
    showDetailBox()
  }

  override protected def notifySingleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_subject_singleDeleteButton", "Delete Subject"))
  }

  override protected def notifyMultipleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_subject_multipleDeleteButton", "Delete Subjects"))
  }
}
