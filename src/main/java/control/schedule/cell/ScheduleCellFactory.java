package control.schedule.cell;

public interface ScheduleCellFactory {
    ScheduleCell newCell(int row, int column);
}
