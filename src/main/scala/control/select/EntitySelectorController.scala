package control.select

import java.net.URL
import java.util
import java.util.ResourceBundle

import app.AppSettings
import control.StageController
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control._
import javafx.scene.layout.HBox

import scala.collection.{JavaConverters, mutable}

@FXML
class EntitySelectorController[E](entities: Iterable[E]) extends StageController {

    class SimpleSelector(initValue: Boolean = false) extends HBox{
        private val checkBox = new CheckBox()

        checkBox.setSelected(initValue)
        this.setAlignment(Pos.CENTER)
        this.getChildren.add(checkBox)

        def isSelected: Boolean = checkBox.isSelected
        def setOnAction(value: EventHandler[ActionEvent]): Unit = checkBox.setOnAction(value)
    }

    @FXML var table: TableView[E] = _

    @FXML var selectColumn: TableColumn[E, Any] = _

    @FXML var okButton: Button = _

    private var canceled: Boolean = true
    private val selection: mutable.Map[E, Boolean] = new mutable.HashMap

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
        selectColumn.setCellFactory(_ => new TableCell[E, Any]{
            override def updateItem(item: Any, empty: Boolean): Unit = {
                super.updateItem(item, empty)
                if (!empty) {
                    val selector = new SimpleSelector(true)

                    selection.update(table.getItems.get(getIndex), selector.isSelected)
                    selector.setOnAction(_ =>
                        selection.update(table.getItems.get(getIndex), selector.isSelected)
                    )

                    this.setGraphic(selector)
                }
                else {
                    setGraphic(null)
                    setText(null)
                }
            }
        })

        additionalTableSetup()
        fillTable(entities)
    }

    protected def additionalTableSetup(): Unit = { }

    protected def bindActions(): Unit = {
        okButton.setOnAction(_ => { canceled = false; close() } )
        additionalActionBinding()
    }

    protected def additionalActionBinding(): Unit = { }

    private def selectedItems: Iterable[E] = {
        JavaConverters.collectionAsScalaIterable(
            table.getItems.filtered(selection.getOrElse(_, false))
        )
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
