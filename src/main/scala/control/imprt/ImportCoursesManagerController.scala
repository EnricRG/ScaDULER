package control.imprt

import app.{AppSettings, FXMLPaths}
import control.form.{CreateCourseFormController, EditCourseFormController}
import factory.ViewFactory
import file.imprt.MutableImportJob
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn}
import javafx.stage.Modality
import model.blueprint.CourseBlueprint
import util.Utils

class ImportCoursesManagerController(importJobEditorController: ImportJobEditorController,
                                     editableImportJob: MutableImportJob)
  extends ImportEntityManagerController[CourseBlueprint]{

  @FXML var nameColumn: TableColumn[CourseBlueprint, String] = _

  private val detailsController: ImportCourseDetailsController = {
    val controller = new ImportCourseDetailsController
    Utils.loadScene(new ViewFactory(FXMLPaths.ImportCourseDetailsView), controller)
    controller
  }

  override def additionalInitialization(): Unit = {
    detailBoxContent_=(detailsController.mainBox)
    nameColumn = new TableColumn()
  }

  override def initializeContentLanguage(): Unit = {
    newButton.setText(language.getItemOrElse("import_course_newButton", "New Course"))
    editButton.setText(language.getItemOrElse("import_course_editButton", "Edit Course"))
    deleteButton.setText(language.getItemOrElse("import_course_singleDeleteButton", "Delete Course"))

    table.setPlaceholder(new Label(language.getItemOrElse("import_course_tablePlaceholder", "No courses")))

    nameColumn.setText(language.getItemOrElse("import_course_nameColumn", "Name"))
  }

  override def additionalTableSetup(): Unit = {
    nameColumn.setCellValueFactory(cell => {
      if (cell.getValue != null) new SimpleStringProperty(cell.getValue.name)
      else new SimpleStringProperty()
    })

    addColumn(nameColumn)

    addContent(editableImportJob.courses)

    table.getSortOrder.add(nameColumn.asInstanceOf[TableColumn[CourseBlueprint, _]])
  }

  override def newEntity: Option[CourseBlueprint] = {
    promptNewCourseForm()
  }

  private def promptNewCourseForm(): Option[CourseBlueprint] = {
    val courseForm = new CreateCourseFormController()

    courseForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("courseForm_windowTitle", "Create new Course"),
      newButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[CreateCourseFormController](FXMLPaths.CourseForm),
      courseForm))

    val ocd = courseForm.waitFormResult

    if (ocd.nonEmpty){
      val cb = new CourseBlueprint
      CourseBlueprint.setBlueprintFromDescriptor(cb,ocd.get)
      Some(cb)
    }
    else None
  }

  override def editEntity(entity: CourseBlueprint): Option[CourseBlueprint] = {
    promptEditCourseForm(entity)
  }

  private def promptEditCourseForm(course: CourseBlueprint): Option[CourseBlueprint] = {
    val courseForm = new EditCourseFormController(course)

    courseForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("courseForm_edit_windowTitle", "Edit Course"),
      newButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[EditCourseFormController[CourseBlueprint]](FXMLPaths.CourseForm),
      courseForm))

    //This is fine because EditCourseFormController(course) specification ensures that if the form result is Some(x),
    //x == course, and that's what we want.
    courseForm.waitFormResult
  }

  override def deleteEntity(entity: CourseBlueprint): Unit = {
    importJobEditorController.notifyCourseDeletion(entity)
  }

  override def showAdditionalInformation(entity: CourseBlueprint): Unit = {
    detailsController.setFromCourseBlueprint(entity)
    showDetailBox()
  }

  override def clearAdditionalInformation(): Unit = {
    detailsController.clear()
  }

  override protected def notifySingleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_course_singleDeleteButton", "Delete Course"))
  }

  override protected def notifyMultipleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_course_multipleDeleteButton", "Delete Courses"))
  }

}
