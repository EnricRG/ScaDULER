package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class MainInterface extends Application{

    public static void main(String[] args) {
        Application.launch(MainInterface.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        try {
            URL url = new File("fxml/main_border_pane.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);

            stage.setTitle(AppSettings.applicationTitle());
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
