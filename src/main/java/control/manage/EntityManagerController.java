package control.manage;

import control.ChildStageController;
import control.MainController;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public abstract class EntityManagerController<E> extends ChildStageController {

    public TableView<E> table;

    public Button addButton;
    public Button editButton;
    public Button removeButton;

    public EntityManagerController(MainController mainController) {
        super(mainController);
    }

    public EntityManagerController(Stage stage, MainController mainController){
        super(stage, mainController);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContentLanguage();
        setupTable();
        bindActions();
    }

    /** Should be used to initialize static text fields that depend on the application language. */
    protected abstract void initializeContentLanguage();

    /** Should be used to initialize fields that are not necessarily text fields. */
    protected abstract void setupTable();

    /** Should be used to initialize interaction fields (i.e. buttons, lists). */
    protected void bindActions(){
        addButton.setOnAction(this::addButtonAction);
        editButton.setOnAction(this::editButtonAction);
        removeButton.setOnAction(this::removeButtonAction);
    }

    protected abstract void addButtonAction(ActionEvent event);
    protected abstract void editButtonAction(ActionEvent event);
    protected abstract void removeButtonAction(ActionEvent event);

    protected final void addColumn(TableColumn<E,?> column){
        table.getColumns().add(column);
    }

    protected final void addColumns(Collection<TableColumn<E,?>> columns){
        table.getColumns().addAll(columns);
    }

    protected final void addRow(E entity){
        table.getItems().add(entity);
    }

    protected final void removeRow(E entity){
        table.getItems().remove(entity);
    }

    protected final void fillTable(Collection<E> items){
        table.getItems().addAll(items);
    }
}
