package control.manage;

import javafx.fxml.Initializable;
import model.NewEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EventIncompatibilityManagerController implements Initializable {

    private final ArrayList<NewEvent> incompatibilities;

    public EventIncompatibilityManagerController(ArrayList<NewEvent> incompatibilities){
        this.incompatibilities = incompatibilities;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO
    }
}
