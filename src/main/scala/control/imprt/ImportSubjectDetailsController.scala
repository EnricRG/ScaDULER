package control.imprt

import java.net.URL
import java.util.ResourceBundle

import app.{AppSettings, Language}
import control.Controller
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn, TableView}
import javafx.scene.layout.{FlowPane, HBox}
import javafx.scene.paint
import model.Color
import model.blueprint.SubjectBlueprint

class ImportSubjectDetailsController extends Controller {

  type AF = (String, String)

  override def language: Language = AppSettings.language

  @FXML var mainBox: HBox = _

  @FXML var nameTag: Label = _
  @FXML var nameContent: Label = _

  @FXML var descriptionTag: Label = _
  @FXML var descriptionContent: Label = _

  @FXML var colorTag: Label = _
  @FXML var colorFrame: FlowPane = _

  @FXML var additionalFieldsTag: Label = _
  @FXML var additionalFieldsTable: TableView[AF] = _
  @FXML var fieldColumn: TableColumn[AF, String] = _
  @FXML var valueColumn: TableColumn[AF, String] = _

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initializeContentLanguage()
    setupTable()
  }

  private def initializeContentLanguage(): Unit = {
    nameTag.setText(language.getItemOrElse("import_subjectDetails_name", "Name"))
    descriptionTag.setText(language.getItemOrElse("import_subjectDetails_description", "Description"))
    colorTag.setText(language.getItemOrElse("import_subjectDetails_color", "Frame Color"))
    additionalFieldsTag.setText(language.getItemOrElse("import_subjectDetails_additionalFields", "Additional Fields"))
  }

  private def setupTable(): Unit = {
    additionalFieldsTable.setPlaceholder(new Label(
      language.getItemOrElse("import_subjectDetails_additionalFieldTablePlaceholder", "No additional fields")))
    additionalFieldsTable.setStyle("-fx-selection-bar: lightblue;")

    fieldColumn.setCellValueFactory(cell => new SimpleStringProperty(cell.getValue._1))
    valueColumn.setCellValueFactory(cell => new SimpleStringProperty(cell.getValue._2))

    additionalFieldsTable.getSortOrder.add(fieldColumn.asInstanceOf[TableColumn[AF, _]])
  }

  def name: String =
    nameContent.getText

  def name_=(name: String): Unit =
    nameContent.setText(name)

  def description: String =
    descriptionContent.getText

  def description_=(description: String): Unit = {
    descriptionContent.setText(description)
  }

  def color_=(color: paint.Color): Unit = {
    colorFrame.setStyle("-fx-border-width: 2; -fx-border-color: #" + color.toString.substring(2) + ";")
  }

  def color_=(color: Color): Unit = color_=(color.toJFXColor)

  def addAdditionalField(additionalField: AF): Unit = {
    additionalFieldsTable.getItems.add(additionalField)
  }

  def setAdditionalFields(additionalFields: Iterable[AF]): Unit = {
    additionalFieldsTable.getItems.clear()
    additionalFields.foreach(addAdditionalField)
    additionalFieldsTable.sort()
  }

  def setFromSubjectBlueprint(sb: SubjectBlueprint): Unit = {
    name = sb.name
    description = sb.description
    if(sb.color.nonEmpty) color_=(sb.color.get)
    setAdditionalFields(sb.additionalFields)
  }

  def clear(): Unit = {
    name = ""
    description = ""
    colorFrame.setStyle("")
    additionalFieldsTable.getItems.clear()
  }
}
