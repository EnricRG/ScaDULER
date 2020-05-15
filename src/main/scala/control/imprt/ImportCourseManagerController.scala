package control.imprt

import app.AppSettings
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn}
import model.blueprint.CourseBlueprint

class ImportCourseManagerController extends ImportEntityManagerController[CourseBlueprint]{

  @FXML var nameColumn: TableColumn[CourseBlueprint, String] = _

  override def additionalInitialization(): Unit = {
    nameColumn = new TableColumn()
  }

  override def initializeContentLanguage(): Unit = {
    newButton.setText(AppSettings.language.getItemOrElse("import_course_newButton", "New Course"))
    editButton.setText(AppSettings.language.getItemOrElse("import_course_editButton", "Edit Course"))
    newButton.setText(AppSettings.language.getItemOrElse("import_course_newButton", "Delete Course"))

    table.setPlaceholder(new Label(AppSettings.language.getItemOrElse("import_course_tablePlaceholder", "No courses")))

    nameColumn.setText(AppSettings.language.getItemOrElse("import_course_nameColumn", "Name"))
  }

  override def setupTable(): Unit = {
    table.getColumns.add(nameColumn.asInstanceOf[TableColumn[CourseBlueprint,_]]) //little hack to check types
  }

  override def newEntity: CourseBlueprint = {
    ???
  }

  override def editEntity(entity: CourseBlueprint): CourseBlueprint = {
    ???
  }

  override def deleteEntity(entity: CourseBlueprint): Unit = { }

  override def showAdditionalInformation(entity: CourseBlueprint): Unit = {

    ???

    showDetailBox()
  }


}
