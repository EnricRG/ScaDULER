package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;


public class MainInterface extends Application{

    public static void main(String[] args) {
        Application.launch(MainInterface.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        URL url = new File("fxml/main3.fxml").toURI().toURL();
        System.out.println(url);
        Parent root = FXMLLoader.load(url);

        stage.setTitle("FXML Welcome");
        stage.setScene(new Scene(root));
        stage.show();
    }

}
