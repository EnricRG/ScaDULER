package factory;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.io.IOException;

//invariant: controller is set only after calling load() method.
public class ViewFactory<C extends Initializable> {

    private String resourcePath;
    protected C controller = null;

    public ViewFactory(String resourcePath){
        this.resourcePath = resourcePath;
    }

    public Node load() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(resourcePath));

        Node n = fxmlLoader.load();
        controller = fxmlLoader.getController();

        return n;
    }

    public Node load(C controller) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(resourcePath));
        fxmlLoader.setController(controller);

        Node n = fxmlLoader.load();
        this.controller = controller;

        return n;
    }

    public C getController() { return controller; }
}
