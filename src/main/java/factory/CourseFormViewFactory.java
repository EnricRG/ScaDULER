package factory;

import app.FXMLPaths;
import control.CourseFormController;
import control.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;

public class CourseFormViewFactory {
    public static Node load(MainController mc) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(new File(FXMLPaths.CourseForm()).toURI().toURL());

        Node n = fxmlLoader.load();
        fxmlLoader.<CourseFormController>getController().setMainController(mc);

        return n;
    }
}
