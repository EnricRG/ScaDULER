package control.manage;

import app.AppSettings;
import app.MainApp;
import control.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Course;
import scala.collection.JavaConverters;
import service.CourseDatabase;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseManagerController implements Initializable {

    private final CourseDatabase courseDatabase = MainApp.getDatabase().courseDatabase();

    private final MainController mainController;

    public TableView<Course> courseTable;

    public TableColumn<Course, String> courseTable_nameColumn;
    public TableColumn<Course, String> courseTable_descriptionColumn;

    public Button addCourseButton;
    public Button editCourseButton;
    public Button removeCourseButton;

    public CourseManagerController(MainController mainController){
        this.mainController = mainController;
    }

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

        addCourseButton.setText(AppSettings.language().getItem("courseManager_addCourseButton"));
        editCourseButton.setText(AppSettings.language().getItem("courseManager_editCourseButton"));
        removeCourseButton.setText(AppSettings.language().getItem("courseManager_removeCourseButton"));
    }

    private void setupViews() {
        courseTable_nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().name()));
        courseTable_descriptionColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().description()));

        courseTable.setItems(FXCollections.observableArrayList(JavaConverters.asJavaCollection(courseDatabase.getElements())));
    }

    private void bindActions() {
        removeCourseButton.setOnAction(event -> removeSelectedCourse());
    }

    private void removeSelectedCourse() {
        Course c = courseTable.getSelectionModel().getSelectedItem();

        if(c != null){
            courseDatabase.removeElement(c.getID());
            courseTable.getItems().remove(c);
            mainController.closeCourseTab(c);
        }
    }

}
