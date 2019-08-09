package factory;

import app.FXMLPaths;
import control.MainController;
import control.SubjectFormController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;

public class SubjectFormViewFactory {
    public static Node load(MainController mc) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(new File(FXMLPaths.SubjectForm()).toURI().toURL());

        Node n = fxmlLoader.load();
        fxmlLoader.<SubjectFormController>getController().setMainController(mc);

        return n;
    }
}
