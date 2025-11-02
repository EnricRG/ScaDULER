package export;

import misc.EventTypeIncompatibility;

/**
 * @author EnricRG
 */
public class ExportedEventTypeIncompatibility {
    private ExportedEventType firstType;
    private ExportedEventType secondType;

    public ExportedEventTypeIncompatibility(EventTypeIncompatibility incompatibility) {
        this.firstType = ExportedEventType.toExported(incompatibility.getFirstType());
        this.secondType = ExportedEventType.toExported(incompatibility.getSecondType());
    }

    public EventTypeIncompatibility toIncompatibility() {
        return new EventTypeIncompatibility(
            ExportedEventType.toEventType(this.firstType),
            ExportedEventType.toEventType(this.secondType)
        );
    }

    public ExportedEventType getFirstType() {
        return firstType;
    }

    public void setFirstType(ExportedEventType firstType) {
        this.firstType = firstType;
    }

    public ExportedEventType getSecondType() {
        return secondType;
    }

    public void setSecondType(ExportedEventType secondType) {
        this.secondType = secondType;
    }
}
