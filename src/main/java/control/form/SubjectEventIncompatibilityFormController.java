package control.form;

import app.AppSettings;
import control.StageController;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import misc.EventTypeIncompatibilities;
import misc.EventTypeIncompatibility;
import scala.collection.JavaConverters;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.ResourceBundle;

public class SubjectEventIncompatibilityFormController extends StageController {

    public Label informationTag;
    public VBox checkBoxContainer;

    private Collection<EventTypeIncompatibility> incompatibilities;
    private HashMap<CheckBox, EventTypeIncompatibility> checkBoxIncompatibilityMap = new HashMap<>();

    SubjectEventIncompatibilityFormController(Collection<EventTypeIncompatibility> incompatibilities){
        this.incompatibilities = incompatibilities;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        setupCheckBoxes();
    }

    private void initializeContentLanguage() {
        informationTag.setText(AppSettings.language().getItem("subjectEventIncompatibilityForm_informationTag"));
    }

    private void setupCheckBoxes(){
        for(EventTypeIncompatibility eti: JavaConverters.asJavaCollection(EventTypeIncompatibilities.list())){
            CheckBox c = new CheckBox();
            bindCheckBoxToIncompatibility(c, eti);
            setupCheckBox(c, eti);
            checkBoxContainer.getChildren().add(c);
            //checkBoxContainer.setPrefWidth(Math.max(c.getWidth(), checkBoxContainer.getPrefWidth()));
        }
    }

    private void bindCheckBoxToIncompatibility(CheckBox c, EventTypeIncompatibility eti) {
        checkBoxIncompatibilityMap.put(c,eti);
    }

    private void setupCheckBox(CheckBox c, EventTypeIncompatibility eti) {
        c.setText(eti.toString());
        if(incompatibilities.contains(eti)) c.setSelected(true);
        setupCheckBoxBehavior(c);
    }

    private void setupCheckBoxBehavior(CheckBox c) {
        c.setOnAction(actionEvent -> {
            EventTypeIncompatibility incompatibility = checkBoxIncompatibilityMap.get(c); //It will always contain the element
            boolean isSelected = c.isSelected(); //The action event sets its value before calling this code

            if(isSelected) incompatibilities.add(incompatibility);
            else incompatibilities.remove(incompatibility);
        });
    }

}
