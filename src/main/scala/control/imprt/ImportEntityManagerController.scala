package control.imprt

import java.net.URL
import java.util.ResourceBundle

import app.{AppSettings, Language}
import control.Controller
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.{Button, SelectionMode, TableColumn, TableView}
import javafx.scene.layout.{HBox, VBox}

import scala.collection.JavaConverters

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
  def clearAdditionalInformation(): Unit

  protected def notifySingleSelection(): Unit
  protected def notifyMultipleSelection(): Unit

  protected def addColumn(column: TableColumn[E,_]): Boolean = table.getColumns.add(column)

  def setupTable(): Unit = {
    table.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
    table.getSelectionModel.selectedItemProperty().addListener( new ChangeListener[E] {
      override def changed(observable: ObservableValue[_ <: E], oldValue: E, newValue: E): Unit = {
        if (table.getSelectionModel.getSelectedCells.size() > 1) {
          editButton.setDisable(true)
          clearAdditionalInformation()
          notifyMultipleSelection()
        }
        else {
          editButton.setDisable(false)
          if (newValue != null) showAdditionalInformation(newValue)
          notifySingleSelection()
        }
      }
    })
    additionalTableSetup()
  }

  def addContent(content: Iterable[E]): Unit = {
    table.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(content)))
  }

  def bindButtons(): Unit = {
    newButton.setOnAction(actionEvent => {
      val entity = newEntity

      if (entity.nonEmpty) {
        table.getItems.add(entity.get)
        table.sort()
        selectEntity(entity.get)
      }

      actionEvent.consume()
    })

    editButton.setOnAction(actionEvent => {
      val editedEntity = editEntity(table.getSelectionModel.getSelectedItem)

      if (editedEntity.nonEmpty){
        table.getItems.remove(table.getSelectionModel.getSelectedIndex)
        table.getItems.add(editedEntity.get)
        table.sort()
        table.refresh() //This shouldn't be here, but the table doesn't reflect changes otherwise.
        selectEntity(editedEntity.get)
      }

      actionEvent.consume()
    })

    deleteButton.setOnAction(actionEvent => {
      val entities = table.getSelectionModel.getSelectedItems

      entities.forEach(deleteEntity(_))
      table.getItems.removeAll(entities)

      actionEvent.consume()
    })
  }

  private def selectEntity(entity: E): Unit = {
    table.getSelectionModel.clearSelection()
    table.scrollTo(entity)
    table.getSelectionModel.select(entity)
  }

  def hideDetailBox(): Unit = if (showingDetails) {
    mainBox.getChildren.remove(detailBox)
    showingDetails = false
  }

  def showDetailBox(): Unit = if (!showingDetails) {
    mainBox.getChildren.add(detailBox)
    showingDetails = true
  }
  
  def detailBoxContent_=(content: Node): Unit = {
    detailBox.getChildren.clear()
    detailBox.getChildren.add(content)
  }
}
