package factory;

import control.form.CourseFormController;
import control.manage.CourseResourceManagerController;
import javafx.scene.Node;
import model.CourseResource;

import java.io.IOException;
import java.util.List;

public class CourseResourceManagerViewFactory extends ViewFactory<CourseResourceManagerController>{

    private final CourseFormController cfc;
    private final List<CourseResource> fqr; /** First Quarter Resources */
    private final List<CourseResource> sqr; /** Second Quarter Resources */

    public CourseResourceManagerViewFactory(String resourcePath, CourseFormController cfc, List<CourseResource> fqr, List<CourseResource> sqr) {
        super(resourcePath);
        this.cfc = cfc;
        this.fqr = fqr;
        this.sqr = sqr;
    }

    @Override
    public Node load() throws IOException {
        Node n = super.load(); //controller is only set after successful loading
        getController().setCourseController(cfc);
        getController().linkResources(fqr,sqr);
        return n;
    }
}
