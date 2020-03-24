package util

import java.io.IOException

import app.AppSettings
import factory.ViewFactory
import javafx.beans.{InvalidationListener, Observable}
import javafx.fxml.Initializable
import javafx.scene.{Node, Parent, Scene}
import javafx.scene.control.TabPane
import javafx.scene.layout.{GridPane, VBox}
import javafx.stage.{Modality, Stage, Window}

import scala.collection.JavaConverters

object Utils {

    def loadScene[C <: Initializable](viewFactory: ViewFactory[C]): Scene =
        loadScene(viewFactory, None)

    def loadScene[C <: Initializable](viewFactory: ViewFactory[C], controller: C): Scene =
        loadScene(viewFactory, Some(controller))

    def loadScene[C <: Initializable](viewFactory: ViewFactory[C], controller: Option[C]): Scene = {
        try {
            new Scene(
                (if (controller.isEmpty) viewFactory.load else viewFactory.load(controller.get)).asInstanceOf[Parent]
            )
        }
        catch {
            case ioe: IOException =>
                ioe.printStackTrace()
                new Scene(new VBox)
        }
    }

    def promptBoundWindow[C <: Initializable](title: String, owner: Window, modality: Modality, viewFactory: ViewFactory[C]): Stage =
        promptBoundWindow(title, owner, modality, viewFactory, None)

    //pre controller != null
    def promptBoundWindow[C <: Initializable](title: String, owner: Window, modality: Modality, viewFactory: ViewFactory[C], controller: C): Stage =
        promptBoundWindow(title, owner, modality, viewFactory, Some(controller))

    def promptBoundWindow[C <: Initializable](title: String, owner: Window, modality: Modality, viewFactory: ViewFactory[C], controller: Option[C]): Stage = {
        val scene = loadScene(viewFactory, controller)

        val stage = new Stage

        stage.initModality(modality)
        stage.initOwner(owner)
        stage.setTitle(title)
        stage.setScene(scene)

        stage
    }

    //pre: gridPane not null
    //post: if node is found, returns the reference to the node. Otherwise returns null
    @deprecated
    def getNodeByColumnAndRow(column: Integer, row: Integer, gridPane: GridPane): Node = {

        //This only works this way because SceneBuilder doesn't set all grid constraints.
        //You should edit FXML to set them manually and avoid this, but here I am, taking care of you.
        for (n <- JavaConverters.collectionAsScalaIterable(gridPane.getChildren)) {
            if (column == GridPane.getColumnIndex(n) && row == GridPane.getRowIndex(n)) {
                return n
            }
        }

        null
    }

    //pre: tabPane not null
    def bindTabWidthToTabPane(tabPane: TabPane, tabs: Int): InvalidationListener = (_: Observable) => {
        //bad solution (subtracting 20), but its working
        if (tabs > 0) {
            tabPane.setTabMinWidth((tabPane.getWidth / tabs) - 20)
            tabPane.setTabMaxWidth((tabPane.getWidth / tabs) - 20)
        }
    }

    def getFileExtension(fileName: String): String = {
        val extension_start = fileName.lastIndexOf('.')

        if (extension_start > 0) fileName.substring(extension_start + 1)
        else null
    }

    def computeInterval(day: Int, dayInterval: Int): Int = day * AppSettings.timeSlotsPerDay + dayInterval
}
