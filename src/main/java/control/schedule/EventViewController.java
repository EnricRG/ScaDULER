package control.schedule;

import control.MainController;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.NewEvent;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class EventViewController implements Initializable {

    protected final MainController controller;
    protected final NewEvent event;

    public VBox mainBox;
    public Label eventDisplayName;
    public Label eventDisplayAdditionalInfo;

    public EventViewController(MainController controller, NewEvent event){
        this.controller = controller;
        this.event = event;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeEventView();
        initializeBehavior();
    }

    protected void initializeEventView() {
        eventDisplayName.setText(String.format("[%s] %s",event.getShortName(), event.getName()));
        eventDisplayAdditionalInfo.setText(String.format("(%s) (%s)", event.getEventType(), event.getWeek()));
    }

    protected abstract void initializeBehavior();

    public NewEvent getEvent() { return event; }

    public Node getNode() {
        return mainBox;
    }
}
