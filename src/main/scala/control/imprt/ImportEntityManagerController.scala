package control.imprt

import java.net.URL
import java.util.ResourceBundle

import control.StageController
import javafx.fxml.FXML
import javafx.scene.control.{Button, TableView}
import javafx.scene.layout.{HBox, VBox}

abstract class ImportEntityManagerController[E] extends StageController {

  @FXML var mainBox: VBox = _

  @FXML var table: TableView[E] = _

  @FXML var newButton: Button = _
  @FXML var editButton: Button = _
  @FXML var deleteButton: Button = _

  @FXML var detailBox: HBox = _ //hidden by default
  private var showingDetails: Boolean = true

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    hideDetailBox()
    additionalInitialization()
    initializeContentLanguage()
    setupTable()
    bindButtons()
  }

  def additionalInitialization(): Unit
  def initializeContentLanguage(): Unit
  def setupTable(): Unit

  def newEntity: E
  def editEntity(entity: E): E
  def deleteEntity(entity: E): Unit

  def showAdditionalInformation(entity: E): Unit

  def bindButtons(): Unit = {
    newButton.setOnAction(actionEvent => {
      table.getItems.add(newEntity)

      actionEvent.consume()
    })

    editButton.setOnAction(actionEvent => {
      val editedEntity = editEntity(table.getSelectionModel.getSelectedItem)

      table.getItems.remove(table.getSelectionModel.getSelectedIndex)
      table.getItems.add(editedEntity)

      actionEvent.consume()
    })

    deleteButton.setOnAction(actionEvent => {
      val entities = table.getSelectionModel.getSelectedItems

      entities.forEach(deleteEntity(_))
      table.getItems.remove(entities)

      actionEvent.consume()
    })
  }

  def hideDetailBox(): Unit = if (showingDetails) {
    mainBox.getChildren.remove(detailBox)
    showingDetails = false
  }

  def showDetailBox(): Unit = if (!showingDetails) {
    mainBox.getChildren.add(detailBox)
    showingDetails = true
  }
}
