package control.imprt

import app.{AppSettings, FXMLPaths}
import control.form.{CreateResourceFormController, EditResourceFormController}
import factory.ViewFactory
import file.imprt.MutableImportJob
import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn}
import javafx.stage.Modality
import misc.Duration
import model.blueprint.ResourceBlueprint
import util.Utils

class ImportResourcesManagerController(
                                        importJobEditorController: ImportJobEditorController,
                                        editableImportJob: MutableImportJob             )
  extends ImportEntityManagerController[ResourceBlueprint] {

  @FXML var nameColumn: TableColumn[ResourceBlueprint, String] = _
  @FXML var capacityColumn: TableColumn[ResourceBlueprint, Int] = _
  @FXML var availabilityColumn: TableColumn[ResourceBlueprint, String] = _

  private val detailsController: ImportResourceDetailsController = {
    val controller = new ImportResourceDetailsController
    Utils.loadScene(new ViewFactory(FXMLPaths.ImportResourceDetailsView), controller)
    controller
  }

  override def additionalInitialization(): Unit = {
    detailBoxContent_=(detailsController.mainBox)

    nameColumn = new TableColumn()
    capacityColumn = new TableColumn()
    availabilityColumn = new TableColumn()
  }

  override def initializeContentLanguage(): Unit = {
    newButton.setText(language.getItemOrElse("import_resource_newButton", "New Resource"))
    editButton.setText(language.getItemOrElse("import_resource_editButton", "Edit Resource"))
    deleteButton.setText(language.getItemOrElse("import_resource_singleDeleteButton", "Delete Resource"))

    table.setPlaceholder(new Label(language.getItemOrElse("import_resource_tablePlaceholder", "No Resources")))

    nameColumn.setText(language.getItemOrElse("import_resource_nameColumn", "Name"))
    capacityColumn.setText(language.getItemOrElse("import_resource_capacityColumn", "Capacity"))
    availabilityColumn.setText(language.getItemOrElse("import_resource_availabilityColumn", "Availability"))
  }

  override def additionalTableSetup(): Unit = {
    nameColumn.setCellValueFactory(cell => {
      if (cell.getValue != null)
        new SimpleStringProperty(cell.getValue.name)
      else
        new SimpleStringProperty()
    })

    capacityColumn.setCellValueFactory(cell => {
      val cellValue =
        if (cell.getValue != null)
          new SimpleIntegerProperty(cell.getValue.capacity)
        else
          new SimpleIntegerProperty()

      cellValue.asInstanceOf[ObservableValue[Int]]
    })

    availabilityColumn.setCellValueFactory(cell => {
      val cellValue =
        if (cell.getValue != null)
          new SimpleStringProperty(Duration.asPrettyString(cell.getValue.availability.getNumberOfAvailableIntervals))
        else
          new SimpleStringProperty()

      cellValue.asInstanceOf[ObservableValue[String]]
    })

    addColumn(nameColumn)
    addColumn(capacityColumn)
    addColumn(availabilityColumn)

    addContent(editableImportJob.resources)

    table.getSortOrder.add(nameColumn.asInstanceOf[TableColumn[ResourceBlueprint, _]])
  }

  override def newEntity: Option[ResourceBlueprint] = {
    val nrb = promptNewResourceForm

    if(nrb.nonEmpty) importJobEditorController.notifyResourceCreation(nrb.get)

    nrb
  }

  private def promptNewResourceForm: Option[ResourceBlueprint] = {
    val resourceForm = new CreateResourceFormController

    resourceForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("resourceForm_create_windowTitle", "Create new Resource"),
      newButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.ResourceForm),
      resourceForm))

    val ord = resourceForm.waitFormResult //execution thread stops here.

    if(ord.nonEmpty){
      Some(ResourceBlueprint.fromDescriptor(ord.get))
    }
    else
      None
  }

  override def editEntity(entity: ResourceBlueprint): Option[ResourceBlueprint] = {
    promptEditResourceForm(entity)
  }

  private def promptEditResourceForm(resource: ResourceBlueprint): Option[ResourceBlueprint] = {
    val resourceForm = new EditResourceFormController(resource)

    resourceForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("resourceForm_edit_windowTitle", "Edit Resource"),
      newButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.ResourceForm),
      resourceForm))

    //This is fine because EditResourceFormController(resource) specification ensures that
    //if the form result is Some(x), x == resource, and that's what we want.
    resourceForm.waitFormResult //execution thread stops here.
  }

  override def deleteEntity(entity: ResourceBlueprint): Unit = {
    importJobEditorController.notifyResourceDeletion(entity)
  }

  override def showAdditionalInformation(entity: ResourceBlueprint): Unit = {
    detailsController.setFromResourceBlueprint(entity)
    showDetailBox()
  }

  override def clearAdditionalInformation(): Unit = {
    detailsController.clear()
  }

  override protected def notifySingleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_resource_singleDeleteButton", "Delete Resource"))
  }

  override protected def notifyMultipleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_resource_multipleDeleteButton", "Delete Resources"))
  }
}
