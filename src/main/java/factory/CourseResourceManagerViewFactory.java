package factory;

import control.form.CourseFormController;
import control.manage.CourseResourceManagerController;
import javafx.scene.Node;
import model.CourseResource;

import java.io.IOException;
import java.util.List;

public class CourseResourceManagerViewFactory extends ViewFactory<CourseResourceManagerController>{

    private final CourseFormController cfc;
    private final List<CourseResource> cr; /** Course Resources */

    public CourseResourceManagerViewFactory(String resourcePath, CourseFormController cfc, List<CourseResource> cr) {
        super(resourcePath);
        this.cfc = cfc;
        this.cr = cr;
    }

    @Override
    public Node load() throws IOException {
        Node n = super.load(); //controller is only set after successful loading
        getController().setCourseController(cfc);
        getController().linkResources(cr);
        return n;
    }
}
