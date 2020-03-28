package app

object FXMLPaths {
    val MainInterface: String = "src/main/fxml/app/main_border_pane.fxml"
    val UnassignedEvent: String = "src/main/fxml/app/unassigned_event_box.fxml"
    val AssignedEvent: String = "src/main/fxml/app/assigned_event_box.fxml"

    val CourseForm: String = "src/main/fxml/form/course_form.fxml"
    val SubjectForm: String = "src/main/fxml/form/subject_form.fxml"
    val SubjectIncompatibilityForm = "src/main/fxml/form/subject_event_incompatibility_manager.fxml"
    val EventForm: String = "src/main/fxml/form/event_form.fxml"
    val EventIncompatibilityFrom: String = "src/main/fxml/form/event_incompatibility_manager.fxml"

    val EntityManagerPanel: String = "src/main/fxml/manage/entity_manager.fxml"
    val ManageResourcesPanel: String = "src/main/fxml/manage/resource_manager.fxml"
    val ResourceAvailabilityManager: String = "src/main/fxml/manage/resource_availability_manager.fxml"

    val GenericSchedule: String = "src/main/fxml/schedule/schedule_view.fxml"
    val DualWeekGenericSchedule: String = "src/main/fxml/schedule/dual_week_schedule_view.fxml"
    val CourseSchedule: String = "src/main/fxml/schedule/course_schedule_view.fxml"

    val BasicAlert: String = "src/main/fxml/alert/basic_alert.fxml"
    val ChoiceAlert: String = "src/main/fxml/alert/choice_alert.fxml"

    val MCFErrorViewer: String = "src/main/fxml/import/mcf/mcf_import_error_viewer.fxml"
    val MCFFinishImportPrompt: String = "src/main/fxml/import/mcf/mcf_finish_import_prompt.fxml"
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

    var maxEventDuration: Int = 5
    var eventViewColumnPercentage: Double = 0.9

    var minCapacityPerResource: Int = 1

    var defaultTimeout: Double = 10 //seconds

    var tempPath: String = "temp/"

    var softViabilityCheck: Boolean = true

}
