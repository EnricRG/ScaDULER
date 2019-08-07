package factory;

import app.FXMLPaths;
import control.CourseFormController;
import control.CourseResourceManagerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import model.CourseResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CourseResourceManagerViewFactory {
    public static Node load(CourseFormController cfc, List<CourseResource> fqr, List<CourseResource> sqr) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(new File(FXMLPaths.CourseResourceManagerForm()).toURI().toURL());

        Node n = fxmlLoader.load();
        CourseResourceManagerController controller = fxmlLoader.getController();
        controller.setCourseController(cfc);
        controller.linkResources(fqr,sqr); //pass both quarters' resource lists

        return n;
    }
}
