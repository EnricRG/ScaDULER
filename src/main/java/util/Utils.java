package util;

import app.AppSettings;
import factory.ViewFactory;
import javafx.beans.InvalidationListener;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class Utils {

    public static Stage promptBoundWindow(String title, Window owner, Modality modality, ViewFactory<?> viewFactory){
        return promptBoundWindow(title, owner, modality, viewFactory, null);
    }

    public static <C extends Initializable> Stage promptBoundWindow(String title, Window owner, Modality modality, ViewFactory<C> viewFactory, C controller){
        Scene scene;

        try{
            scene = new Scene((Parent) (controller == null ? viewFactory.load() : viewFactory.load(controller)));
        } catch (IOException ioe){
            ioe.printStackTrace();
            scene = new Scene(new VBox());
        }

        Stage stage = new Stage();

        stage.initModality(modality);
        stage.initOwner(owner);
        stage.setTitle(title);
        stage.setScene(scene);

        return stage;
    }

    //pre: gridPane not null
    //post: if node is found, returns the reference to the node. Otherwise returns null
    public static Node getNodeByColumnAndRow(Integer column, Integer row, GridPane gridPane) {
        Node result = null;

        //This only works this way because SceneBuilder doesn't set all grid constraints.
        //You should edit FXML to set them manually and avoid this, but here I am, taking care of you.
        for(Node n : gridPane.getChildren()) {
            if (column.equals(GridPane.getColumnIndex(n)) && row.equals(GridPane.getRowIndex(n))) {
                result = n;
                break;
            }
        }

        return result;
    }

    //pre: tabPane not null
    public static InvalidationListener bindTabWidthToTabPane(TabPane tabPane) {
        return param -> {
            //bad solution (subtracting 20), but its working
            tabPane.setTabMinWidth((tabPane.getWidth() / 2) - 20);
            tabPane.setTabMaxWidth((tabPane.getWidth() / 2) - 20);
        };
    }

    public static String getFileExtension(String fileName){
        int extension_start = fileName.lastIndexOf('.');

        if(extension_start > 0) return fileName.substring(extension_start+1);
        else return null;
    }

    public static int computeInterval(int day, int dayInterval) {
        return day * AppSettings.timeSlotsPerDay() + dayInterval;
    }
}