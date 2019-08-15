package control.schedule;

import app.AppSettings;
import control.MainController;
import javafx.scene.control.TitledPane;
import model.NewEvent;

public class AssignedEventViewController extends EventViewController {

    public TitledPane hourPane;

    public AssignedEventViewController(MainController controller, NewEvent event) {
        super(controller, event);
    }

    public AssignedEventViewController(MainController controller, EventViewController oldController) {
        super(controller, oldController.getEvent());
    }

    public void setHour(Integer interval){
        hourPane.setText(computeHour(interval));
    }

    private String computeHour(Integer interval) {
        Integer dayRelativeInterval = interval % AppSettings.timeSlotsPerDay();
        Integer slotsPerHour = 60/AppSettings.TimeSlotDuration();
        Integer minutes = (dayRelativeInterval % slotsPerHour) * AppSettings.TimeSlotDuration();
        Integer hours = AppSettings.dayStart() + dayRelativeInterval / slotsPerHour;
        return String.format("%02d:%02d",hours,minutes);
    }

    @Override
    protected void initializeEventView(){
        super.initializeEventView();
        //TODO ad event color
    }

    @Override
    protected void initializeBehavior() {
        hourPane.setOnDragDetected(event -> {

        });
    }
}
