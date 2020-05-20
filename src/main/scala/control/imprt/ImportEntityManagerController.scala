package control.imprt

import java.net.URL
import java.util.ResourceBundle

import app.{AppSettings, Language}
import control.Controller
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.FXML
import javafx.scene.control.{Button, TableView}
import javafx.scene.layout.{HBox, VBox}

abstract class ImportEntityManagerController[E] extends Controller {

  override protected def language: Language = AppSettings.language

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
  def additionalTableSetup(): Unit

  def newEntity: Option[E]
  def editEntity(entity: E): Option[E]
  def deleteEntity(entity: E): Unit

  def showAdditionalInformation(entity: E): Unit

  protected def notifySingleSelection(): Unit
  protected def notifyMultipleSelection(): Unit

  def setupTable(): Unit = {
    /*table.getSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) => {
      if (newValue != null) showAdditionalInformation(newValue)
    })*/ //JavaFX8 doesn't like scala anonymous functions
    table.getSelectionModel.selectedItemProperty().addListener(new ChangeListener[E] {
      override def changed(observable: ObservableValue[_ <: E], oldValue: E, newValue: E): Unit = {
        if (newValue != null) showAdditionalInformation(newValue)
        if (table.getSelectionModel.getSelectedCells.size() > 1)
          notifyMultipleSelection()
        else
          notifySingleSelection()
      }
    })
    additionalTableSetup()
  }

  def bindButtons(): Unit = {
    newButton.setOnAction(actionEvent => {
      val entity = newEntity

      if (entity.nonEmpty) table.getItems.add(entity.get)

      actionEvent.consume()
    })

    editButton.setOnAction(actionEvent => {
      val editedEntity = editEntity(table.getSelectionModel.getSelectedItem)

      if (editedEntity.nonEmpty){
        table.getItems.remove(table.getSelectionModel.getSelectedIndex)
        table.getItems.add(editedEntity.get)
      }

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
