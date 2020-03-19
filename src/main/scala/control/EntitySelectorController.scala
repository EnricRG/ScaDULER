package control
import java.net.URL
import java.util.ResourceBundle

import app.AppSettings
import javafx.fxml.FXML
import javafx.scene.control.{Button, CheckBox, TableColumn, TableView}

@FXML
class EntitySelectorController[E](entities: Iterable[E]) extends StageController {

    @FXML var table: TableView[E] = _

    @FXML var selectColumn: TableColumn[E, CheckBox] = _

    @FXML var okButton: Button = _

    override def initialize(location: URL, resources: ResourceBundle): Unit = {
        initializeContentLanguage()
        setupTable()
        bindActions()
    }

    protected def initializeContentLanguage(): Unit = {
        selectColumn.setText(AppSettings.language.getItemOrElse("selectColumn", "Select"))
        okButton.setText(AppSettings.language.getItemOrElse("okButton", "Ok"))
    }

    protected def setupTable(): Unit = {

    }

    protected def bindActions(): Unit = {
        okButton.setOnAction(_ => close())
    }
}
