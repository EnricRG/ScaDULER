package factory

import java.io.{File, IOException}

import javafx.fxml.{FXMLLoader, Initializable}
import javafx.scene.Node

//invariant: controller will only be set after calling load method
class ViewFactory[C <: Initializable](resourcePath: String) {

    protected var controller: C = _

    @throws[IOException]
    def load: Node = {
        val fxmlLoader = new FXMLLoader(new File(resourcePath).toURI.toURL)

        val n = fxmlLoader.load
        controller = fxmlLoader.getController[C]

        n
    }

    @throws[IOException]
    def load(controller: C): Node = {
        val fxmlLoader = new FXMLLoader(new File(resourcePath).toURI.toURL)
        fxmlLoader.setController(controller)

        val n = fxmlLoader.load[Node]
        this.controller = controller

        n
    }

    def getController: C = controller
}

