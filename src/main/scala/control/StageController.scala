package control

import javafx.fxml.Initializable
import javafx.stage.Stage

/** Base class for controllers that control a Stage. */
abstract class StageController extends Initializable {

    /** Controlled Stage. */
    protected var stage: Stage = _

    /** Creates an instance of StageController given an initialized stage.
     *
     * @param stage non-null stage that will be controlled.
     */
    def this(stage: Stage) = {
        this()
        setStage(stage)
    }

    /** Sets the stage for this controller if possible. This method can only be called if the stage was not set yet.
     *
     * @param stage the stage.
     */
    def setStage(stage: Stage): Unit = {
        if (this.stage != null) throw new IllegalStateException("stage was already set.")
        else this.stage = stage
    }

    /** Shows the stage. */
    final def show(): Unit = stage.show()

    /** Shows the stage and stops calling thread until it's closed. */
    final def showAndWait(): Unit = stage.showAndWait()

    /** Hides the stage. */
    final def close(): Unit = stage.close()
}
