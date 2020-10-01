package control.imprt

import app.FXMLPaths
import control.StageController
import control.form.{CreateSubjectLikeFormController, EditSubjectLikeFormController, SubjectLikeForm, SubjectLikeFormInitializer}
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

  type SFI = SubjectLikeFormInitializer[CourseBlueprint, EventBlueprint]
  type ED = SubjectLikeForm[CourseBlueprint, ResourceBlueprint]#ED

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
    val formResult = promptNewSubjectForm()

    if(formResult.nonEmpty) {
      val subjectBlueprint = SubjectBlueprint.fromDescriptorWithoutEvents(formResult.get)

      val eventsByType: mutable.Map[EventType, mutable.Set[EventBlueprint]] = new mutable.HashMap
      EventTypes.commonEventTypes.foreach(eventsByType.put(_, new mutable.HashSet))

      //create blueprints from descriptors and map them together.
      val descriptorToBlueprintMap: Map[ED, EventBlueprint] = formResult.get.events.map(ed => {
        val eventBlueprint = EventBlueprint.fromSubjectLikeFormDescriptor(ed)
        eventBlueprint.subject = subjectBlueprint

        eventsByType(eventBlueprint.eventType) += eventBlueprint
        subjectBlueprint.events_+=(eventBlueprint)

        (ed, eventBlueprint)
      }).toMap

      //add descriptor's incompatibilities to new blueprints.
      //As of this version, subject form descriptors cannot have discrete incompatibilities with other events.
      //Future proofing.
      descriptorToBlueprintMap.foreach(entry => {
        entry._1.incompatibilities
          .map(descriptorToBlueprintMap(_))
          .foreach(entry._2.addIncompatibility)
      })

      subjectBlueprint.eventTypeIncompatibilities.foreach(eti => {
        eventsByType(eti.getFirstType).foreach(e1 => {
          eventsByType(eti.getSecondType).foreach(e2 => {
            e1.addIncompatibility(e2)
          })
        })
      })

      importJobEditorController.notifySubjectEventsCreation(subjectBlueprint.events)
      importJobEditorController.notifySubjectCreation(subjectBlueprint)

      Some(subjectBlueprint)
    }
    else
      None
  }

  private def promptNewSubjectForm(): Option[SubjectLikeForm[CourseBlueprint, ResourceBlueprint]#SD] = {
    val subjectForm = new CreateSubjectLikeFormController(
      editableImportJob.courses,
      editableImportJob.resources)

    subjectForm.setStage(Utils.promptBoundWindow(
      language.getItemOrElse("subjectForm_windowTitle", "Create new Subject"),
      newButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[StageController](FXMLPaths.SubjectForm),
      subjectForm))

    subjectForm.waitFormResult
  }

  override def editEntity(entity: SubjectBlueprint): Option[SubjectBlueprint] = {
    val formResult = promptEditSubjectForm(entity)

    if(formResult.nonEmpty) {
      val (subjectBlueprint,addedEvents,removedEvents) = formResult.get

      importJobEditorController.notifyEventsDeletion(removedEvents)

      //create blueprints from descriptors and map them together.
      val descriptorToBlueprintMap: Map[ED, EventBlueprint] = addedEvents.map(ed => {
        val eventBlueprint = EventBlueprint.fromSubjectLikeFormDescriptor(ed)
        eventBlueprint.subject = subjectBlueprint

        subjectBlueprint.events_+=(eventBlueprint)

        (ed, eventBlueprint)
      }).toMap

      //add descriptor's incompatibilities to new blueprints.
      //As of this version, subject form descriptors cannot have discrete incompatibilities with other events.
      //Future proofing.
      descriptorToBlueprintMap.foreach(entry => {
        entry._1.incompatibilities
          .map(descriptorToBlueprintMap(_))
          .foreach(entry._2.addIncompatibility)
      })

      val subjectEventsByType = subjectBlueprint.events.groupBy(_.eventType)
      val newEventsByType = descriptorToBlueprintMap.values.groupBy(_.eventType)

      /*
      // inefficient, runs over all events instead of only the added ones
      subjectBlueprint.eventTypeIncompatibilities.foreach(eti =>
        subjectEventsByType.getOrElse(eti.getFirstType, Nil).foreach(e1 =>
          subjectEventsByType.getOrElse(eti.getSecondType, Nil).foreach(e2 =>
            e1.addIncompatibility(e2))))*/

      subjectBlueprint.eventTypeIncompatibilities.foreach(eti => {
        newEventsByType.getOrElse(eti.getFirstType, Nil).foreach(e1 =>
          subjectEventsByType.getOrElse(eti.getSecondType, Nil).foreach(e2 =>
            e1.addIncompatibility(e2)))

        newEventsByType.getOrElse(eti.getSecondType, Nil).foreach(e1 =>
          subjectEventsByType.getOrElse(eti.getFirstType, Nil).foreach(e2 =>
            e1.addIncompatibility(e2)))
      })

      Some(subjectBlueprint)
    }
    else
      None
  }

  private def promptEditSubjectForm(subject: SubjectBlueprint): Option[(SubjectBlueprint, Iterable[ED], Iterable[EventBlueprint])] = {
    //TODO remove type parameters, scala inference not working here right now.
    val subjectForm = new EditSubjectLikeFormController[SubjectBlueprint,CourseBlueprint,ResourceBlueprint,EventBlueprint](
      subject,
      editableImportJob.courses,
      editableImportJob.resources)

    subjectForm.setStage(Utils.promptBoundWindow(
      language.getItemOrElse("subjectForm_edit_windowTitle", "Edit Subject"),
      editButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[StageController](FXMLPaths.SubjectForm),
      subjectForm))

    subjectForm.waitFormResult
  }

  override def deleteEntity(entity: SubjectBlueprint): Unit = {
    importJobEditorController.notifySubjectDeletion(entity)
  }

  override def showAdditionalInformation(entity: SubjectBlueprint): Unit = {
    //detailsController.clear()
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
