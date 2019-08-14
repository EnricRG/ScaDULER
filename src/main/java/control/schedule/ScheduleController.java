package control.schedule;

import app.AppSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import util.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class ScheduleController implements Initializable {

    protected static final String HEADER_CSS_STYLE = "-fx-border-width: 0 0 2 0; -fx-border-color: gray;";
    protected static final String INNER_CELL_CSS_BORDER = "-fx-border-width: 1 0 0 0;";
    protected static final String O_CLOCK_CSS_BORDER_STYLE = INNER_CELL_CSS_BORDER + "-fx-border-color: gray;";
    protected static final String HALF_HOUR_CSS_BORDER_STYLE = INNER_CELL_CSS_BORDER + "-fx-border-color: lightgray; -fx-border-style: dotted;";

    protected static final String DRAG_OVER_CELL_ADDITIONAL_CSS_STYLE = "-fx-background-color: lightgrey;";

    public StackPane stackPane;
    public GridPane gridPane;

    public Label mondayTag;
    public Label tuesdayTag;
    public Label wednesdayTag;
    public Label thursdayTag;
    public Label fridayTag;

    private ObservableList<Node> headers = FXCollections.observableArrayList();
    private ObservableList<Node> innerCells = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeGrid();
        initializeContentLanguage();
    }

    private void initializeGrid() {
        int numberOfRows = gridPane.getRowCount();
        int numberOfColumns = gridPane.getColumnCount();

        //first row
        for(int column = 0; column < numberOfColumns; column++){
            Node node = Utils.getNodeByColumnAndRow(column,0,gridPane);

            if(node != null) node.setStyle(HEADER_CSS_STYLE);
            else {
                Node newNode = new HBox();
                newNode.setStyle(HEADER_CSS_STYLE);
                gridPane.add(newNode, column, 0);
            }

            headers.add(node);
        }

        //skipping first row that contains the headers
        //I nested the loops this way to get all intervals sorted by its global interval number
        for(int column = 0; column < numberOfColumns; column++){
            for(int row = 1; row < numberOfRows; row++){
                Node node = Utils.getNodeByColumnAndRow(column,row,gridPane);

                if(node == null){ //fill grid cell
                    node = new HBox();
                    gridPane.add(node, column, row);
                }

                if(row % 2 != 0){ //o'clock hours
                    node.setStyle(O_CLOCK_CSS_BORDER_STYLE);
                }
                else{ //half hours
                    node.setStyle(HALF_HOUR_CSS_BORDER_STYLE);
                }

                if(column > 0){ //avoid changing hour column behavior
                    innerCells.add(node);
                    setupCellBehavior(node);
                }
            }
        }
    }

    protected void setupCellBehavior(Node node) {
        node.setOnMouseEntered(event ->
                node.setStyle(node.getStyle() + DRAG_OVER_CELL_ADDITIONAL_CSS_STYLE)
        );
        node.setOnMouseExited(event ->
                node.setStyle(node.getStyle().replace(DRAG_OVER_CELL_ADDITIONAL_CSS_STYLE, ""))
        );
        node.setOnMouseDragEntered(node.getOnMouseEntered());
        node.setOnMouseDragExited(node.getOnMouseExited());
    }

    private void initializeContentLanguage() {
        mondayTag.setText(AppSettings.language().getItem("monday"));
        tuesdayTag.setText(AppSettings.language().getItem("tuesday"));
        wednesdayTag.setText(AppSettings.language().getItem("wednesday"));
        thursdayTag.setText(AppSettings.language().getItem("thursday"));
        fridayTag.setText(AppSettings.language().getItem("friday"));
    }

    public ObservableList<Node> getInnerCells() { return innerCells; }

    //pre: gridPane != null && cell != null && gridPane.getChildren().contains(cell)
    public static Integer computeInterval(GridPane gridPane, Node cell){
        Integer day = GridPane.getColumnIndex(cell) - 1; //subtract first column (hours)
        Integer dayInterval = GridPane.getRowIndex(cell) - 1; //subtract first row (headers)

        return day * AppSettings.timeSlotsPerDay() + dayInterval;
    }
}
