package factory;

import app.FXMLPaths;
import control.CourseFormController;
import control.ResourceManagerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;

public class ResourceManagerViewFactory {
    //if cfc is null, no controller will be bound
    public static Node load(CourseFormController cfc) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(new File(FXMLPaths.ManageResourcesPanel()).toURI().toURL());

        Node n = fxmlLoader.load();
        if(cfc != null) fxmlLoader.<ResourceManagerController>getController().bindCourseController(cfc);

        return n;
    }
}
