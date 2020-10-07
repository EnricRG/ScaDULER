package control.manage

import java.net.URL
import java.util.ResourceBundle

import control.{ChildStageController, MainController}
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.{Button, SelectionMode, TableColumn, TableView}
import javafx.stage.Stage

import scala.collection.JavaConverters

abstract class EntityManagerController2[E](mainController: MainController)
  extends ChildStageController(mainController) {

  @FXML var table: TableView[E] = _

  @FXML var addButton: Button = _
  @FXML var editButton: Button = _
  @FXML var removeButton: Button = _

  def this(stage: Stage, mainController: MainController) = {
    this(mainController)
    setStage(stage)
  }

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initializeContentLanguage()
    setupTable()
    bindActions()
  }

  /** Should be used to initialize static text fields that depend on the application language. */
  protected def initializeContentLanguage(): Unit

  protected def setupTable(): Unit = {
    table.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
    table.getSelectionModel.selectedItemProperty.addListener(observable => {
      if (table.getSelectionModel.getSelectedCells.size > 1) {
        editButton.setDisable(true)
        notifyMultipleSelection()
      }
      else {
        editButton.setDisable(false)
        notifySingleSelection()
      }
    })

    table.setStyle("-fx-selection-bar: lightblue;")

    additionalTableSetup()
  }

  /** Should be used to initialize additional entity table settings. */
  protected def additionalTableSetup(): Unit

  /** Should be used to initialize interaction fields (i.e. buttons, lists). */
  protected def bindActions(): Unit = {
    addButton.setOnAction(event => {
      addButtonAction()
      event.consume()
    })

    editButton.setOnAction(event => {
      val selectedEntity = getSelectedEntity
      if (selectedEntity.nonEmpty)
        editButtonAction(selectedEntity.get)
      event.consume()
    })

    removeButton.setOnAction(event => {
      removeButtonAction(getSelectedEntities)
      event.consume()
    })

    editButton.setDisable(true)
    removeButton.setDisable(true)
  }

  protected final def addColumn(column: TableColumn[E,_]): Unit = {
    table.getColumns.add(column)
  }

  protected final def addEntity(entity: E): Unit = {
    table.getItems.add(entity)
  }

  protected final def removeEntity(entity: E): Unit = {
    table.getItems.remove(entity)
  }

  protected final def fillTable(entities: Iterable[E]): Unit = {
    table.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(entities)))
  }

  protected final def getSelectedEntity: Option[E] =
    table.getSelectionModel.getSelectedItem match {
      case e if e != null => Some(e)
      case _ => None
    }

  protected final def getSelectedEntities: Iterable[E] =
    JavaConverters.collectionAsScalaIterable(table.getSelectionModel.getSelectedItems)

  protected def addButtonAction(): Unit

  protected def editButtonAction(entity: E): Unit

  protected def removeButtonAction(entities: Iterable[E]): Unit

  protected def notifySingleSelection(): Unit

  protected def notifyMultipleSelection(): Unit
}
