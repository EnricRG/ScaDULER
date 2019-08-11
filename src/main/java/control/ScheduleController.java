package control;

import app.AppSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ScheduleController implements Initializable {

    private static final String HEADER_CSS_STYLE = "-fx-border-width: 0 0 2 0; -fx-border-color: gray;";
    private static final String INNER_CELL_CSS_BORDER = "-fx-border-width: 1 0 0 0;";
    private static final String O_CLOCK_CSS_BORDER_STYLE = INNER_CELL_CSS_BORDER + "-fx-border-color: gray;";
    private static final String HALF_HOUR_CSS_BORDER_STYLE = INNER_CELL_CSS_BORDER + "-fx-border-color: lightgray; -fx-border-style: dotted;";

    private static final String DRAG_OVER_CELL_ADDITIONAL_CSS_STYLE = "-fx-background-color: lightgrey;";

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
            Node node = getNodeByColumnAndRow(column,0,gridPane);

            if(node != null) node.setStyle(HEADER_CSS_STYLE);
            else {
                Node newNode = new HBox();
                newNode.setStyle(HEADER_CSS_STYLE);
                gridPane.add(newNode, column, 0);
            }

            headers.add(node);
        }

        //skipping first row that contains the headers
        for(int row = 1; row < numberOfRows; row++){
            for(int column = 0; column < numberOfColumns; column++){
                Node node = getNodeByColumnAndRow(column,row,gridPane);

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

    private void setupCellBehavior(Node node) {
        //TODO: finish this
        node.setOnMouseEntered(event ->
                node.setStyle(node.getStyle() + DRAG_OVER_CELL_ADDITIONAL_CSS_STYLE)
        );
        node.setOnMouseExited(event ->
                node.setStyle(node.getStyle().replace(DRAG_OVER_CELL_ADDITIONAL_CSS_STYLE, ""))
        );
    }

    private void initializeContentLanguage() {
        mondayTag.setText(AppSettings.language().getItem("monday"));
        tuesdayTag.setText(AppSettings.language().getItem("tuesday"));
        wednesdayTag.setText(AppSettings.language().getItem("wednesday"));
        thursdayTag.setText(AppSettings.language().getItem("thursday"));
        fridayTag.setText(AppSettings.language().getItem("friday"));
    }

    //pre: gridPane not null
    //post: if node is found, returns the reference to the node. Otherwise returns null
    private Node getNodeByColumnAndRow(Integer column, Integer row, GridPane gridPane) {
        Node result = null;

        //Only way this works is using Integers and this particular line of code. If you swap equals order, you'll get NPE
        for(Node n : gridPane.getChildren()) {
            if (column.equals(GridPane.getColumnIndex(n)) && row.equals(GridPane.getRowIndex(n))) {
                result = n;
                break;
            }
        }

        return result;
    }

    public ObservableList<Node> getInnerCells() { return innerCells; }
}
