package control.imprt

import app.FXMLPaths
import control.form.{SubjectDescriptorFormController, SubjectFormInitializer}
import factory.ViewFactory
import file.imprt.MutableImportJob
import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn}
import javafx.stage.Modality
import model.blueprint.{CourseBlueprint, EventBlueprint, ResourceBlueprint, SubjectBlueprint}
import model.{EventType, EventTypes}
import util.Utils

import scala.collection.mutable

class ImportSubjectsManagerController(importJobEditorController: ImportJobEditorController,
                                      editableImportJob: MutableImportJob)
  extends ImportEntityManagerController[SubjectBlueprint]{

  type SFI = SubjectFormInitializer[SubjectBlueprint, CourseBlueprint, ResourceBlueprint, EventBlueprint]

  @FXML var shortNameColumn: TableColumn[SubjectBlueprint, String] = _
  @FXML var courseColumn: TableColumn[SubjectBlueprint, String] = _
  @FXML var quarterColumn: TableColumn[SubjectBlueprint, String] = _
  @FXML var eventsColumn: TableColumn[SubjectBlueprint, Int] = _

  private val detailsController: ImportSubjectDetailsController = {
    val controller = new ImportSubjectDetailsController
    Utils.loadScene(new ViewFactory(FXMLPaths.ImportSubjectDetailsView), controller)
    controller
  }

  override def additionalInitialization(): Unit = {
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

  override def additionalTableSetup(): Unit = {
    shortNameColumn.setCellValueFactory(cell => {
      if (cell.getValue != null) new SimpleStringProperty(cell.getValue.shortName)
      else new SimpleStringProperty()
    })

    courseColumn.setCellValueFactory(cell => {
      if (cell.getValue != null && cell.getValue.course.nonEmpty)
        new SimpleStringProperty(cell.getValue.course.get.name)
      else new SimpleStringProperty()
    })

    quarterColumn.setCellValueFactory(cell => {
      if (cell.getValue != null && cell.getValue.quarter.nonEmpty)
        new SimpleStringProperty(cell.getValue.quarter.get.toShortString)
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

    addContent(editableImportJob.subjects)

    table.getSortOrder.add(shortNameColumn.asInstanceOf[TableColumn[SubjectBlueprint, _]])
  }

  override def newEntity: Option[SubjectBlueprint] = {
    promptSubjectForm()
  }

  private def promptSubjectForm(sfi: SFI): Option[SubjectBlueprint] =
    promptSubjectForm(Some(sfi))

  private def promptSubjectForm(osfi: Option[SFI] = None): Option[SubjectBlueprint] = {
    val subjectForm = new SubjectDescriptorFormController(editableImportJob.courses, editableImportJob.resources)

    subjectForm.setStage(Utils.promptBoundWindow(
      language.getItemOrElse("subjectForm_windowTitle", "Create new Subject"),
      newButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.SubjectForm),
      subjectForm))

    val osd = subjectForm.waitFormResult

    if (osd.nonEmpty) {
      val sb = SubjectBlueprint.fromDescriptor(osd.get)

      sb.course = osd.get.course

      val eventsByType: mutable.Map[EventType, mutable.Set[EventBlueprint]] = new mutable.HashMap
      EventTypes.commonEventTypes.foreach(eventsByType.put(_, new mutable.HashSet))

      osd.get.events.map(ed => {
        val eb = EventBlueprint.fromDescriptor(ed)

        eb.subject = sb
        eb.course = sb.course
        eb.neededResource = ed.neededResource

        sb.events_+=(eb)

        eventsByType(eb.eventType) += eb
      })

      //This will not set per event type incompatibilities, only subject ones.
      sb.eventTypeIncompatibilities.foreach(eti => {
        eventsByType(eti.getFirstType).foreach(e1 => {
          eventsByType(eti.getSecondType).foreach(e2 => {
            e1.addIncompatibility(e2)
          })
        })
      })

      Some(sb)
    }
    else None
  }

  override def editEntity(entity: SubjectBlueprint): Option[SubjectBlueprint] = {
    ???
  }

  override def deleteEntity(entity: SubjectBlueprint): Unit = {
    importJobEditorController.notifySubjectDeletion(entity)
  }

  override def showAdditionalInformation(entity: SubjectBlueprint): Unit = {
    detailsController.setFromSubjectBlueprint(entity)
    showDetailBox()
  }

  override def clearAdditionalInformation(): Unit = {
    detailsController.clear()
  }

  override protected def notifySingleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_subject_singleDeleteButton", "Delete Subject"))
  }

  override protected def notifyMultipleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_subject_multipleDeleteButton", "Delete Subjects"))
  }
}
