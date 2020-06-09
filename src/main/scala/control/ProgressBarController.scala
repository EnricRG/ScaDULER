package control
import java.net.URL
import java.util.ResourceBundle

import app.{AppSettings, Language}
import javafx.fxml.FXML
import javafx.scene.control.{Label, ProgressBar}

class ProgressBarController extends StageController {

  protected def language: Language = AppSettings.language

  @FXML var progressBar: ProgressBar = _
  @FXML var text: Label = _

  def initialize(location: URL, resources: ResourceBundle): Unit = { }

  def progress_=(progress: Double): Unit = {
    progressBar.setProgress(progress)
  }

  def text_=(text: String): Unit = {
    this.text.setText(text)
  }

  def setState(progress: Double, text: String): Unit = {
    this.progress_=(progress)
    this.text_=(text)
  }
}
