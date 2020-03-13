package control.schedule;

import app.AppSettings;
import control.StageController;
import control.schedule.cell.ScheduleCell;
import control.schedule.cell.ScheduleCellFactory;
import factory.DualWeekScheduleViewFactory;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import model.Resource;
import util.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class ResourceAvailabilityController extends StageController{

    private class ResourceScheduleCellFactory implements ScheduleCellFactory{
        public ScheduleCell newCell(int row, int column){ return new ResourceAvailabilityCell(row, column); }
    }

    private class ResourceAvailabilityCell extends ScheduleCell {

        private static final int UNSET_STATE = 0;
        private static final int SET_STATE = 1;
        private static final int SELECTED_STATE = 2;

        private int week;
        private int interval;

        private int quantity;
        private int state = UNSET_STATE;
        private Label quantityLabel;

        private boolean selected = false;

        public ResourceAvailabilityCell(int row, int column){
            this(0, row, column);
        }

        public ResourceAvailabilityCell(int quantity, int row, int column){
            super(row, column);
            this.setAlignment(Pos.CENTER);
            quantityLabel = new Label();
            this.getChildren().add(quantityLabel);
            setQuantity(quantity, false);
        }

        public void setWeek(int week){ this.week = week; }
        public void setInterval(int interval){ this.interval = interval; }

        public void setState(int state, boolean silent){
            if (!silent) setStateStyle(state);
            this.state = state;
        }

        private void setStateStyle(int state) {
            //remove old style
            String neutralStyle = removeStateFromStyle(this.getStyle(), this.state);
            //set new style
            String newStyle = addStateToStyle(neutralStyle, state);

            this.setStyle(newStyle);
        }

        String removeStateFromStyle(String style, int state){
            String newStyle;

            if(state == SET_STATE) newStyle = style.replace(SET_STATE_STYLE, "");
            else if(state == SELECTED_STATE) newStyle = style.replace(SELECTED_STATE_STYLE, "");
            else newStyle = style;

            return newStyle;
        }

        String addStateToStyle(String style, int state){
            String newStyle;

            if(state == SET_STATE) newStyle = style + SET_STATE_STYLE;
            else if(state == SELECTED_STATE) newStyle = style + SELECTED_STATE_STYLE;
            else newStyle = style;

            return newStyle;
        }

        public void setQuantity(int quantity, boolean silent){
            if(quantity <= 0) {
                this.setState(UNSET_STATE, silent);
                quantityLabel.setVisible(false);
            }
            else {
                quantityLabel.setText(Integer.toString(quantity));
                this.setState(SET_STATE, silent);
                quantityLabel.setVisible(true);
            }
            this.quantity = quantity;
        }

        public void select(){
            selected = true;
            setStateStyle(SELECTED_STATE);
        }

        public void deselect(){
            selected = false;
            setStyle(removeStateFromStyle(this.getStyle(), SELECTED_STATE));
            //TODO improve: redundant job at setStateStyle
            setStateStyle(state);
        }

        public boolean selected() { return selected; }

    }

    private class CellEventHandler implements EventHandler<MouseEvent>{

        private final ResourceAvailabilityCell cell;

        CellEventHandler(ResourceAvailabilityCell cell){
            this.cell = cell;
        }

        @Override
        public void handle(MouseEvent event) {
            if(event.getEventType() == MouseEvent.DRAG_DETECTED) {
                dragging = true;
                cell.startFullDrag();
            }
            else if(event.getEventType() == MouseDragEvent.MOUSE_DRAG_ENTERED){
                if(dragging){
                    modifyCellState(event, cell);
                }
            }
            else if (event.getEventType() == MouseEvent.MOUSE_RELEASED){
                if(!dragging) { //check if it was a click
                    modifyCellState(event, cell);
                }
                else dragging = false;
            }

            event.consume();
        }

        private void modifyCellState(MouseEvent mouseEvent, ResourceAvailabilityCell cell){
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                if(!cell.selected()){
                    cell.select();
                    selection.add(cell);
                }
            }
            else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                if(cell.selected()){
                    cell.deselect();
                    selection.remove(cell);
                }
            }
        }
    }

    private DualWeekScheduleViewController<WeekScheduleController,WeekScheduleController> schedule;

    public VBox mainContainer;

    public Label selectedIntervalsTag;
    public Label selectedIntervalsNumberTag;

    public TextField quantityField;
    public Button setButton;
    public Button plusOneButton;
    public Button zeroButton;
    public Button minusOneButton;

    public Label deselectExplanationTag;

    private Set<ResourceAvailabilityCell> selection = new HashSet<>();
    private boolean dragging = false;

    private static final String SELECTED_STATE_STYLE = "-fx-background-color: gold;";
    private static final String SET_STATE_STYLE = "-fx-background-color: skyblue;";

    protected Resource resource;

    public ResourceAvailabilityController(Resource resource){
        ScheduleCellFactory factory = new ResourceScheduleCellFactory();
        schedule = new DualWeekScheduleViewController<>(new WeekScheduleController(factory),
                new WeekScheduleController(factory));
        this.resource = resource;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            mainContainer.getChildren().add(0, new DualWeekScheduleViewFactory<>(schedule).load());
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        initializeContentLanguage();
        initializeCustomBehavior();
    }

    private void initializeContentLanguage() {
        selectedIntervalsTag.setText(AppSettings.language().getItem("resourceAvailability_selectedIntervalsTag"));
        selectedIntervalsNumberTag.setText("0");

        setButton.setText(AppSettings.language().getItem("resourceAvailability_setButton"));
        plusOneButton.setText("+1");
        zeroButton.setText("0");
        minusOneButton.setText("-1");

        deselectExplanationTag.setText(AppSettings.language().getItem("resourceAvailability_deselectExplanation"));
    }

    private void initializeCustomBehavior() {

        plusOneButton.setOnAction(actionEvent -> {
            incrementSelectionIn(1);
            actionEvent.consume();
        });
        minusOneButton.setOnAction(actionEvent -> {
            decrementSelectionIn(1);
            actionEvent.consume();
        });
        zeroButton.setOnAction(actionEvent -> {
            setSelectionTo(0);
            actionEvent.consume();
        });
        setButton.setOnAction(actionEvent -> {
            setSelectionFromQuantityField();
            actionEvent.consume();
        });

        quantityField.setOnKeyReleased(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) setSelectionFromQuantityField();
            else if(keyEvent.getCode() == KeyCode.ESCAPE) setButton.requestFocus();
            //keyEvent.consume();
        });

        for(int week = 0; week <= 1; week++) {
            WeekScheduleController c = week < 1 ? schedule.firstWeekController : schedule.secondWeekController;
            for (ScheduleCell cell : c.getInnerCells()) {
                Integer interval = computeInterval(cell);
                configureCell((ResourceAvailabilityCell) cell, week, interval);
            }
        }
    }

    //pre: cell not null
    private void configureCell(ResourceAvailabilityCell cell, int week, int interval) {
        cell.addEventHandler(MouseEvent.ANY, new CellEventHandler(cell));
        cell.setWeek(week);
        cell.setInterval(interval);
        cell.setQuantity(resource.getAvailability(week, interval),false);
    }

    private Integer computeInterval(ScheduleCell cell) {
        int day = cell.getColumn() - 1; //subtract first column (hours)
        int dayInterval = cell.getRow() - 1; //subtract first row (headers)

        return Utils.computeInterval(day, dayInterval);
    }

    private void incrementSelectionIn(int amount) {
        for(ResourceAvailabilityCell cell : selection){
            resource.getAvailability().increment(cell.week, cell.interval, amount);
            cell.setQuantity(resource.getAvailability(cell.week, cell.interval), true);
        }
    }

    private void decrementSelectionIn(int amount) { incrementSelectionIn(-amount); }

    private void setSelectionTo(int quantity){
        for(ResourceAvailabilityCell cell : selection){
            if(quantity <= 0) resource.getAvailability().unset(cell.week, cell.interval);
            else resource.getAvailability().set(cell.week, cell.interval, quantity);
            cell.setQuantity(quantity, true);
        }
    }

    public void unsetSelection(){
        setSelectionTo(0);
    }

    private void setSelectionFromQuantityField(){
        int quantity = getNumberFromField();
        if (quantity > 0) setSelectionTo(quantity);
    }

    private int getNumberFromField(){
        try{
            return Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException nfe){
            return -1;
        }
    }

    public void clearSelection(){
        if(!selection.isEmpty()) {
            for (ResourceAvailabilityCell cell : selection) {
                cell.deselect();
            }
            selection = new HashSet<>();
        }
    }

}
