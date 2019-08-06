package control;

import app.AppSettings;
import factory.ResourceManagerViewFactory;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CourseResourcesForm implements Initializable {

    private CourseFormController courseFormController;

    public Button manageGlobalResources;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        manageGlobalResources.setOnAction(event -> {
            courseFormController.getMainController().promptResourceManager(manageGlobalResources.getScene().getWindow());
        });
    }

    public void setCourseController(CourseFormController cfc){
        courseFormController = cfc;
    }

}
