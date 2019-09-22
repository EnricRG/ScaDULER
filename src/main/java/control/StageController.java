package control;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

/** Base class for controllers that control a Stage. */
public abstract class StageController implements Initializable{

    /** Controlled Stage. */
    private Stage stage = null;

    /** Creates an instance of StageController without initializing the stage. */
    public StageController(){}

    /** Creates an instance of StageController given an initialized stage.
     *
     * @param stage non-null stage that will be controlled.
     */
    public StageController(Stage stage) {
        this.stage = stage;
    }

    /** Sets the stage for this controller if possible. This method can only be called if the stage was not set yet.
     *
     * @param stage the stage.
     */
    public void setStage(Stage stage){
        if(this.stage != null){
            throw new IllegalStateException("stage was already set.");
        }
        else{
            this.stage = stage;
        }
    }

    /** Shows the stage. */
    public final void show(){
        stage.show();
    }

    /** Shows the stage and stops calling thread until it's closed. */
    public final void showAndWait(){
        stage.showAndWait();
    }

    /** Hides the stage. */
    public final void close(){
        stage.close();
    }
}