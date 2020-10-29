package control.misc

import java.net.URL
import java.util.ResourceBundle

import app.FXMLPaths
import control.{SelfInitializedStageController, StageSettings}
import javafx.fxml.FXML
import javafx.scene.control.{Button, Label}

sealed trait RemoveMode
object HardRemove extends RemoveMode
object SoftRemove extends RemoveMode

class RemoveModePrompt(text: String,
  softRemoveButtonText: String,
  hardRemoveButtonText: String,
  settings: StageSettings
) extends SelfInitializedStageController {

  @FXML var textLabel: Label = _

  @FXML var softRemoveButton: Button = _
  @FXML var hardRemoveButton: Button = _

  private var selectedMode: Option[RemoveMode] = None

  override def selfInitialize(): Unit =
    initializeWith(settings, FXMLPaths.RemoveModePrompt)

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    textLabel.setText(text)
    softRemoveButton.setText(softRemoveButtonText)
    hardRemoveButton.setText(hardRemoveButtonText)

    bindActions()
  }

  protected def bindActions(): Unit = {
    softRemoveButton.setOnAction(_ => {
      selectedMode = Some(SoftRemove)
      close()
    })

    hardRemoveButton.setOnAction(_ => {
      selectedMode = Some(HardRemove)
      close()
    })
  }

  def waitChoice(): Option[RemoveMode] = {
    showAndWait()
    selectedMode
  }
}