package control.select

import app.AppSettings
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.{Label, TableColumn}
import model.Resource

class ResourceSelectorController(resources: Iterable[Resource]) extends EntitySelectorController(resources){

    private def nameColumn = new TableColumn[Resource, String]
    private def capacityColumn = new TableColumn[Resource, Int]

    override protected def setupTable(): Unit = {
        super.setupTable()
        addColumns()
        setupColumns()

        table.setPlaceholder(
            new Label(AppSettings.language.getItemOrElse("resourceTable_placeholder", "No resources"))
        )
    }

    private def addColumns(): Unit = {
        addColumn(nameColumn)
        addColumn(capacityColumn)
    }

    private def setupColumns(): Unit = {
        nameColumn.setCellValueFactory(new PropertyValueFactory[Resource, String]("name"))
        capacityColumn.setCellValueFactory(new PropertyValueFactory[Resource, Int]("capacity"))
    }
}
