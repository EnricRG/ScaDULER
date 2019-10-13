package control;

import app.AppSettings;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class BasicAlertController implements Initializable {

    protected boolean accepted = false;

    public Label message;
    public Button acceptButton;

    private final String text;

    public BasicAlertController(String text){
        this(text, false);
    }

    public BasicAlertController(String text, boolean defaultState){
        this.text = text;
        accepted = defaultState;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        message.setText(text);
        acceptButton.setText(AppSettings.language().getItem("acceptButton"));

        acceptButton.setOnAction(event -> {
            accepted = true;
            event.consume();
            ((Stage) acceptButton.getScene().getWindow()).close();
        });
    }

    public boolean accepted(){
        return accepted;
    }

    public boolean rejected(){
        return !accepted();
    }
}
