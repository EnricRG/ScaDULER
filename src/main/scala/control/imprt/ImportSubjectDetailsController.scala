package control.imprt

import java.net.URL
import java.util.ResourceBundle

import app.{AppSettings, Language}
import control.Controller
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn, TableView}
import javafx.scene.layout.{Pane, VBox}
import javafx.scene.paint
import model.Color
import model.blueprint.SubjectBlueprint

class ImportSubjectDetailsController extends Controller {

  type AF = (String, String)

  override def language: Language = AppSettings.language

  @FXML var mainBox: VBox = _

  @FXML var nameTag: Label = _
  @FXML var nameContent: Label = _

  @FXML var descriptionTag: Label = _
  @FXML var descriptionContent: Label = _

  @FXML var colorTag: Label = _
  @FXML var colorFrame: Pane = _

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

    fieldColumn.setCellValueFactory(cell => new SimpleStringProperty(cell.getValue._1))
    valueColumn.setCellValueFactory(cell => new SimpleStringProperty(cell.getValue._2.toString))

    additionalFieldsTable.getSortOrder.add(fieldColumn.asInstanceOf[TableColumn[AF, _]])

    addAdditionalFieldsColumn(fieldColumn)
    addAdditionalFieldsColumn(valueColumn)
  }

  private def addAdditionalFieldsColumn(column: TableColumn[AF, _]): Unit = {
    additionalFieldsTable.getColumns.add(column)
  }

  def name_=(name: String): Unit = {
    nameContent.setText(name)
  }

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

  def addAdditionalFields(additionalFields: Iterable[AF]): Unit = {
    additionalFields.foreach(addAdditionalField)
    additionalFieldsTable.sort()
  }

  def setFromSubjectBlueprint(sb: SubjectBlueprint): Unit = {
    name_=(sb.name)
    description_=(sb.description)
    if(sb.color.nonEmpty) color_=(sb.color.get)
    addAdditionalFields(sb.additionalFields)
  }

  def clear(): Unit = {
    name_=("")
    description_=("")
    colorFrame.setStyle("")
    additionalFieldsTable.getItems.clear()
  }
}
