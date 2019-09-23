package control.schedule;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.*;
import model.Resource;

import java.net.URL;
import java.util.ResourceBundle;

public class ResourceScheduleController extends DualWeekScheduleViewController<ScheduleController, ScheduleController> {

    private class CellEventHandler implements EventHandler<MouseEvent>{

        private final Node cell;
        private final Integer week;
        private final Integer interval;

        CellEventHandler(Node cell, Integer week, Integer interval){
            this.cell = cell;
            this.week = week;
            this.interval = interval;
        }

        @Override
        public void handle(MouseEvent event) {
            if(event.getEventType() == MouseEvent.DRAG_DETECTED) {
                dragging = true;
                cell.startFullDrag();
            }
            else if(event.getEventType() == MouseDragEvent.MOUSE_DRAG_ENTERED){
                if(dragging){
                    modifyCellState(event,cell,week,interval);
                }
            }
            else if (event.getEventType() == MouseEvent.MOUSE_RELEASED){
                if(!dragging) {
                    flipCellState(cell, week, interval);
                }
                else dragging = false;
            }

            event.consume();
        }
    }

    private boolean dragging = false;

    private static final String SET_STATE = "-fx-background-color: steelblue;";

    protected Resource resource;



    public ResourceScheduleController(Resource resource){
        super(new ScheduleController(), new ScheduleController());
        this.resource = resource;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        initializeCustomBehavior();
    }

    private void initializeCustomBehavior() {
        for(int week = 0; week <= 1; week++) {
            ScheduleController c = week < 1 ? this.firstWeekController : this.secondWeekController;
            for (Node cell : c.getInnerCells()) {
                Integer interval = ScheduleController.computeInterval(c.gridPane, cell);
                setState(cell, week, interval);
                configureCell(cell, week, interval);
            }
        }
    }

    private void setState(Node cell, Integer week, Integer interval) {
        if(resource.getAvailability().isAvailable(week, interval)){
            cell.setStyle(cell.getStyle() + SET_STATE);
        }
        else cell.setStyle(cell.getStyle().replace(SET_STATE, ""));
    }

    //pre: cell not null
    private void configureCell(Node cell, Integer week, Integer interval) {
        cell.addEventHandler(MouseEvent.ANY, new CellEventHandler(cell, week, interval));
    }

    private void setCellState(Node cell, Integer week, Integer interval) {
        resource.getAvailability().set(week, interval);
        setState(cell, week, interval);
    }

    private void unsetCellState(Node cell, Integer week, Integer interval) {
        resource.getAvailability().unset(week, interval);
        setState(cell, week, interval);
    }

    private void flipCellState(Node cell, Integer week, Integer interval){
        resource.getAvailability().flip(week, interval);
        setState(cell, week, interval);
    }

    private void modifyCellState(MouseEvent event, Node cell, Integer week, Integer interval){
        if (event.isPrimaryButtonDown()) setCellState(cell, week, interval);
        else if (event.isSecondaryButtonDown()) unsetCellState(cell, week, interval);
        else flipCellState(cell, week, interval);
    }


}
