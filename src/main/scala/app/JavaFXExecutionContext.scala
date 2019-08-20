package app

import javafx.application.Platform
import java.util.concurrent.Executor

import scala.concurrent.ExecutionContext

//source: https://stackoverflow.com/questions/20828726/javafx2-or-scalafx-akka
object JavaFXExecutionContext {
    implicit val javaFxExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(new Executor {
        def execute(command: Runnable): Unit = Platform.runLater(command)
    })
}
