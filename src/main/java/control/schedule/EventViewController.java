package control.schedule;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Event;
import model.Subject;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class EventViewController implements Initializable {

    protected final Event event;

    public VBox mainBox;
    public Label eventDisplayName;
    public Label eventDisplayAdditionalInfo;

    public EventViewController(Event event){
        this.event = event;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeEventView();
        initializeBehavior();
    }

    protected void initializeEventView() {
        eventDisplayName.setText(
            !event.getShortName().trim().isEmpty() ?
                String.format("[%s]",event.getShortName()) :
                String.format("%s", event.getName())
        );
        eventDisplayAdditionalInfo.setText(String.format("(%s) (%s)", event.getEventType(), event.getPeriodicity()));
        setEventColor();
        setEventColorFrame();
    }

    protected void setEventColor(){
        mainBox.setStyle("-fx-background-color: #" + event.getEventType().color().toString().substring(2) + ";");
    }

    protected void setEventColorFrame(){
        Subject eventSubject = event.getSubject().isDefined() ? event.getSubject().get() : null;
        if(eventSubject != null) mainBox.setStyle(mainBox.getStyle() + "-fx-border-width: 2; -fx-border-color: #" + eventSubject.getColor().toJFXColor().toString().substring(2) + ";");
    }

    protected abstract void initializeBehavior();

    public Event getEvent() {
        return event;
    }

    public Node getNode() {
        return mainBox;
    }
}
