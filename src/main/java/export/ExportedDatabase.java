package export;

import model.*;
import service.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author EnricRG
 */
public class ExportedDatabase {

    private List<ExportedEvent> events;
    private List<ExportedSubject> subjects;
    private List<ExportedCourse> courses;
    private List<ExportedResource> resources;

    public ExportedDatabase(AppDatabase database) {
        this.events = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.resources = new ArrayList<>();

        database.eventDatabase().getElements().foreach(e -> this.events.add(new ExportedEvent(e)));
        database.subjectDatabase().getElements().foreach(s -> this.subjects.add(new ExportedSubject(s)));
        database.courseDatabase().getElements().foreach(c -> this.courses.add(new ExportedCourse(c)));
        database.resourceDatabase().getElements().foreach(r -> this.resources.add(new ExportedResource(r)));
    }

    public AppDatabase toDatabase() {
        Map<Long, Resource> resourceMap = new HashMap<>();
        Map<Long, Course> courseMap = new HashMap<>();
        Map<Long, Subject> subjectMap = new HashMap<>();
        Map<Long, Event> eventMap = new HashMap<>();

        this.resources.forEach(r -> resourceMap.put(r.getId(), r.toResource()));
        this.courses.forEach(c -> courseMap.put(c.getId(), c.toPartialCourse()));
        this.subjects.forEach(s -> subjectMap.put(s.getId(), s.toPartialSubject()));
        this.events.forEach(e -> eventMap.put(e.getId(), e.toPartialEvent()));


        return new AppDatabase(
            new EventDatabase(events.stream().map(ExportedEvent::toPartialEvent).collect(Collectors.toList())),
            new SubjectDatabase(subjects.stream().map(ExportedSubject::toPartialSubject).collect(Collectors.toList())),
            new CourseDatabase(courses.stream().map(ExportedCourse::toPartialCourse).collect(Collectors.toList())),
            new ResourceDatabase(resources.stream().map(ExportedResource::toResource).collect(Collectors.toList())));
    }

    public List<ExportedEvent> getEvents() {
        return events;
    }

    public void setEvents(List<ExportedEvent> events) {
        this.events = events;
    }

    public List<ExportedSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<ExportedSubject> subjects) {
        this.subjects = subjects;
    }

    public List<ExportedCourse> getCourses() {
        return courses;
    }

    public void setCourses(List<ExportedCourse> courses) {
        this.courses = courses;
    }

    public List<ExportedResource> getResources() {
        return resources;
    }

    public void setResources(List<ExportedResource> resources) {
        this.resources = resources;
    }

    public static class ExportedResource {

        private Long id;
        private String name;
        private Integer capacity;
        private

        public ExportedResource(Resource r) {
        }

        public Resource toResource() {

        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public enum ExportedPeriodicity {
        WEEKLY, BIWEEKLY;

        public static Weeks.Periodicity toPeriodicity(ExportedPeriodicity exportedPeriodicity) {
            switch (exportedPeriodicity) {
                case WEEKLY:
                    return Weeks.Weekly$.MODULE$;
                case BIWEEKLY:
                    return Weeks.Biweekly$.MODULE$;
            }
            throw new IllegalStateException("Unknown exported event periodicity " + exportedPeriodicity);
        }

        public static ExportedPeriodicity toExported(Weeks.Periodicity periodicity) {
            if (Objects.equals(Weeks.Weekly$.MODULE$, periodicity)) {
                return WEEKLY;
            } else if (Objects.equals(Weeks.Biweekly$.MODULE$, periodicity)) {
                return BIWEEKLY;
            }
            throw new IllegalStateException("Unknown event periodicity " + periodicity);
        }
    }

    public enum ExportedWeek {
        A_WEEK, B_WEEK, EVERY_WEEK;

        public static Weeks.Week toWeek(ExportedWeek exportedPeriodicity) {
            switch (exportedPeriodicity) {
                case A_WEEK:
                    return Weeks.AWeek$.MODULE$;
                case B_WEEK:
                    return Weeks.BWeek$.MODULE$;
                case EVERY_WEEK:
                    return Weeks.EveryWeek$.MODULE$;
            }
            throw new IllegalStateException("Unknown exported event week " + exportedPeriodicity);
        }

        public static ExportedWeek toExported(Weeks.Week week) {
            if (Objects.equals(Weeks.AWeek$.MODULE$, week)) {
                return A_WEEK;
            } else if (Objects.equals(Weeks.BWeek$.MODULE$, week)) {
                return B_WEEK;
            } else if (Objects.equals(Weeks.EveryWeek$.MODULE$, week)) {
                return EVERY_WEEK;
            }
            throw new IllegalStateException("Unknown event week " + week);
        }
    }

    public enum ExportedQuarter {
        FIRST, SECOND;

        public static Quarter toQuarter(ExportedQuarter exportedQuarter) {
            switch (exportedQuarter) {
                case FIRST:
                    return FirstQuarter$.MODULE$;
                case SECOND:
                    return SecondQuarter$.MODULE$;
            }
            throw new IllegalStateException("Unknown exported quarter " + exportedQuarter);
        }

        public static ExportedQuarter toExported(Quarter quarter) {
            if (Objects.equals(FirstQuarter$.MODULE$, quarter)) {
                return FIRST;
            } else if (Objects.equals(SecondQuarter$.MODULE$, quarter)) {
                return SECOND;
            }
            throw new IllegalStateException("Unknown quarter " + quarter);
        }
    }
}
