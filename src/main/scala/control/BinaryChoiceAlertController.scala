package control

import java.net.URL
import java.util.ResourceBundle

import app.AppSettings
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.stage.Stage

class BinaryChoiceAlertController(text: String, defaultState: Boolean = false)
    extends BasicAlertController(text, defaultState) {

    @FXML var rejectButton: Button = _

    //For Java interoperability transparency
    def this(text: String) = this(text, false)

    override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
        super.initialize(url, resourceBundle)

        rejectButton.setText(AppSettings.language.getItemOrElse("denyButton", "Reject"))
        //acceptButton.setText(AppSettings.language().getItemOrElse("acceptButton", "Accept"));

        rejectButton.setOnAction(event => {
            _accepted = false
            event.consume()
            rejectButton.getScene.getWindow.asInstanceOf[Stage].close()
        })
    }
}