package control.form

import java.net.URL
import java.util.ResourceBundle

import app.AppSettings
import control.StageController
import javafx.beans.property.SimpleStringProperty
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml.FXML
import javafx.scene.control._
import model.EventLike

import scala.collection.{JavaConverters, mutable}

//constructor pre: `events` contains all `incompatibilities` elements.
class EventIncompatibilityFormController[E <: EventLike[_,_,_,_]](
  incompatibilities: Iterable[E],
  events: Iterable[E])
  extends StageController {

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

  private val _incompatibilities: mutable.HashSet[E] = {
    val hashSet = new mutable.HashSet[E]

    hashSet ++= incompatibilities

    hashSet
  }

  private val _newIncompatibilities: mutable.HashSet[E] =
    new mutable.HashSet[E]

  private val _removedIncompatibilities: mutable.HashSet[E] =
    new mutable.HashSet[E]

  private val _allEvents: mutable.HashSet[E] = {
    val hashSet = new mutable.HashSet[E]

    hashSet ++= events
    hashSet --= incompatibilities

    hashSet
  }

  private var _edited: Boolean = false

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

    incompatibilityTable.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(incompatibilities)))

    generalEventTable.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(_allEvents)))
  }

  private def filterGeneralEventTable(text: String): Unit = {
    val filteredResources: ObservableList[E] =
      FXCollections.observableArrayList(JavaConverters.asJavaCollection(_allEvents))

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
      _edited = true
      actionEvent.consume()
    })
    removeButton.setOnAction( actionEvent => {
      removeSelectedIncompatibilities()
      _edited = true
      actionEvent.consume()
    })
  }

  private def addSelectedIncompatibilities(): Unit = {
    val selection: ObservableList[E] = generalEventTable.getSelectionModel.getSelectedItems

    _allEvents --= JavaConverters.collectionAsScalaIterable(selection)
    _newIncompatibilities ++= JavaConverters.collectionAsScalaIterable(selection)

    incompatibilityTable.getItems.addAll(selection)

    filterGeneralEventTable(eventSearchBox.getText.trim)
  }

  private def removeSelectedIncompatibilities(): Unit = {
    val selection: ObservableList[E] = incompatibilityTable.getSelectionModel.getSelectedItems

    _allEvents ++= JavaConverters.collectionAsScalaIterable(selection)
    _removedIncompatibilities ++= JavaConverters.collectionAsScalaIterable(selection)

    incompatibilityTable.getItems.removeAll(selection)

    filterGeneralEventTable(eventSearchBox.getText.trim)
  }

  def incompatibilitiesChanged: Boolean =
    _edited && (_newIncompatibilities.nonEmpty || _removedIncompatibilities.nonEmpty)

  //first element of the pair contains newly added incompatibilities,
  //second element contains the removed ones (present at form start and not present on form finish).
  def incompatibilities: (Iterable[E], Iterable[E]) =
    (_newIncompatibilities -- incompatibilities, _removedIncompatibilities -- incompatibilities)
}

