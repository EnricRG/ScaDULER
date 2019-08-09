package factory;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;

//invariant: controller is set only after calling load() method.
public class ViewFactory<C extends Initializable> {

    private String resourcePath;
    private C controller = null;

    public ViewFactory(String resourcePath){
        this.resourcePath = resourcePath;
    }

    public Node load() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(new File(resourcePath).toURI().toURL());

        Node n = fxmlLoader.load();
        controller = fxmlLoader.getController();

        return n;
    }

    public C getController() { return controller; }
}
