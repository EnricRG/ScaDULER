package control.schedule;

import app.AppSettings;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import util.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class DualWeekScheduleViewController<C1 extends WeekScheduleController, C2 extends WeekScheduleController> implements Initializable {

    protected C1 firstWeekController;
    protected C2 secondWeekController;

    public Pane mainPane;
    public TabPane tabPane;
    public Tab aWeekTab;
    public Tab bWeekTab;

    public DualWeekScheduleViewController(C1 controller1, C2 controller2){
        this.firstWeekController = controller1;
        this.secondWeekController = controller2;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        initializeTabConstraints();
    }

    private void initializeTabConstraints() {
        tabPane.widthProperty().addListener(Utils.bindTabWidthToTabPane(tabPane, 2));
    }

    private void initializeContentLanguage() {
        aWeekTab.setText(AppSettings.language().getItem("aWeek"));
        bWeekTab.setText(AppSettings.language().getItem("bWeek"));
    }

    public C1 getFirstWeekController(){ return this.firstWeekController; }
    public C2 getSecondWeekController(){ return this.secondWeekController; }

    public void setAWeekContent(Node node){ aWeekTab.setContent(node); }
    public void setBWeekContent(Node node){ bWeekTab.setContent(node); }
}
