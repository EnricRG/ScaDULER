package control.imprt

import java.net.URL
import java.util.ResourceBundle

import app.{AppSettings, Language}
import control.Controller
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import model.blueprint.CourseBlueprint

class ImportCourseDetailsController extends Controller {

  override def language: Language = AppSettings.language

  @FXML var mainBox: VBox = _

  @FXML var descriptionTag: Label = _
  @FXML var descriptionContent: Label = _

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initializeContentLanguage()
  }

  private def initializeContentLanguage(): Unit = {
    descriptionTag.setText(language.getItemOrElse("import_courseDetails_descriptionTag", "Description"))
  }

  def description: String = descriptionContent.getText

  def description_=(s: String): Unit = descriptionContent.setText(s)

  def setFromCourseBlueprint(cb: CourseBlueprint): Unit = {
    description = cb.description
  }

  def clear(): Unit = {
    description = ""
  }
}
