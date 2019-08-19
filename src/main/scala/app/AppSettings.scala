package app

object FXMLPaths {
    val MainInterface: String = "src/main/fxml/main_border_pane.fxml"
    val UnassignedEvent: String = "src/main/fxml/unassigned_event_box.fxml"
    val AssignedEvent: String = "src/main/fxml/assigned_event_box.fxml"
    val CoursePanel: String = "src/main/fxml/course_grid.fxml"

    val CourseForm: String = "src/main/fxml/course_form.fxml"
    val CourseResourceManagerForm: String = "src/main/fxml/course_resource_manager.fxml"
    val SubjectForm: String = "src/main/fxml/subject_form.fxml"
    val EventForm: String = "src/main/fxml/event_form.fxml"
    val EventIncompatibilityFrom: String = "src/main/fxml/event_incompatibility_manager.fxml"

    val ManageResourcesPanel: String = "src/main/fxml/resource_manager.fxml"
    val ManageCoursesPanel: String = "src/main/fxml/course_manager.fxml"
    val ManageSubjectsPanel: String = "src/main/fxml/subject_manager.fxml"
    val ManageEventsPanel: String = "src/main/fxml/event_manager.fxml"

    val GenericSchedule: String = "src/main/fxml/schedule_view.fxml"
    val DualWeekGenericSchedule: String = "src/main/fxml/dual_week_schedule_view.fxml"

    val CourseSchedule: String = "src/main/fxml/course_schedule_view.fxml"
}

object AppSettings {

    final val applicationTitle: String = "ScaDULER v0.1"
    final val TimeSlotDuration: Int = 30
    final val TimeSlotsPerHour: Int = 60/TimeSlotDuration

    var language: Language = DefaultLanguage

    var timeSeparatorSymbol: String = ":"
    var dayStart: Int = 8 //8:00 AM
    var dayEnd: Int = 19 //19:00

    var timeSlotsPerDay:Int = 22
    var days: Int = 5
    var timeSlots: Int = timeSlotsPerDay*days

    var maxEventDuration: Int = 4
    var eventViewColumnPercentage: Double = 0.9

    var minQuantityPerResource: Int = 1

    var defaultTimeout: Double = 10 //seconds

    object eventFormSettings {
        var width: Int = 640
        var height: Int = 450

        var fieldSpacing: Int = 4

        var nameFieldWidth: Int = width/2
        var shortNameFieldWidth: Int = width/4

    }
}
