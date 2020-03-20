package control.select

import java.net.URL
import java.util
import java.util.ResourceBundle

import app.AppSettings
import control.StageController
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.{Button, CheckBox, TableColumn, TableView}
import javafx.scene.layout.HBox

import scala.collection.JavaConverters

@FXML
class EntitySelectorController[E](entities: Iterable[E]) extends StageController {

    class SimpleSelector extends HBox{
        private val checkBox = new CheckBox()

        this.setAlignment(Pos.CENTER)
        this.getChildren.add(checkBox)

        def isSelected: Boolean = checkBox.isSelected
    }

    @FXML var table: TableView[E] = _

    @FXML var selectColumn: TableColumn[E, SimpleSelector] = _

    @FXML var okButton: Button = _

    private var canceled: Boolean = true

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
        selectColumn.setCellValueFactory(_ => new SimpleObjectProperty(new SimpleSelector))
    }

    protected def bindActions(): Unit = {
        okButton.setOnAction(_ => { canceled = false; close() } )
    }

    private def selectedItems: Iterable[E] = {
        (0 until table.getItems.size)
            .filterNot(i => selectColumn.getCellData(i).isSelected)
            .map(i => table.getItems.get(i))
    }

    final def waitSelection: Iterable[E] = {
        showAndWait()
        if (!canceled) selectedItems
        else Nil
    }

    protected final def addColumn(column: TableColumn[E, _]): Unit = {
        table.getColumns.add(column)
    }

    protected final def addColumns(columns: util.Collection[TableColumn[E, _]]): Unit = {
        table.getColumns.addAll(columns)
    }

    protected final def addRow(entity: E): Unit = {
        table.getItems.add(entity)
    }

    protected final def removeRow(entity: E): Unit = {
        table.getItems.remove(entity)
    }

    protected final def fillTable(items: Iterable[E]): Unit = {
        table.getItems.addAll(JavaConverters.asJavaCollection(items))
    }

}
