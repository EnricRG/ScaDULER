package control.schedule;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Event;
import model.Subject;
import scala.Option;

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
            !event.shortName().trim().isEmpty() ?
                String.format("[%s]",event.shortName()) :
                String.format("%s", event.name())
        );
        eventDisplayAdditionalInfo.setText(String.format("(%s) (%s)", event.eventType(), event.periodicity()));
        setEventColor();
        setEventColorFrame();
    }

    protected void setEventColor(){
        mainBox.setStyle("-fx-background-color: #" + event.eventType().color().toString().substring(2) + ";");
    }

    protected void setEventColorFrame(){
        Option<Subject> eventSubject = event.subject();
        if(eventSubject.nonEmpty() && eventSubject.get().color().nonEmpty())
            mainBox.setStyle(mainBox.getStyle() + "-fx-border-width: 2; -fx-border-color: #" +
                eventSubject.get().color().get().toJFXColor().toString().substring(2) + ";");
        else if(eventSubject.nonEmpty())
            mainBox.setStyle(mainBox.getStyle() + "-fx-border-width: 2; -fx-border-color: #" +
                Subject.DefaultColor().toString().substring(2) + ";");
    }

    protected abstract void initializeBehavior();

    public Event getEvent() {
        return event;
    }

    public Node getNode() {
        return mainBox;
    }
}
