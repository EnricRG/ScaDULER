package control

import javafx.stage.Stage

/** Base class for child controllers.
 *
 * Child controllers are the ones that depend somehow on a MainController instance.
 */
abstract class ChildStageController(mainController: MainController)
    extends StageController {

    /** Creates an instance of ChildController given its dependencies.
     *
     * @param stage          controlled stage
     * @param mainController MainController instance of the application
     */
    def this(stage: Stage, mainController: MainController) {
        this(mainController)
        setStage(stage)
    }

    /** MainController reference getter.
     *
     * @return the MainController instance this child depends on.
     */
    final def getMainController: MainController = mainController
}
