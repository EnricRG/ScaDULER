package factory;

import control.MainController;
import control.SubjectFormController;
import javafx.scene.Node;

import java.io.IOException;

public class SubjectFormViewFactory extends ViewFactory<SubjectFormController>{

    private final MainController mc;

    public SubjectFormViewFactory(String resourcePath, MainController mc) {
        super(resourcePath);
        this.mc = mc;
    }

    @Override
    public Node load() throws IOException {
        Node n = super.load(); //controller is only set after successful loading
        getController().setMainController(mc);
        return n;
    }
}
