package control.manage

import util.Utils
import java.util

import app.{AppSettings, FXMLPaths}
import control.form.FormController2
import control.schedule.ResourceAvailabilityController
import factory.ViewFactory
import javafx.beans.property.{SimpleObjectProperty, SimpleStringProperty}
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml.FXML
import javafx.geometry.{Insets, Pos}
import javafx.scene.Node
import javafx.scene.control._
import javafx.scene.input.KeyCode
import javafx.scene.layout.Region.USE_COMPUTED_SIZE
import javafx.scene.layout.{HBox, Priority}
import javafx.stage.{Modality, Stage}
import misc.Warning
import model.ResourceLike
import model.blueprint.ResourceBlueprint

import scala.collection.JavaConverters
import scala.collection.mutable.ArrayBuffer

class ResourceManagerController[R <: ResourceLike](
  initialResources: Iterable[R]) extends FormController2[(Iterable[ResourceBlueprint], Iterable[R])] {

  @FXML var searchResourceField: TextField = _

  @FXML var resourceTable: TableView[ResourceLike] = _
  @FXML var resourceTable_nameColumn: TableColumn[ResourceLike, String] = _
  @FXML var resourceTable_capacityColumn: TableColumn[ResourceLike, Int] = _
  @FXML var resourceTable_availabilityColumn: TableColumn[ResourceLike, Any] = _

  @FXML var addResourceButton: Button = _
  @FXML var resourceNameField: TextField = _
  @FXML var resourceCapacityField: TextField = _

  @FXML var deleteResourceButton: Button = _
  @FXML var minusOneButton: Button = _
  @FXML var plusOneButton: Button = _

  private val _tableResources: util.ArrayList[ResourceLike] =
    new util.ArrayList(JavaConverters.asJavaCollection(initialResources))

  private val _addedResources: ArrayBuffer[ResourceBlueprint] = new ArrayBuffer
  private val _removedResources: ArrayBuffer[R] = new ArrayBuffer

  def this(resources: Iterable[R], stage: Stage) {
    this(resources)
    setStage(stage)
  }

  override protected def initializeContentLanguage(): Unit = {
    searchResourceField.setPromptText(AppSettings.language.getItemOrElse(
      "manageResources_searchResourceField",
      "search resources"))

    resourceTable.setPlaceholder(new Label(AppSettings.language.getItemOrElse(
      "resourceTable_placeholder",
      "No resources")))

    resourceTable_nameColumn.setText(AppSettings.language.getItemOrElse(
      "manageResources_nameColumn",
      "Name"))

    resourceTable_capacityColumn.setText(AppSettings.language.getItemOrElse(
      "manageResources_capacityColumn",
      "Capacity"))

    resourceTable_availabilityColumn.setText(AppSettings.language.getItemOrElse(
      "manageResources_availabilityColumn",
      "Availability"))

    addResourceButton.setText(AppSettings.language.getItemOrElse(
      "manageResources_addButton",
      "Add Resource"))

    deleteResourceButton.setText(AppSettings.language.getItemOrElse(
      "manageResources_deleteButton",
      "Delete Resource"))

    resourceCapacityField.setPromptText(AppSettings.language.getItemOrElse(
      "manageResources_capacityField",
      "capacity"))

    minusOneButton.setText(AppSettings.language.getItemOrElse("manageResources_subButton", "-1"))

    plusOneButton.setText(AppSettings.language.getItemOrElse("manageResources_sumButton", "+1"))
  }

  override protected def setupViews(): Unit = {
    resourceTable.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)

    resourceTable_nameColumn.setCellValueFactory(cell => new SimpleStringProperty(cell.getValue.name))
    resourceTable_capacityColumn.setCellValueFactory(cell => new SimpleObjectProperty(cell.getValue.capacity))
    resourceTable_availabilityColumn.setCellFactory(
      (param: TableColumn[ResourceLike, Any]) => new TableCell[ResourceLike, Any]() {
        override protected def updateItem(item: Any, empty: Boolean): Unit = {
          super.updateItem(item, empty)
          if (!empty)
            this.setGraphic(generateResourceManageButton(this.getTableRow.getItem.asInstanceOf[ResourceLike]))
          else {
            setGraphic(null)
            setText(null)
          }
        }
      }
    )

    resourceTable.getItems.addAll(_tableResources)
  }

  override protected def bindActions(): Unit = {
    searchResourceField.textProperty.addListener((observable, oldValue, newValue) => {
      filterResourceTable(searchResourceField.getText.trim.toLowerCase)
    })

    addResourceButton.setOnAction(actionEvent => {
      addResource()
      actionEvent.consume()
    })

    deleteResourceButton.setOnAction(actionEvent => {
      deleteResource()
      actionEvent.consume()
    })

    minusOneButton.setOnAction(actionEvent => {
      decrementByOne()
      actionEvent.consume()
    })

    plusOneButton.setOnAction(actionEvent => {
      incrementByOne()
      actionEvent.consume()
    })
  }

  private def generateResourceManageButton(r: ResourceLike): Node = {
    val hbox: HBox = new HBox

    hbox.setAlignment(Pos.CENTER)
    hbox.setMaxWidth(USE_COMPUTED_SIZE)

    val button: Button = new Button(AppSettings.language.getItemOrElse("manage", "Manage"))

    button.setPadding(new Insets(1))
    button.setMaxWidth(Double.MaxValue)
    button.setMaxHeight(Double.MaxValue)
    button.setOnAction(actionEvent => manageResourceAvailability(r))

    HBox.setHgrow(button, Priority.ALWAYS)
    hbox.getChildren.add(button)

    hbox
  }

  private def manageResourceAvailability(r: ResourceLike): Unit = {
    val controller: ResourceAvailabilityController = new ResourceAvailabilityController(r)

    val stage: Stage = Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse(
        "manageResources_availabilityPrompt",
        "Manage Availability"),
      resourceTable.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[ResourceAvailabilityController](FXMLPaths.ResourceAvailabilityManager),
      controller)

    controller.setStage(stage)

    //TODO move this to ResourceAvailabilityController
    stage.getScene.setOnKeyReleased(keyEvent => {
      val keyCode: KeyCode = keyEvent.getCode

      if (keyCode == KeyCode.ESCAPE)
        controller.clearSelection()
      else if (keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE)
        controller.unsetSelection()
    })

    controller.show()
  }

  private def incrementByOne(): Unit = {
    val selection: ObservableList[ResourceLike] = resourceTable.getSelectionModel.getSelectedItems
    selection.forEach((resource: ResourceLike) => resource.incrementCapacity(1))
    updateTableView()
  }

  private def decrementByOne(): Unit = {
    val selection: ObservableList[ResourceLike] = resourceTable.getSelectionModel.getSelectedItems
    selection.forEach((resource: ResourceLike) => resource.decrementCapacity(1))
    updateTableView()
  }

  //pre: text not null
  private def filterResourceTable(text: String): Unit = {
    val filteredResources: ObservableList[ResourceLike] = FXCollections.observableArrayList(_tableResources)

    //if search field is not blank, remove all rows that resource's name does not contain fields content as a substring
    if (!text.trim.isEmpty) filteredResources.removeIf(
      (resource: ResourceLike) => !resource.name.toLowerCase.contains(text))

    resourceTable.setItems(filteredResources)
  }

  private def addResource(): Unit = {
    if (!warnings) {
      val r: ResourceBlueprint = new ResourceBlueprint
      r.name = resourceNameField.getText.trim
      r.capacity = getCapacityFieldValue

      _addedResources += r
      updateCourseInTableView(r)
      clearInputFields()
    }
  }

  //post: return capacity field value if it is a number, Integer.MIN_VALUE otherwise
  private def getCapacityFieldValue: Int = {
    try {
      resourceCapacityField.getText.toInt
    } catch {
      case nfe: NumberFormatException =>
        Integer.MIN_VALUE
    }
  }

  private def clearInputFields(): Unit = {
    resourceNameField.clear()
    resourceCapacityField.clear()
  }

  private def updateTableView(): Unit = {
    resourceTable.refresh()
  }

  //pre: r not null
  private def updateCourseInTableView(r: ResourceLike): Unit = {
    if (resourceTable.getItems.contains(r)) updateTableView()
    else {
      _tableResources.add(r)
      resourceTable.getItems.add(r)
    }
  }

  private def deleteResource(): Unit = {
    val selection: ObservableList[ResourceLike] = resourceTable.getSelectionModel.getSelectedItems
    if (!warnings(resourcesCanBeDeleted(selection))) {
      selection.forEach{
        case r: ResourceBlueprint => _addedResources -= r
        case r: ResourceLike => _removedResources += r.asInstanceOf[R]
        case _ =>
      }
      _tableResources.removeAll(selection)
      resourceTable.getItems.removeAll(selection)
    }
  }

  override protected def checkWarnings: Option[Warning] = resourceCanBeCreated

  override def waitFormResult: Option[(Iterable[ResourceBlueprint], Iterable[R])] = {
    showAndWait()
    if(_addedResources.nonEmpty || _removedResources.nonEmpty)
      Some((_addedResources, _removedResources))
    else
      None
  }

  //pre: name and capacity not null
  private def resourceCanBeCreated: Option[Warning] = {
    val capacity: Integer = getCapacityFieldValue

    if (resourceNameField.getText.trim.isEmpty)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_resourceNameCannotBeEmpty",
        "Resource name cannot be empty.")))
    else if (capacity == Integer.MIN_VALUE)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_resourceCapacityNaN",
        "The capacity is not a number.")))
    else if (capacity.compareTo(AppSettings.minCapacityPerResource) < 0) { //if capacity is lower than minimum required.
      Some(new Warning(capacity + AppSettings.language.getItemOrElse(
        "warning_resourceCapacityMin",
        " is lower than the minimum allowed quantity") +
        " (" + AppSettings.minCapacityPerResource + ")."))
    }
    else
      None
  }

  private def resourcesCanBeDeleted(selection: ObservableList[ResourceLike]): Option[Warning] = {
    if (selection.isEmpty)
      Some(new Warning(AppSettings.language.getItemOrElse(
        "warning_resourcesNotSelected",
        "No resource has been selected.")))
    else
      None
  }
}
