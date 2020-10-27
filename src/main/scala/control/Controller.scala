package control

import app.Language
import javafx.fxml.Initializable

trait Controller extends Initializable {
  protected def language: Language
  //protected def notifyLanguageChange(newLanguage: Language): Unit
}