package app;

import control.BinaryChoiceAlertController;
import control.MainController;
import factory.ViewFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import solver.EventAssignment;
import util.Utils;

import java.io.File;
import java.net.URL;
import java.util.Collection;

public class MainInterface extends javafx.application.Application{

    private static MainController mainController;

    //TODO remove this method, unused
    public static void main(String[] args) {
        Application.launch(MainInterface.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Loading main controller...");

        mainController = new MainController();
        mainController.setStage(stage);

        stage.setScene(Utils.loadScene(new ViewFactory<>(FXMLPaths.MainInterface()), mainController));

        System.out.println("Main Controller Loaded");

        stage.setTitle(AppSettings.applicationTitle());
        stage.show();

        System.out.println("Loaded");
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

    public static void processAssignments(Collection<EventAssignment> assignments){
        mainController.processEventAssignments(assignments);
    }

}
