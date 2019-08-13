package factory;

import control.form.CourseFormController;
import control.MainController;
import javafx.scene.Node;

import java.io.IOException;

public class CourseFormViewFactory extends ViewFactory<CourseFormController>{

    private final MainController mc;

    public CourseFormViewFactory(String resourcePath, MainController mc) {
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
