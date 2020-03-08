package control.manage;

import app.AppSettings;
import app.MainApp;
import control.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import model.Course;
import scala.collection.JavaConverters;
import service.CourseDatabase;

public class CourseManagerController extends EntityManagerController<Course> {

    private final CourseDatabase courseDatabase = MainApp.getDatabase().courseDatabase();

    private TableColumn<Course, String> nameColumn = new TableColumn<>();
    private TableColumn<Course, String> descriptionColumn = new TableColumn<>();

    public CourseManagerController(MainController mainController){
        super(mainController);
    }
    public CourseManagerController(Stage stage, MainController mainController){
        super(stage, mainController);
    }

    @Override
    protected void initializeContentLanguage() {
        table.setPlaceholder(new Label(AppSettings.language().getItem("courseTable_placeholder")));

        nameColumn.setText(AppSettings.language().getItem("courseManager_nameColumnHeader"));
        descriptionColumn.setText(AppSettings.language().getItem("courseManager_descriptionColumnHeader"));

        addButton.setText(AppSettings.language().getItem("courseManager_addCourseButton"));
        editButton.setText(AppSettings.language().getItem("courseManager_editCourseButton"));
        removeButton.setText(AppSettings.language().getItem("courseManager_removeCourseButton"));
    }

    @Override
    protected void setupTable() {
        addColumns();
        configureColumns();
        fillTable(JavaConverters.asJavaCollection(courseDatabase.getElements()));
    }

    private void addColumns(){
        addColumn(nameColumn);
        addColumn(descriptionColumn);
    }

    private void configureColumns(){
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        descriptionColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));
    }

    @Override
    protected void addButtonAction(ActionEvent e) {

    }

    @Override
    protected void editButtonAction(ActionEvent e) {

    }

    @Override
    protected void removeButtonAction(ActionEvent e) {
        Course course = table.getSelectionModel().getSelectedItem();

        if(course != null){
            courseDatabase.removeElement(course.getID());
            removeRow(course);
            getMainController().closeCourseTab(course);
        }
    }

}
