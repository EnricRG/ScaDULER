package control;

import javafx.stage.Stage;

/** Base class for child controllers.
 *
 * Child controllers are the ones that depend somehow on a MainController instance.
 */
public abstract class ChildStageController extends StageController {

    /** MainController instance */
    private final MainController mainController;

    /** Creates an instance of ChildController given its dependencies.
     *
     * @param mainController MainController instance of the application
     */
    public ChildStageController(MainController mainController){
        super();
        this.mainController = mainController;
    }

    /** Creates an instance of ChildController given its dependencies.
     *
     * @param stage controlled stage
     * @param mainController MainController instance of the application
     */
    public ChildStageController(Stage stage, MainController mainController){
        super(stage);
        this.mainController = mainController;
    }

    /** MainController reference getter.
     *
     * @return the MainController instance this child depends on.
     */
    public final MainController getMainController() {
        return mainController;
    }
}
