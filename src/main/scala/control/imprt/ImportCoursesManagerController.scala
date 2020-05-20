package control.imprt

import app.{AppSettings, FXMLPaths}
import control.form.CourseFormController
import factory.ViewFactory
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Label, TableColumn}
import javafx.stage.Modality
import model.blueprint.CourseBlueprint
import util.Utils

class ImportCoursesManagerController extends ImportEntityManagerController[CourseBlueprint]{

  @FXML var nameColumn: TableColumn[CourseBlueprint, String] = _

  override def additionalInitialization(): Unit = {
    nameColumn = new TableColumn()
  }

  override def initializeContentLanguage(): Unit = {
    newButton.setText(AppSettings.language.getItemOrElse("import_course_newButton", "New Course"))
    editButton.setText(AppSettings.language.getItemOrElse("import_course_editButton", "Edit Course"))
    newButton.setText(AppSettings.language.getItemOrElse("import_course_newButton", "Delete Course"))

    table.setPlaceholder(new Label(AppSettings.language.getItemOrElse(
      "import_course_tablePlaceholder",
      "No courses")))

    nameColumn.setText(AppSettings.language.getItemOrElse("import_course_nameColumn", "Name"))
  }

  override def additionalTableSetup(): Unit = {
    table.getColumns.add(nameColumn.asInstanceOf[TableColumn[CourseBlueprint,_]]) //little hack to check types
  }

  override def newEntity: Option[CourseBlueprint] = {
    promptCourseForm
  }

  private def promptCourseForm: Option[CourseBlueprint] = {
    val courseForm = new CourseFormController

    courseForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("courseForm_windowTitle", "Create new Course"),
      newButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[CourseFormController](FXMLPaths.CourseForm),
      courseForm))

    courseForm.waitFormResult
  }

  override def editEntity(entity: CourseBlueprint): Option[CourseBlueprint] = {
    ???
  }

  override def deleteEntity(entity: CourseBlueprint): Unit = { }

  override def showAdditionalInformation(entity: CourseBlueprint): Unit = {
    val informationController: ImportCourseDetailsController = newInformationController

    informationController.name_=(entity.name)
    informationController.description_=(entity.description)

    detailBox.getChildren.add(informationController.mainBox)

    showDetailBox()
  }

  private def newInformationController: ImportCourseDetailsController = {
    val controller = new ImportCourseDetailsController

    Utils.loadScene(new ViewFactory(FXMLPaths.ImportCourseDetailsView), controller)

    controller
  }

}
