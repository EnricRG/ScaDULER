package control.form

import java.net.URL
import java.util
import java.util.ResourceBundle

import app.AppSettings
import control.StageController
import javafx.beans.property.SimpleStringProperty
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml.FXML
import javafx.scene.control._
import model.EventLike

import scala.collection.JavaConverters
import scala.collection.mutable.ArrayBuffer

class EventIncompatibilityFormController[E <: EventLike[_,_,_,_]](
  incompatibilities: ArrayBuffer[E],
  events: Iterable[E]) extends StageController {

  @FXML var assignedIncompatibilitiesTag: Label = _
  @FXML var incompatibilityTable: TableView[E] = _
  @FXML var incompatibilityTable_nameColumn: TableColumn[E, String] = _
  @FXML var selectAllAssigned: Button = _

  @FXML var addButton: Button = _
  @FXML var removeButton: Button = _

  @FXML var generalEventListTag: Label = _
  @FXML var eventSearchBox: TextField = _
  @FXML var generalEventTable: TableView[E] = _
  @FXML var generalEventTable_nameColumn: TableColumn[E, String] = _
  @FXML var selectAllUnassigned: Button = _

  private val allEvents: util.ArrayList[E] = {
    val x = new util.ArrayList(JavaConverters.asJavaCollection(events))
    x.removeAll(JavaConverters.asJavaCollection(incompatibilities))
    x
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    initializeContentLanguage()
    setupViews()
    bindActions()
  }

  private def initializeContentLanguage(): Unit = {
    assignedIncompatibilitiesTag.setText(AppSettings.language.getItemOrElse(
      "manageIncompatibilities_assignIncompatibilities",
      "Assigned Incompatibilities"))

    incompatibilityTable.setPlaceholder(new Label(AppSettings.language.getItemOrElse(
      "manageIncompatibilities_incompatibilityTablePlaceholder",
      "No incompatibilities")))

    incompatibilityTable_nameColumn.setText(AppSettings.language.getItemOrElse(
      "manageIncompatibilities_nameColumnHeader",
      "Name"))

    selectAllAssigned.setText(AppSettings.language.getItemOrElse(
      "manageIncompatibilities_selectAllIncompatibilities",
      "Select All Incompatibilities"))

    generalEventListTag.setText(AppSettings.language.getItemOrElse(
      "manageIncompatibilities_allEventsHeader",
      "All Events"))

    eventSearchBox.setPromptText(AppSettings.language.getItemOrElse(
      "manageIncompatibilities_searchEvent",
      "Enter event name"))

    generalEventTable.setPlaceholder(new Label(AppSettings.language.getItemOrElse(
      "eventListPlaceholder",
      "No events")))

    generalEventTable_nameColumn.setText(AppSettings.language.getItemOrElse(
      "manageIncompatibilities_nameColumnHeader",
      "Name"))

    selectAllUnassigned.setText(AppSettings.language.getItemOrElse(
      "manageIncompatibilities_selectAllEvents",
      "Select All Events"))
  }

  private def setupViews(): Unit = {
    incompatibilityTable.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
    incompatibilityTable_nameColumn.setCellValueFactory(cell => {
      val e: E = cell.getValue

      if(e != null) new SimpleStringProperty(e.name)
      else new SimpleStringProperty()
    })

    generalEventTable.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
    generalEventTable_nameColumn.setCellValueFactory(cell => {
      val e: E = cell.getValue

      if(e != null) new SimpleStringProperty(e.name)
      else new SimpleStringProperty()
    })

    incompatibilityTable.getItems.addAll(JavaConverters.asJavaCollection(incompatibilities))
    generalEventTable.getItems.addAll(allEvents)
  }

  private def filterGeneralEventTable(text: String): Unit = {
    val filteredResources: ObservableList[E] = FXCollections.observableArrayList(allEvents)

    //if search field is not blank, remove all rows that event's name does not contain field's content as a substring
    if (!text.trim.isEmpty) filteredResources.removeIf(
      event => !event.name.toLowerCase.contains(text.toLowerCase))

    generalEventTable.setItems(filteredResources)
  }

  private def bindActions(): Unit = {
    selectAllAssigned.setOnAction( actionEvent => {
      incompatibilityTable.getSelectionModel.selectAll()
      actionEvent.consume()
    })
    selectAllUnassigned.setOnAction( actionEvent => {
      generalEventTable.getSelectionModel.selectAll()
      actionEvent.consume()
    })
    eventSearchBox.textProperty.addListener( (observable, oldValue, newValue) => {
      filterGeneralEventTable(eventSearchBox.getText.trim)
    })
    addButton.setOnAction( actionEvent => {
      addSelectedIncompatibilities()
      actionEvent.consume()
    })
    removeButton.setOnAction( actionEvent => {
      removeSelectedIncompatibilities()
      actionEvent.consume()
    })
  }

  private def addSelectedIncompatibilities(): Unit = {
    val selection: ObservableList[E] = generalEventTable.getSelectionModel.getSelectedItems

    allEvents.removeAll(selection)

    incompatibilityTable.getItems.addAll(selection)
    incompatibilities ++= JavaConverters.collectionAsScalaIterable(selection)

    filterGeneralEventTable(eventSearchBox.getText.trim)
  }

  private def removeSelectedIncompatibilities(): Unit = {
    val selection: ObservableList[E] = incompatibilityTable.getSelectionModel.getSelectedItems

    allEvents.addAll(selection)

    incompatibilities --= JavaConverters.collectionAsScalaIterable(selection)
    incompatibilityTable.getItems.removeAll(selection)

    filterGeneralEventTable(eventSearchBox.getText.trim)
  }
}

