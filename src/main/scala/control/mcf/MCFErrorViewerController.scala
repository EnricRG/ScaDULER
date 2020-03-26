package control.mcf

import java.net.URL
import java.util.ResourceBundle

import app.AppSettings
import control.StageController
import file.imprt.{ImportError, MCFImportError}
import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.{Button, Label, TableColumn, TableView}

import scala.collection.JavaConverters

class MCFErrorViewerController(errors: Iterable[MCFImportError]) extends StageController{

    @FXML var errorsFound: Label = _

    @FXML var table: TableView[MCFImportError] = _
    @FXML var lineColumn: TableColumn[MCFImportError, Int] = _
    @FXML var fieldNumberColumn: TableColumn[MCFImportError, Int] = _
    @FXML var fieldValueColumn: TableColumn[MCFImportError, String] = _
    @FXML var errorDescriptionColumn: TableColumn[MCFImportError, String] = _

    @FXML var advice: Label = _
    @FXML var cancelButton: Button = _

    //TODO remove this workaround
    def this(errors: List[ImportError]) = this(errors.asInstanceOf[Iterable[MCFImportError]])

    override def initialize(location: URL, resources: ResourceBundle): Unit = {
        initializeContentLanguage()
        setupTable()
        bindActions()
    }

    def initializeContentLanguage(): Unit = {
        errorsFound.setText(
            errors.size + " " +
            AppSettings.language.getItemOrElse("mcf_errorsFound", "Errors have been found")
        )

        table.setPlaceholder(
            new Label(AppSettings.language.getItemOrElse("mcf_errorTable_placeholder", "No errors"))
        )

        lineColumn.setText(
            AppSettings.language.getItemOrElse("mcf_lineColumn", "Line")
        )
        fieldNumberColumn.setText(
            AppSettings.language.getItemOrElse("mcf_fieldNumberColumn", "Field Nº")
        )
        fieldValueColumn.setText(
            AppSettings.language.getItemOrElse("mcf_fieldValueColumn", "Field Value")
        )
        errorDescriptionColumn.setText(
            AppSettings.language.getItemOrElse("mcf_descriptionColumn", "Error Description")
        )

        advice.setText(
            AppSettings.language
                .getItemOrElse("mcf_errorsFound_advice", "Fix the errors and try importing again.")
        )

        cancelButton.setText(AppSettings.language.getItemOrElse("mcf_cancelImport", "Cancel Import"))
    }

    def setupTable(): Unit = {

        lineColumn.setCellValueFactory(cell => {
            val error = cell.getValue
            val cellValue = if (error == null) new SimpleIntegerProperty else new SimpleIntegerProperty(error.row)
            cellValue.asInstanceOf[ObservableValue[Int]] //type inference not working here
        })

        fieldNumberColumn.setCellValueFactory(cell => {
            val error = cell.getValue
            val cellValue = if (error == null) new SimpleIntegerProperty else new SimpleIntegerProperty(error.field)
            cellValue.asInstanceOf[ObservableValue[Int]] //type inference not working here
        })

        fieldValueColumn.setCellValueFactory(cell => {
            val error = cell.getValue
            val cellValue = if (error == null) new SimpleStringProperty else new SimpleStringProperty(error.value)
            cellValue.asInstanceOf[ObservableValue[String]] //type inference not working here
        })

        errorDescriptionColumn.setCellValueFactory(cell => {
            val error = cell.getValue
            val cellValue =
                if (error == null) new SimpleStringProperty
                else new SimpleStringProperty(error.specificMessage)

            cellValue.asInstanceOf[ObservableValue[String]] //type inference not working here
        })

        table.getItems.addAll(JavaConverters.asJavaCollection(errors))
    }

    def bindActions(): Unit = {
        cancelButton.setOnAction(_ => close())
    }
}
