package control.imprt

import java.net.URL
import java.util.ResourceBundle

import app.{AppSettings, Language}
import control.Controller
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import model.blueprint.EventBlueprint

class ImportEventDetailsController extends Controller {

  override def language: Language = AppSettings.language

  @FXML var mainBox: VBox = _

  @FXML var descriptionTag: Label = _
  @FXML var descriptionContent: Label = _

  @FXML var subjectNameTag: Label = _
  @FXML var subjectNameContent: Label = _


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initializeContentLanguage()
  }

  private def initializeContentLanguage(): Unit = {
    descriptionTag.setText(language.getItemOrElse(
      "import_eventDetails_descriptionTag",
      "Description"))

    subjectNameTag.setText(language.getItemOrElse(
      "import_eventDetails_subjectNameTag",
      "Subject name"))
  }

  def description: String = descriptionContent.getText

  def description_=(s: String): Unit = descriptionContent.setText(s)

  def subjectName: String = subjectNameContent.getText

  def subjectName_=(s: String): Unit = subjectNameContent.setText(s)

  def setFromEventBlueprint(eb: EventBlueprint): Unit = {
    description = eb.description
    subjectName = if(eb.subject.nonEmpty) eb.subject.get.name else ""
  }

  def clear(): Unit ={
    description = ""
    subjectName = ""
  }
}
