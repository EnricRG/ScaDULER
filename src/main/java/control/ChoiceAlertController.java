package control;

import app.AppSettings;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import scala.App;

import java.net.URL;
import java.util.ResourceBundle;

public class ChoiceAlertController implements Initializable {

    public static boolean accepted = false;

    public Label message;
    public Button rejectButton;
    public Button acceptButton;

    private final String text;

    public ChoiceAlertController(String text){
        this(text, false);
    }

    public ChoiceAlertController(String text, boolean defaultState){
        this.text = text;
        accepted = defaultState;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        message.setText(text);
        rejectButton.setText(AppSettings.language().getItem("denyButton"));
        acceptButton.setText(AppSettings.language().getItem("acceptButton"));

        rejectButton.setOnAction(event -> {
            accepted = false;
            event.consume();
            ((Stage) rejectButton.getScene().getWindow()).close();
        });

        acceptButton.setOnAction(event -> {
            accepted = true;
            event.consume();
            ((Stage) rejectButton.getScene().getWindow()).close();
        });
    }
}
