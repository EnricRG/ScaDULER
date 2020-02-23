package app

object FXMLPaths {
    val MainInterface: String = "src/main/fxml/main_border_pane.fxml"
    val UnassignedEvent: String = "src/main/fxml/unassigned_event_box.fxml"
    val AssignedEvent: String = "src/main/fxml/assigned_event_box.fxml"
    val CoursePanel: String = "src/main/fxml/course_grid.fxml"

    val CourseForm: String = "src/main/fxml/course_form.fxml"
    val CourseResourceManagerForm: String = "src/main/fxml/course_resource_manager.fxml"
    val SubjectForm: String = "src/main/fxml/subject_form.fxml"
    val SubjectIncompatibilityForm = "src/main/fxml/subject_event_incompatibility_manager.fxml"
    val EventForm: String = "src/main/fxml/event_form.fxml"
    val EventIncompatibilityFrom: String = "src/main/fxml/event_incompatibility_manager.fxml"

    val EntityManagerPanel: String = "src/main/fxml/entity_manager.fxml"
    val ManageResourcesPanel: String = "src/main/fxml/resource_manager.fxml"

    val GenericSchedule: String = "src/main/fxml/schedule_view.fxml"
    val DualWeekGenericSchedule: String = "src/main/fxml/dual_week_schedule_view.fxml"

    val CourseSchedule: String = "src/main/fxml/course_schedule_view.fxml"

    val BasicAlert: String = "src/main/fxml/basic_alert.fxml"
    val ChoiceAlert: String = "src/main/fxml/choice_alert.fxml"
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

    var tempPath: String = "temp/"

}
