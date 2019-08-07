package factory;

import app.FXMLPaths;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;

public class ResourceManagerViewFactory {
    public static Node load() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(new File(FXMLPaths.ManageResourcesPanel()).toURI().toURL());

        Node n = fxmlLoader.load();

        return n;
    }
}
