package view;

import control.MainController;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import model.OldEvent;

/*class EventView extends VBox{

    public OldEvent event;

    public EventView(VBox v, OldEvent e){
        super(v);
        event = e;
    }
}*/

@Deprecated
public class DraggableVBox extends VBox {

    private double mouseX ;
    private double mouseY ;

    public DraggableVBox(VBox vb, MainController c){
        super(vb);

        setManaged(false);

        setOnDragDetected(event-> {
            System.out.println("onDragDetected");
            Dragboard db = startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString("hi all");
            db.setContent(content);
            event.consume();
        });
/*
        setOnMousePressed(event -> {
            //toFront();
            toBack();
            System.out.println("MousePressed");

            Bounds boundsInScene = this.localToScene(this.getBoundsInLocal());

            c.rightPane_VBox.getChildren().remove(this);
            if(getParent() != c.mainBorderPane) c.mainBorderPane.getChildren().add(this);

            mouseX = event.getSceneX() ;
            mouseY = event.getSceneY() ;

            setLayoutX(boundsInScene.getMinX());
            setLayoutY(boundsInScene.getMinY());

            event.consume();
        });

        setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mouseX ;
            double deltaY = event.getSceneY() - mouseY ;
            relocate(getLayoutX() + deltaX, getLayoutY() + deltaY);
            mouseX = event.getSceneX() ;
            mouseY = event.getSceneY() ;
            //event.consume();
        });*/
/*
        setOnMouseReleased(event -> {
            event.consume();
        });*/
    }
}
