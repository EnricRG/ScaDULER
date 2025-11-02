package export;

import java.util.List;

/**
 * @author EnricRG
 */
public class ExportedScheduledTimeslot {
    private Integer slot;
    private List<Long> eventIds;

    public ExportedScheduledTimeslot(Integer slot, List<Long> eventIds) {
        this.slot = slot;
        this.eventIds = eventIds;
    }

    public Integer getSlot() {
        return slot;
    }

    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }
}
