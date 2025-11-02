package export;

import model.QuarterData;
import service.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author EnricRG
 */
public class ExportedQuarterData {
    private Integer intervalsPerWeek;
    private List<ExportedScheduledTimeslot> scheduledEvents;

    public ExportedQuarterData(QuarterData quarterData) {
        this.intervalsPerWeek = quarterData.getSchedule().intervalsPerWeek();
        this.scheduledEvents = new ArrayList<>();
        quarterData.getSchedule().getAllPairs().foreach(pair -> {
            List<Long> scheduledEventsInSlot = scala.collection.JavaConverters.asJavaCollection(pair._2()).stream()
                .map(Identifiable::getID)
                .collect(Collectors.toList());

            scheduledEvents.add(
                new ExportedScheduledTimeslot((Integer) pair._1(), scheduledEventsInSlot)
            );
            return null;
        });
    }

    public Integer getIntervalsPerWeek() {
        return intervalsPerWeek;
    }

    public void setIntervalsPerWeek(Integer intervalsPerWeek) {
        this.intervalsPerWeek = intervalsPerWeek;
    }

    public List<ExportedScheduledTimeslot> getScheduledEvents() {
        return scheduledEvents;
    }

    public void setScheduledEvents(List<ExportedScheduledTimeslot> scheduledEvents) {
        this.scheduledEvents = scheduledEvents;
    }
}
