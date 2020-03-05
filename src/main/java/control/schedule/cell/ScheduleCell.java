package control.schedule.cell;

import javafx.scene.layout.HBox;

public abstract class ScheduleCell extends HBox {

    protected int row;
    protected int column;

    public ScheduleCell(int r, int c){
        row = r;
        column = c;
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }
}

