package control.imprt

import app.FXMLPaths
import factory.ViewFactory
import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn}
import model.blueprint.ResourceBlueprint
import util.Utils

class ImportResourcesManagerController(/*importJobController: ImportJobEditorController*/)
  extends ImportEntityManagerController[ResourceBlueprint]{

  @FXML var nameColumn: TableColumn[ResourceBlueprint, String] = _
  @FXML var capacityColumn: TableColumn[ResourceBlueprint, Int] = _

  private val detailsController: ImportResourceDetailsController = {
    val controller = new ImportResourceDetailsController
    Utils.loadScene(new ViewFactory(FXMLPaths.ImportResourceDetailsView), controller)
    controller
  }

  def additionalInitialization(): Unit = {
    detailBoxContent_=(detailsController.mainBox)

    nameColumn = new TableColumn()
    capacityColumn = new TableColumn()
  }

  override def initializeContentLanguage(): Unit = {
    newButton.setText(language.getItemOrElse("import_resource_newButton", "New Resource"))
    editButton.setText(language.getItemOrElse("import_resource_editButton", "Edit Resource"))
    deleteButton.setText(language.getItemOrElse("import_resource_singleDeleteButton", "Delete Resource"))

    table.setPlaceholder(new Label(language.getItemOrElse("import_resource_tablePlaceholder", "No Resources")))

    nameColumn.setText(language.getItemOrElse("import_resource_shortNameColumn", "Name"))
    capacityColumn.setText(language.getItemOrElse("import_resource_typeColumn", "Capacity"))
  }

  def additionalTableSetup(): Unit = {
    nameColumn.setCellValueFactory(cell => {
      if (cell.getValue != null) new SimpleStringProperty(cell.getValue.name)
      else new SimpleStringProperty()
    })

    capacityColumn.setCellValueFactory(cell => {
      val cellValue =
        if (cell.getValue != null) new SimpleIntegerProperty(cell.getValue.capacity)
        else new SimpleIntegerProperty()

      cellValue.asInstanceOf[ObservableValue[Int]]
    })

    addColumn(nameColumn)
    addColumn(capacityColumn)
  }

  def newEntity: Option[ResourceBlueprint] = {
    //promptResourceForm
    ???
  }

  /*private def promptResourceForm: Option[ResourceBlueprint] = {
    val resourceForm = new ResourceFormController

    resourceForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("resourceForm_windowTitle", "Create new Resource"),
      newButton.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.ResourceForm),
      resourceForm))

    resourceForm.waitFormResult
  }*/

  def editEntity(entity: ResourceBlueprint): Option[ResourceBlueprint] = {
    ???
  }

  def deleteEntity(entity: ResourceBlueprint): Unit = {
    ???
  }

  def showAdditionalInformation(entity: ResourceBlueprint): Unit = {
    //TODO
    showDetailBox()
  }

  override protected def notifySingleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_resource_singleDeleteButton", "Delete Resource"))
  }

  override protected def notifyMultipleSelection(): Unit = {
    deleteButton.setText(language.getItemOrElse("import_resource_multipleDeleteButton", "Delete Resources"))
  }
}
