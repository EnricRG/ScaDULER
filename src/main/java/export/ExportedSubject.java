package export;

import misc.EventTypeIncompatibility;
import model.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author EnricRG
 */
public class ExportedSubject {

    private Long id;
    private String name;
    private String shortName;
    private String description;
    private Long courseId;
    private ExportedDatabase.ExportedQuarter quarter;
    private ExportedColor color;
    private List<Long> events;
    private Map<String, String> additionalInformation;
    private List<ExportedEventTypeIncompatibility> eventTypeIncompatibilities;

    public ExportedSubject(Subject s) {
        this.id = s.getID();
        this.name = s.name();
        this.shortName = s.shortName();
        this.description = s.description();
        this.courseId = s.course().isEmpty() ? null : s.course().get().getID();
        this.quarter = s.quarter().isEmpty() ? null : ExportedDatabase.ExportedQuarter.toExported(s.quarter().get());
        this.color = s.color().isEmpty() ? null : new ExportedColor(s.color().get());
        this.events = new ArrayList<>();
        s.events().foreach(e -> this.events.add(e.getID()));
        this.additionalInformation = new HashMap<>();
        s.additionalFields().toList()
            .foreach(tuple -> this.additionalInformation.put(tuple._1(), tuple._2()));
        this.eventTypeIncompatibilities = new ArrayList<>();
        s.eventTypeIncompatibilities()
            .foreach(i -> this.eventTypeIncompatibilities.add(new ExportedEventTypeIncompatibility(i)));
    }

    public Subject toPartialSubject() {
        Subject subject = new Subject(this.id);

        subject.name_$eq(this.name);
        subject.shortName_$eq(this.shortName);
        subject.description_$eq(this.description);
        if (this.quarter != null) {
            subject.quarter_$eq(ExportedDatabase.ExportedQuarter.toQuarter(this.quarter));
        }
        if (this.color != null) {
            subject.color_$eq(color.toColor());
        }
        scala.collection.Map<String, String> scalaMap =
            scala.collection.JavaConverters.mapAsScalaMap(this.additionalInformation);
        subject.additionalFields().$plus$plus(scalaMap);
        scala.collection.Iterable<EventTypeIncompatibility> scalaIncompatibilities =
            scala.collection.JavaConverters.collectionAsScalaIterable(this.eventTypeIncompatibilities.stream()
                .map(ExportedEventTypeIncompatibility::toIncompatibility).collect(Collectors.toList()));
        subject.eventTypeIncompatibilities().$plus$plus(scalaIncompatibilities);

        // Doesn't map events, this is done on a second pass

        return subject;
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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public ExportedDatabase.ExportedQuarter getQuarter() {
        return quarter;
    }

    public void setQuarter(ExportedDatabase.ExportedQuarter quarter) {
        this.quarter = quarter;
    }

    public ExportedColor getColor() {
        return color;
    }

    public void setColor(ExportedColor color) {
        this.color = color;
    }

    public List<Long> getEvents() {
        return events;
    }

    public void setEvents(List<Long> events) {
        this.events = events;
    }

    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(Map<String, String> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public List<ExportedEventTypeIncompatibility> getEventTypeIncompatibilities() {
        return eventTypeIncompatibilities;
    }

    public void setEventTypeIncompatibilities(List<ExportedEventTypeIncompatibility> eventTypeIncompatibilities) {
        this.eventTypeIncompatibilities = eventTypeIncompatibilities;
    }
}
