package control

import factory.ViewFactory
import javafx.stage.{Modality, Window}
import util.Utils

case class StageSettings(title: String = "",
                         owner: Option[Window] = None,
                         modality: Modality = Modality.NONE)

trait SelfInitializedStageController extends StageController {

  selfInitialize()

  protected def selfInitialize(): Unit

  protected def initializeWith(settings: StageSettings, resourcePath: String): Unit =
    setStage(Utils.promptBoundWindow(
      settings.title,
      if (settings.owner.nonEmpty) settings.owner.get else null,
      settings.modality,
      new ViewFactory(resourcePath),
      this
    ))
}