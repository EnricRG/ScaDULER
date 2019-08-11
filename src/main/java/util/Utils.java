package util;

import factory.ViewFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class Utils {

    public static Stage promptBoundWindow(String title, Window owner, Modality modality, ViewFactory viewFactory){
        Scene scene;

        try{
            scene = new Scene((Parent) viewFactory.load());
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
}
