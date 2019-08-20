package app;

import control.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class MainInterface extends javafx.application.Application{

    private static MainController mainController;

    public static void main(String[] args) {
        Application.launch(MainInterface.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        URL url = new File(FXMLPaths.MainInterface()).toURI().toURL();
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        mainController = loader.getController();

        stage.setTitle(AppSettings.applicationTitle());
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void promptAlert(String title, String message){
        if(mainController != null){
            mainController.promptAlert(title, message);
        }
    }

    public static boolean promptChoice(String title, String message){
        if(mainController != null){
            return mainController.promptChoice(title, message);
        }
        else return false;
    }

}
