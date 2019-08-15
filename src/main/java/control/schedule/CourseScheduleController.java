package control.schedule;

import app.AppSettings;
import control.MainController;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import model.Course;
import util.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseScheduleController implements Initializable {

    private final Course course;
    private final QuarterScheduleController firstQuarterController;
    private final QuarterScheduleController secondQuarterController;

    public TabPane tabPane;
    public Tab firstQuarterTab;
    public Tab secondQuarterTab;

    public CourseScheduleController(MainController mainController, Course course){
        this.course = course;
        this.firstQuarterController = new QuarterScheduleController(mainController, course.firstQuarter());
        this.secondQuarterController = new QuarterScheduleController(mainController, course.secondQuarter());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        initializeQuarters();
        initializeTabView();
        bindActions();
    }

    private void initializeContentLanguage() {
        //TODO change this item labels to a more generic name
        firstQuarterTab.setText(AppSettings.language().getItem("manageCourseResources_firstQuarter"));
        secondQuarterTab.setText(AppSettings.language().getItem("manageCourseResources_secondQuarter"));
    }

    private void initializeQuarters() {

    }

    private void initializeTabView() {
        tabPane.widthProperty().addListener(Utils.bindTabWidthToTabPane(tabPane));
    }

    private void bindActions() {
    }

    public QuarterScheduleController getFirstQuarterController() { return firstQuarterController; }
    public QuarterScheduleController getSecondQuarterController() { return secondQuarterController; }

    public void setFirstQuarterContent(Node node) {
        firstQuarterTab.setContent(node);
    }

    public void setSecondQuarterContent(Node node) {
        secondQuarterTab.setContent(node);
    }
}
