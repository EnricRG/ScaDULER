package factory;

import app.FXMLPaths;
import control.CourseTableController;
import control.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;

@Deprecated
public class CoursePanelViewFactory {
    public static Node load(MainController mc) throws IOException{

        FXMLLoader fxmlLoader = new FXMLLoader(new File(FXMLPaths.CoursePanel()).toURI().toURL());

        Node n = fxmlLoader.load();
        fxmlLoader.<CourseTableController>getController().setMainController(mc);

        return n;
    }
}
