package app

import control.MainController
import factory.ViewFactory
import javafx.stage.Stage
import solver.EventAssignment
import util.Utils
import java.util

class MainInterface extends javafx.application.Application {

    @throws[Exception]
    override def start(stage: Stage): Unit = {

        System.out.println("Loading main controller...")

        MainInterface.mainController = new MainController
        MainInterface.mainController.setStage(stage)
        stage.setScene(Utils.loadScene(
            new ViewFactory[MainController](FXMLPaths.MainInterface), MainInterface.mainController)
        )

        System.out.println("Main Controller Loaded")

        stage.setTitle(AppSettings.applicationTitle)
        stage.show()

        System.out.println("Loaded")
    }

}

object MainInterface {

    private var mainController: MainController = _

    def promptAlert(title: String, message: String): Unit = {
        if (mainController != null) mainController.promptAlert(title, message)
    }

    def promptChoice(title: String, message: String): Boolean = {
        if (mainController != null) mainController.promptChoice(title, message)
        else false
    }

    //TODO replace util.Collection
    def processAssignments(assignments: util.Collection[EventAssignment]): Unit = {
        mainController.processEventAssignments(assignments)
    }
}

