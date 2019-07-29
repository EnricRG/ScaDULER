package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;


public class MainInterface extends javafx.application.Application{

    public static void main(String[] args) {
        Application.launch(MainInterface.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        URL url = new File(FXMLPaths.MainInterface()).toURI().toURL();
        Parent root = FXMLLoader.load(url);

        stage.setTitle(AppSettings.applicationTitle());
        stage.setScene(new Scene(root));
        stage.show();
    }

}
