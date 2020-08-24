package control.imprt

import java.net.URL
import java.util.ResourceBundle

import app.{AppSettings, Language}
import control.Controller
import javafx.fxml.FXML
import javafx.scene.layout.VBox
import model.blueprint.ResourceBlueprint

class ImportResourceDetailsController extends Controller {

  override def language: Language = AppSettings.language

  @FXML var mainBox: VBox = _

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initializeContentLanguage()
  }

  private def initializeContentLanguage(): Unit = {

  }

  def setFromResourceBlueprint(rb: ResourceBlueprint): Unit = {

  }

  def clear(): Unit ={

  }
}
