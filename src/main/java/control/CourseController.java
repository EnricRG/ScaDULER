package control;

import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseController implements Initializable {

    public GridPane gridPane;

    public TitledPane monday;
    public TitledPane tuesday;
    public TitledPane wednesday;
    public TitledPane thursday;
    public TitledPane friday;

    public VBox mondayContent;

    private MainController mc;

    public void setMainController(MainController mc){
        this.mc = mc;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureEventDropListeners();
    }

    private void configureEventDropListeners() {

        monday.setOnMouseEntered(event ->{
            System.out.println("Mouse Entered");

        });
/*
        monday.setOnDragOver(dragEvent -> {
            System.out.println("Drag Over");
            dragEvent.acceptTransferModes(TransferMode.ANY);
        });
*/
        monday.setOnDragDropped(dragEvent -> {
            System.out.println("Drag Dropped");
            //mondayContent.getChildren().add((Node)dragEvent.getGestureTarget());
        });

        monday.setOnDragOver(enableDragging());
        tuesday.setOnDragOver(enableDragging());
        wednesday.setOnDragOver(enableDragging());
        thursday.setOnDragOver(enableDragging());
        friday.setOnDragOver(enableDragging());

        monday.setOnDragDropped(unassignedEventHandling());
        tuesday.setOnDragDropped(unassignedEventHandling());
        wednesday.setOnDragDropped(unassignedEventHandling());
        thursday.setOnDragDropped(unassignedEventHandling());
        friday.setOnDragDropped(unassignedEventHandling());
    }

    private EventHandler<? super DragEvent> enableDragging() {
        return dragEvent -> {
            System.out.println("Drag Over");
            dragEvent.acceptTransferModes(TransferMode.ANY);
        };
    }

    private EventHandler<? super DragEvent> unassignedEventHandling() {
        return dragEvent -> {


            Node unassignedEvent = null;
            try {
                unassignedEvent = (Node) dragEvent.getGestureSource();
            } catch (ClassCastException cce){
                cce.printStackTrace();
            }

            if(unassignedEvent != null){
                //This code is absolute evil. This will be removed as soon as possible.
                if( ((Node) dragEvent.getGestureSource()).getParent() == mc.rightPane_VBox){
                    mc.rightPane_VBox.getChildren().remove(unassignedEvent);
                    mondayContent.getChildren().add(unassignedEvent);
                }
                else if(((Node) dragEvent.getGestureSource()).getParent() == mondayContent){
                    mondayContent.getChildren().remove(unassignedEvent);
                    mc.rightPane_VBox.getChildren().add(unassignedEvent);
                }

            }

            //unassignedEvent.toBack();
            //unassignedEvent.setManaged(true);

        };
    }


}
