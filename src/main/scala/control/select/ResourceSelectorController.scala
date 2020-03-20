package control.select

import app.AppSettings
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.{Label, TableColumn}
import model.Resource

class ResourceSelectorController(resources: Iterable[Resource]) extends EntitySelectorController(resources){

    private def nameColumn: TableColumn[Resource, String] =
        new TableColumn(AppSettings.language.getItemOrElse("name", "Name"))

    private def capacityColumn: TableColumn[Resource, Int] =
        new TableColumn(AppSettings.language.getItemOrElse("capacity", "Capacity"))

    override protected def additionalTableSetup(): Unit = {
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
        nameColumn.setCellValueFactory(new PropertyValueFactory("name"))
        capacityColumn.setCellValueFactory(new PropertyValueFactory("capacity"))
    }
}
