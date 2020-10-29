package control.manage

import java.net.URL
import java.util.ResourceBundle

import control.StageController
import control.misc.RemoveMode
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.stage.Stage

import scala.collection.JavaConverters

//TODO change to trait
abstract class EntityManagerController2[E] extends StageController {

  @FXML var table: TableView[E] = _

  @FXML var addButton: Button = _
  @FXML var editButton: Button = _
  @FXML var removeButton: Button = _

  def this(stage: Stage) = {
    this()
    setStage(stage)
  }

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    setupTable()
    bindActions()
    initializeContentLanguage()
  }

  /** Should be used to initialize static text fields that depend on the application language. */
  protected def initializeContentLanguage(): Unit

  protected def setupTable(): Unit = {
    table.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)

    table.setRowFactory(_ => new TableRow[E] {
      setOnMouseClicked(_ => updateSelection())
    })

    table.setStyle("-fx-selection-bar: lightblue;")

    additionalTableSetup()
  }

  private def updateSelection(): Unit = {
    val nSelectedItems = table.getSelectionModel.getSelectedItems.size

    if (nSelectedItems > 1) {
      editButton.setDisable(true)
      removeButton.setDisable(false)
      notifyMultipleSelection()
    }
    else if(nSelectedItems == 1) {
      editButton.setDisable(false)
      removeButton.setDisable(false)
      notifySingleSelection()
    }
    else { //nSelectedItems == 0
      editButton.setDisable(true)
      removeButton.setDisable(true)
    }
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
      editButtonAction()
      event.consume()
    })

    removeButton.setOnAction(event => {
      removeButtonAction()
      event.consume()
    })

    editButton.setDisable(true)
    removeButton.setDisable(true)
  }

  protected def addButtonAction(): Unit = {
    val entity = newEntity

    if (entity.nonEmpty) {
      table.getItems.add(entity.get)
      table.sort()
      selectEntity(entity.get)
    }
  }

  protected def editButtonAction(): Unit = {
    val editTarget = selectedEntity

    if (editTarget.nonEmpty){
      val editedEntity = editEntity(editTarget.get)

      if (editedEntity.nonEmpty){
        table.getItems.remove(table.getSelectionModel.getSelectedIndex)
        table.getItems.add(editedEntity.get)
        table.sort()
        //table.refresh() //This shouldn't be here, but the table doesn't reflect changes otherwise.
        selectEntity(editedEntity.get)
      }
    }
  }

  protected def removeButtonAction(): Unit = {
    val removeMode = askRemoveMode

    if (removeMode.nonEmpty) {
      val selectedEntities = table.getSelectionModel.getSelectedItems

      selectedEntities.forEach(removeEntity(_, removeMode.get))

      table.getItems.removeAll(selectedEntities)
    }
  }

  protected def askRemoveMode: Option[RemoveMode]

  private def selectEntity(entity: E): Unit = {
    table.getSelectionModel.clearSelection()
    table.scrollTo(entity)
    table.getSelectionModel.select(entity)
  }

  protected final def addColumn(column: TableColumn[E,_]): Unit = {
    table.getColumns.add(column)
  }

  protected final def fillTable(entities: Iterable[E]): Unit = {
    table.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(entities)))
  }

  protected final def selectedEntity: Option[E] =
    table.getSelectionModel.getSelectedItem match {
      case e if e != null => Some(e)
      case _ => None
    }

  protected final def getSelectedEntities: Iterable[E] =
    JavaConverters.collectionAsScalaIterable(table.getSelectionModel.getSelectedItems)

  protected def newEntity: Option[E]

  protected def editEntity(entity: E): Option[E]

  protected def removeEntity(entity: E, removeMode: RemoveMode): Unit

  protected def notifySingleSelection(): Unit

  protected def notifyMultipleSelection(): Unit
}
