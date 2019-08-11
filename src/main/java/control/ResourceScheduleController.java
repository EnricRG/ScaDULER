package control;

import javafx.scene.Node;
import model.Resource;

import java.net.URL;
import java.util.ResourceBundle;

public class ResourceScheduleController extends ScheduleController {

    private static final String SET_STATE = "-fx-background-color: steelblue;";

    protected Resource resource;

    public ResourceScheduleController(Resource resource){
        this.resource = resource;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        initializeCustomBehavior();
    }

    private void initializeCustomBehavior() {
        for(Node cell : getInnerCells()) {
            setState(cell);
            configureCell(cell);
        }
    }

    //TODO: improve efficiency pre-computing interval and passing it as a parameter.
    private void setState(Node cell) {
        if(resource.availability().isAvailable(computeInterval(gridPane, cell))){
            cell.setStyle(cell.getStyle() + SET_STATE);
        }
        else cell.setStyle(cell.getStyle().replace(SET_STATE, ""));
    }

    //TODO: allow dragging
    private void configureCell(Node cell) {
        cell.setOnMouseClicked(event -> {
            resource.availability().flip(computeInterval(gridPane, cell));
            setState(cell);
            event.consume();
        });
    }


}
