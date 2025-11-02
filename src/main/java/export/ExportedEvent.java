package export;

import model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * @author EnricRG
 */
public class ExportedEvent {

    private Long id;
    private String name;
    private String shortName;
    private String description;
    private ExportedEventType eventType;
    private Integer duration;
    private ExportedDatabase.ExportedPeriodicity periodicity;
    private Long subjectId;
    private Long courseId;
    private Long resourceId;
    private ExportedDatabase.ExportedQuarter quarter;
    private List<Long> incompatibilities;
    private Integer startInterval;
    private ExportedDatabase.ExportedWeek week;

    public ExportedEvent(Event e) {
        this.id = e.getID();
        this.name = e.name();
        this.shortName = e.shortName();
        this.description = e.description();
        this.eventType = ExportedEventType.toExported(e.eventType());
        this.duration = e.duration();
        this.periodicity = ExportedDatabase.ExportedPeriodicity.toExported(e.periodicity());
        this.subjectId = e.subject().isEmpty() ? null : e.subject().get().getID();
        this.courseId = e.course().isEmpty() ? null : e.course().get().getID();
        this.resourceId = e.neededResource().isEmpty() ? null : e.neededResource().get().getID();
        this.quarter = e.quarter().isEmpty() ? null : ExportedDatabase.ExportedQuarter.toExported(e.quarter().get());
        this.startInterval = e.getStartInterval();
        this.week = e.week().isEmpty() ? null : ExportedDatabase.ExportedWeek.toExported(e.week().get());
        this.incompatibilities = new ArrayList<>();
        e.incompatibilities().foreach(i -> incompatibilities.add(i.getID()));
    }

    public Event toPartialEvent() {
        Event event = new Event(this.id);

        event.name_$eq(this.name);
        event.shortName_$eq(this.shortName);
        event.description_$eq(this.description);
        event.eventType_$eq(ExportedEventType.toEventType(this.eventType));
        event.duration_$eq(this.duration);
        event.periodicity_$eq(ExportedDatabase.ExportedPeriodicity.toPeriodicity(this.periodicity));
        if (this.quarter != null) {
            event.quarter_$eq(ExportedDatabase.ExportedQuarter.toQuarter(this.quarter));
        }
        event.setStartInterval(this.startInterval);
        if (this.week != null) {
            event.week_$eq(ExportedDatabase.ExportedWeek.toWeek(this.week));
        }
        // Doesn't map subject, course, incompatibilities or resources, that's done on a second pass

        return event;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExportedEventType getEventType() {
        return eventType;
    }

    public void setEventType(ExportedEventType eventType) {
        this.eventType = eventType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ExportedDatabase.ExportedPeriodicity getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(ExportedDatabase.ExportedPeriodicity periodicity) {
        this.periodicity = periodicity;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public ExportedDatabase.ExportedQuarter getQuarter() {
        return quarter;
    }

    public void setQuarter(ExportedDatabase.ExportedQuarter quarter) {
        this.quarter = quarter;
    }

    public List<Long> getIncompatibilities() {
        return incompatibilities;
    }

    public void setIncompatibilities(List<Long> incompatibilities) {
        this.incompatibilities = incompatibilities;
    }

    public Integer getStartInterval() {
        return startInterval;
    }

    public void setStartInterval(Integer startInterval) {
        this.startInterval = startInterval;
    }

    public ExportedDatabase.ExportedWeek getWeek() {
        return week;
    }

    public void setWeek(ExportedDatabase.ExportedWeek week) {
        this.week = week;
    }
}
