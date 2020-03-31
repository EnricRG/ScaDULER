package control

import java.net.URL
import java.util.ResourceBundle

import app.AppSettings
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, Label}
import javafx.stage.Stage

class BasicAlertController(text: String, defaultState: Boolean = false) extends Initializable {

    protected var _accepted: Boolean = defaultState

    @FXML var message: Label = _
    @FXML var acceptButton: Button = _

    //For Java interoperability transparency
    def this(text: String) = this(text, false)

    override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
        message.setText(text)
        acceptButton.setText(AppSettings.language.getItemOrElse("acceptButton", "Accept"))

        acceptButton.setOnAction(event => {
            _accepted = true
            event.consume()
            acceptButton.getScene.getWindow.asInstanceOf[Stage].close()
        })
    }

    def accepted: Boolean = _accepted

    def rejected: Boolean = !accepted
}
