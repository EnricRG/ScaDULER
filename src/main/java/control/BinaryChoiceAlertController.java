package control;

import app.AppSettings;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class BinaryChoiceAlertController extends BasicAlertController {

    public Button rejectButton;

    public BinaryChoiceAlertController(String text){
        super(text);
    }

    public BinaryChoiceAlertController(String text, boolean defaultState){
        super(text, defaultState);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        rejectButton.setText(AppSettings.language().getItem("denyButton"));
        //acceptButton.setText(AppSettings.language().getItem("acceptButton"));

        rejectButton.setOnAction(event -> {
            accepted = false;
            event.consume();
            ((Stage) rejectButton.getScene().getWindow()).close();
        });
    }
}
