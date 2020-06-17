package control.form

import java.net.URL
import java.util.ResourceBundle

import control.StageController
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.stage.Stage
import misc.Warning

object FormModes{
  sealed trait FormMode
  case object Create extends FormMode
  case object Edit extends FormMode

  //Java interoperability method
  def create(): FormMode = Create
}

/** Base class for entity form controllers
 *
 * Provides a common and mostly implemented interface for all StageControllers that manage an entity form window.
 * This way form controllers only have to override a bunch of methods and focus only on implementing its own logic.
 */
abstract class FormController[E] extends StageController {

  /** Warning notification field that all forms must have. */
  @FXML var warningTag: Label = _

  /** Holds form result */
  protected var formResult: Option[E] = None

  def this(stage: Stage) = {
    this()
    setStage(stage)
  }

  /** Should be used to initialize static text fields that depend on the application language. */
  protected def initializeContentLanguage(): Unit

  /** Initializes warning system. */
  protected def initializeWarningSystem(): Unit = {
    hideWarnings()
    warningTag.setText("")
  }

  /** Should be used to initialize fields that are not necessarily text fields. */
  protected def setupViews(): Unit

  /** Should be used to initialize interaction fields (i.e. buttons, lists). */
  protected def bindActions(): Unit

  /** Base FormController initialization. All classes overriding this method should call it first. */
  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    initializeContentLanguage()
    initializeWarningSystem()
    setupViews()
    bindActions()
  }

  /** Should be used to check that all input form fields are well formatted.
   *
   * @return a Warning if a field contains an error, null otherwise.
   */
  protected def checkWarnings: Option[Warning]

  /** Checks if any warning is generated when checking form fields, and shows it to the user if any.
   *
   * @return true when a warning is shown to the user, false otherwise.
   */
  protected def warnings: Boolean = warnings(checkWarnings)

  /** Checks if @p warning is a valid Warning, and shows it to the user if it is.
   *
   * @param warning A Warning.
   * @return true when a warning is shown to the user, false otherwise.
   */
  protected def warnings(warning: Option[Warning]): Boolean =
    if (warning.isEmpty) {
      hideWarnings()
      false
    }
    else {
      popUpWarning(warning.get)
      true
    }

  /** Hides the warning field. */
  private def hideWarnings(): Unit = {
    warningTag.setVisible(false)
  }

  /** shows the warning field. */
  private def showWarnings(): Unit = {
    warningTag.setVisible(true)
  }

  /** Shows a Warning to the user.
   *
   * @param warning the Warning to be shown
   */
  private def popUpWarning(warning: Warning): Unit = {
    warningTag.setText(warning.toString)
    showWarnings()
  }

  /** Shows controller's stage, blocks the thread until window is closed and then returns formResult. */
  def waitFormResult: Option[E] = {
    showAndWait()
    formResult
  }
}
