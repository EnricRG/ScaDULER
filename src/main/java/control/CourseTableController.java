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

public class CourseTableController implements Initializable {

    public GridPane gridPane;

    public TitledPane monday;
    public TitledPane tuesday;
    public TitledPane wednesday;
    public TitledPane thursday;
    public TitledPane friday;

    public VBox mondayContent;
    public VBox tuesdayContent;
    public VBox wednesdayContent;
    public VBox thursdayContent;
    public VBox fridayContent;

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

        monday.setOnDragDropped(unassignedEventHandling(mondayContent));
        tuesday.setOnDragDropped(unassignedEventHandling(tuesdayContent));
        wednesday.setOnDragDropped(unassignedEventHandling(wednesdayContent));
        thursday.setOnDragDropped(unassignedEventHandling(thursdayContent));
        friday.setOnDragDropped(unassignedEventHandling(fridayContent));
    }

    private EventHandler<? super DragEvent> enableDragging() {
        return dragEvent -> {
            System.out.println("Drag Over");
            dragEvent.acceptTransferModes(TransferMode.ANY);
        };
    }

    private EventHandler<? super DragEvent> unassignedEventHandling(VBox target) {
        return dragEvent -> mc.moveUnassignedEvent((Node) dragEvent.getGestureSource(), mc.rightPane_VBox ,target);

        /*{



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

        };*/
    }


}
