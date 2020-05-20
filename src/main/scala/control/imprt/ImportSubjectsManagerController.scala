package control.imprt

import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn}
import model.blueprint.SubjectBlueprint

class ImportSubjectsManagerController(/*
  courses: Iterable[CourseLike],
  resources: Iterable[ResourceLike]*/) extends ImportEntityManagerController[SubjectBlueprint]{

  @FXML var nameColumn: TableColumn[SubjectBlueprint, String] = _
  @FXML var shortNameColumn: TableColumn[SubjectBlueprint, String] = _
  @FXML var courseColumn: TableColumn[SubjectBlueprint, String] = _
  @FXML var quarterColumn: TableColumn[SubjectBlueprint, String] = _
  @FXML var eventsColumn: TableColumn[SubjectBlueprint, Int] = _

  def additionalInitialization(): Unit = {
    nameColumn = new TableColumn()
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

    nameColumn.setText(language.getItemOrElse("import_subject_nameColumn", "Name"))
    shortNameColumn.setText(language.getItemOrElse("import_subject_shortNameColumn", "Short"))
    courseColumn.setText(language.getItemOrElse("import_subject_courseColumn", "Course"))
    quarterColumn.setText(language.getItemOrElse("import_subject_quarterColumn", "Quarter"))
    eventsColumn.setText(language.getItemOrElse("import_subject_eventsColumn", "Event Nº"))
  }

  def additionalTableSetup(): Unit = {
    table.getColumns.add(nameColumn.asInstanceOf[TableColumn[SubjectBlueprint,_]]) //little hack to check types
    table.getColumns.add(shortNameColumn.asInstanceOf[TableColumn[SubjectBlueprint,_]])
    table.getColumns.add(courseColumn.asInstanceOf[TableColumn[SubjectBlueprint,_]])
    table.getColumns.add(quarterColumn.asInstanceOf[TableColumn[SubjectBlueprint,_]])
    table.getColumns.add(eventsColumn.asInstanceOf[TableColumn[SubjectBlueprint,_]])
  }

  def newEntity: Option[SubjectBlueprint] = {
    //promptSubjectForm
    ???
  }

  /*private def promptCourseForm: Option[CourseBlueprint] = {
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

  def deleteEntity(entity: SubjectBlueprint): Unit = { }

  def showAdditionalInformation(entity: SubjectBlueprint): Unit = {
    //FIXME content won't update since the controller is not swapped from detailBox children.
    showDetailBox()
  }

  override protected def notifySingleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_subject_singleDeleteButton", "Delete Subject"))
  }

  override protected def notifyMultipleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_subject_multipleDeleteButton", "Delete Subjects"))
  }
}
