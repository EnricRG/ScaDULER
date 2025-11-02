package export;

import model.*;

import java.util.Objects;

/**
 * @author EnricRG
 */
public enum ExportedEventType {
    THEORY_EVENT, LABORATORY_EVENT, PROBLEMS_EVENT, COMPUTER_EVENT, SPECIAL_EVENT;

    public static EventType toEventType(ExportedEventType exportedEventType) {
        switch (exportedEventType) {
            case THEORY_EVENT:
                return TheoryEvent$.MODULE$;
            case LABORATORY_EVENT:
                return LaboratoryEvent$.MODULE$;
            case PROBLEMS_EVENT:
                return ProblemsEvent$.MODULE$;
            case COMPUTER_EVENT:
                return ComputerEvent$.MODULE$;
            case SPECIAL_EVENT:
                return SpecialEvent$.MODULE$;
        }
        throw new IllegalStateException("Unknown exported event type " + exportedEventType);
    }

    public static ExportedEventType toExported(EventType eventType) {
        if (Objects.equals(TheoryEvent$.MODULE$, eventType)) {
            return THEORY_EVENT;
        } else if (Objects.equals(LaboratoryEvent$.MODULE$, eventType)) {
            return LABORATORY_EVENT;
        } else if (Objects.equals(ComputerEvent$.MODULE$, eventType)) {
            return COMPUTER_EVENT;
        } else if (Objects.equals(ProblemsEvent$.MODULE$, eventType)) {
            return PROBLEMS_EVENT;
        } else if (Objects.equals(SpecialEvent$.MODULE$, eventType)) {
            return SPECIAL_EVENT;
        }
        throw new IllegalStateException("Unknown event type " + eventType);
    }
}
