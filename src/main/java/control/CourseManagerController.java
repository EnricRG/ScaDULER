package control;

import akka.Main;
import app.AppSettings;
import app.MainApp;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import model.Course;
import scala.collection.JavaConverters;
import service.CourseDatabase;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseManagerController implements Initializable {

    private CourseDatabase courseDatabase = MainApp.database().courseDatabase();

    public TableView<Course> courseTable;

    public TableColumn<Course, String> courseTable_nameColumn;
    public TableColumn<Course, String> courseTable_descriptionColumn;
    public TableColumn<Course, String> courseTable_firstQuarterResources;
    public TableColumn<Course, String> courseTable_secondQuarterResources;

    public Button addCourseButton;
    public Button editCourseButton;
    public Button removeCourseButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        setupViews();
        bindActions();
    }

    private void initializeContentLanguage() {
        courseTable.setPlaceholder(new Label(AppSettings.language().getItem("courseTable_placeholder")));

        courseTable_nameColumn.setText(AppSettings.language().getItem("courseManager_nameColumnHeader"));
        courseTable_descriptionColumn.setText(AppSettings.language().getItem("courseManager_descriptionColumnHeader"));
        courseTable_firstQuarterResources.setText(AppSettings.language().getItem("courseManager_q1resourcesColumnHeader"));
        courseTable_secondQuarterResources.setText(AppSettings.language().getItem("courseManager_q2resourcesColumnHeader"));

        addCourseButton.setText(AppSettings.language().getItem("courseManager_addCourseButton"));
        editCourseButton.setText(AppSettings.language().getItem("courseManager_editCourseButton"));
        removeCourseButton.setText(AppSettings.language().getItem("courseManager_removeCourseButton"));
    }

    private void setupViews() {
        courseTable_nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().name()));
        courseTable_descriptionColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().description()));
        courseTable_firstQuarterResources.setCellValueFactory(cell -> {
            String s1 = AppSettings.language().getItem("courseTable_totalResourcesTypesWord") + cell.getValue().firstQuarter().resourceTypeCount();
            String s2 = AppSettings.language().getItem("courseTable_totalResourcesWord") + cell.getValue().firstQuarter().resourceAmount();
            return new SimpleStringProperty(s1 + "\n" + s2);
        });
        courseTable_secondQuarterResources.setCellValueFactory(cell -> {
            String s1 = AppSettings.language().getItem("courseTable_totalResourcesTypesWord") + cell.getValue().secondQuarter().resourceTypeCount();
            String s2 = AppSettings.language().getItem("courseTable_totalResourcesWord") + cell.getValue().secondQuarter().resourceAmount();
            return new SimpleStringProperty(s1 + "\n" + s2);
        });

        courseTable.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(courseDatabase.getElements())));
    }

    private void bindActions() {
        removeCourseButton.setOnAction(event -> removeSelectedCourse());
    }

    private void removeSelectedCourse() {
        Course c = courseTable.getSelectionModel().getSelectedItem();

        if(c != null){
            courseDatabase.removeElement(c);
            courseTable.getItems().remove(c);
        }
    }

}
