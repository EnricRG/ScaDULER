package factory;

import app.FXMLPaths;
import control.CourseController;
import control.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import model.Course;

import java.io.File;
import java.io.IOException;

public class CourseViewFactory {
    public static Node newCourseViewFromFXML(MainController mc) throws IOException{

        FXMLLoader fxmlLoader = new FXMLLoader(new File(FXMLPaths.Course()).toURI().toURL());

        Node n = fxmlLoader.load();
        fxmlLoader.<CourseController>getController().setMainController(mc);

        return n;
    }
}
