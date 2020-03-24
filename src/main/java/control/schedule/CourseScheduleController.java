package control.schedule;

import app.AppSettings;
import control.MainController;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import misc.Warning;
import model.Course;
import model.Quarter;
import model.Quarters;
import util.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseScheduleController implements Initializable {

    private final Course course;
    private final QuarterScheduleController firstQuarterController;
    private final QuarterScheduleController secondQuarterController;
    private final MainController mainController;

    public Label warningTag;

    public TabPane tabPane;
    public Tab firstQuarterTab;
    public Tab secondQuarterTab;

    public CourseScheduleController(MainController mainController, Course course){
        this.mainController = mainController;
        this.course = course;
        this.firstQuarterController = new QuarterScheduleController(mainController, this, course.getFirstQuarterData());
        this.secondQuarterController = new QuarterScheduleController(mainController, this, course.getSecondQuarterData());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        initializeQuarters();
        initializeTabView();
        initializeWarningSystem();
        bindActions();
        fillCourse();
    }

    private void initializeContentLanguage() {
        //TODO change this item labels to a more generic name
        firstQuarterTab.setText(AppSettings.language().getItem("manageCourseResources_firstQuarter"));
        secondQuarterTab.setText(AppSettings.language().getItem("manageCourseResources_secondQuarter"));
    }

    private void initializeQuarters() {

    }

    private void initializeTabView() {
        tabPane.widthProperty().addListener(Utils.bindTabWidthToTabPane(tabPane, 2));
    }

    private void bindActions() {
        firstQuarterTab.setOnSelectionChanged(event -> {
            if(firstQuarterTab.isSelected()){
                mainController.selectQuarterTabs(Quarters.firstQuarter());
            }
        });
        secondQuarterTab.setOnSelectionChanged(event -> {
            if(secondQuarterTab.isSelected()){
                mainController.selectQuarterTabs(Quarters.secondQuarter());
            }
        });
    }

    public void notifyEventDrop(QuarterScheduleController quarterScheduleController, ScheduleIntervalController intervalController, int hint) {
        mainController.processEventAssignment(this, quarterScheduleController, intervalController, hint);
    }

    public QuarterScheduleController getFirstQuarterController() { return firstQuarterController; }
    public QuarterScheduleController getSecondQuarterController() { return secondQuarterController; }

    public void setFirstQuarterContent(Node node) {
        firstQuarterTab.setContent(node);
    }

    public void setSecondQuarterContent(Node node) {
        secondQuarterTab.setContent(node);
    }

    private void initializeWarningSystem() {
        hideWarnings();
        warningTag.setText("");
    }

    private void fillCourse() {
        System.out.println("Fill course init");
        firstQuarterController.fillQuarter();
        secondQuarterController.fillQuarter();
    }

    public void hideWarnings(){
        warningTag.setVisible(false);
    }
    private void showWarnings(){
        warningTag.setVisible(true);
    }

    public void popUpWarning(Warning warning) {
        warningTag.setText(warning.toString());
        showWarnings();
    }

    public Course getCourse() {
        return course;
    }

    public QuarterScheduleController getVisibleQuarterController() {
        return tabPane.getSelectionModel().getSelectedItem() == firstQuarterTab ? firstQuarterController : secondQuarterController;
    }

    public QuarterScheduleController getQuarterController(Quarter quarter) {
        return quarter == Quarters.firstQuarter() ? firstQuarterController :
                quarter == Quarters.secondQuarter() ? secondQuarterController : null;
    }
}
