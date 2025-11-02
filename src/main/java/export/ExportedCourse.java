package export;

import model.Course;

/**
 * @author EnricRG
 */
public class ExportedCourse {

    private Long id;
    private String name;
    private String description;
    private ExportedQuarterData firstQuarterData;
    private ExportedQuarterData secondQuarterData;

    public ExportedCourse(Course c) {
        this.id = c.getID();
        this.name = c.name();
        this.description = c.description();
        this.firstQuarterData = new ExportedQuarterData(c.firstQuarterData());
        this.secondQuarterData = new ExportedQuarterData(c.secondQuarterData());
    }

    public Course toPartialCourse() {
        Course course = new Course(this.id);

        course.name_$eq(this.name);
        course.description_$eq(this.description);
        // Not mapping the schedule now, it will be done in a second pass

        return course;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExportedQuarterData getFirstQuarterData() {
        return firstQuarterData;
    }

    public void setFirstQuarterData(ExportedQuarterData firstQuarterData) {
        this.firstQuarterData = firstQuarterData;
    }

    public ExportedQuarterData getSecondQuarterData() {
        return secondQuarterData;
    }

    public void setSecondQuarterData(ExportedQuarterData secondQuarterData) {
        this.secondQuarterData = secondQuarterData;
    }
}
