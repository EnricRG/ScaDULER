package factory;

import app.FXMLPaths;
import control.CourseFormController;
import control.CourseResourcesForm;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;

public class CourseResourceManagerViewFactory {
    public static Node load(CourseFormController cfc) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(new File(FXMLPaths.CourseResourceManagerForm()).toURI().toURL());

        Node n = fxmlLoader.load();
        fxmlLoader.<CourseResourcesForm>getController().setCourseController(cfc);

        return n;
    }
}
