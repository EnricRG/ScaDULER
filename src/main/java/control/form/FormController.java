package control.form;

import control.ChildStageController;
import control.MainController;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import misc.Warning;

import java.net.URL;
import java.util.ResourceBundle;

/** Base class for entity form controllers
 *
 * Provides a common and mostly implemented interface for all ChildStageControllers that manage an entity form window.
 * This way form controllers only have to override a bunch of methods and focus only on implementing its own logic.
 */
public abstract class FormController extends ChildStageController implements Initializable {

    /** Warning notification field that all forms must have. */
    public Label warningTag;

    /** {@inheritDoc} */
    public FormController(MainController mainController){
        super(mainController);
    }

    /** {@inheritDoc} */
    public FormController(Stage stage, MainController mainController) {
        super(stage, mainController);
    }

    /** Should be used to initialize static text fields that depend on the application language. */
    protected abstract void initializeContentLanguage();

    /** Initializes warning system. */
    protected void initializeWarningSystem() {
        hideWarnings();
        warningTag.setText("");
    }

    /** Should be used to initialize fields that are not necessarily text fields. */
    protected abstract void setupViews();

    /** Should be used to initialize interaction fields (i.e. buttons, lists). */
    protected abstract void bindActions();

    /** {@inheritDoc} */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        initializeWarningSystem();
        setupViews();
        bindActions();
    }

    /** Should be used to check that all input form fields are well formatted.
     *
     * @return a Warning if a field contains an error, null otherwise.
     */
    protected abstract Warning checkWarnings();

    /** Checks if any warning is generated when checking form fields, and shows it to the user if any.
     *
     * @return true when a warning is shown to the user, false otherwise.
     */
    protected boolean warnings() {
        Warning warning = checkWarnings();
        if(warning == null){
            hideWarnings();
            return false;
        }
        else{
            popUpWarning(warning);
            return true;
        }
    }

    /** Checks if @p warning is a valid Warning, and shows it to the user if it is.
     *
     * @param warning A Warning.
     * @return true when a warning is shown to the user, false otherwise.
     */
    protected boolean warnings(Warning warning) {
        if(warning == null){
            hideWarnings();
            return false;
        }
        else{
            popUpWarning(warning);
            return true;
        }
    }

    /** Hides the warning field. */
    private void hideWarnings(){
        warningTag.setVisible(false);
    }

    /** shows the warning field. */
    private void showWarnings(){
        warningTag.setVisible(true);
    }

    /** Shows a Warning to the user.
     *
     * @param warning the Warning to be shown
     */
    private void popUpWarning(Warning warning) {
        warningTag.setText(warning.toString());
        showWarnings();
    }

}
