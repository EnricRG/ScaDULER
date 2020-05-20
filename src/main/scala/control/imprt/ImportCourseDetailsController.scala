package control.imprt

import java.net.URL
import java.util.ResourceBundle

import app.{AppSettings, Language}
import control.Controller
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.VBox

class ImportCourseDetailsController extends Controller {

  @FXML var mainBox: VBox = _

  @FXML var nameTag: Label = _
  @FXML var nameContent: Label = _

  @FXML var descriptionTag: Label = _
  @FXML var descriptionContent: Label = _

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initializeContentLanguage(AppSettings.language)
  }

  def initializeContentLanguage(language: Language): Unit = {
    nameTag.setText(language.getItemOrElse("import_course_nameTag", "Name"))
    descriptionTag.setText(language.getItemOrElse("import_course_descriptionTag", "Description"))
  }

  def name_=(s: String): Unit = nameContent.setText(s)

  def description_=(s: String): Unit = descriptionContent.setText(s)
}
