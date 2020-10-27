package app

object FXMLPaths {
  val MainInterface: String = "fxml/app/main_border_pane.fxml"
  val UnassignedEvent: String = "fxml/app/unassigned_event_box.fxml"
  val AssignedEvent: String = "fxml/app/assigned_event_box.fxml"
  val ProgressBar: String = "fxml/app/simple_progress_bar.fxml"

  val CourseForm: String = "fxml/form/course_form.fxml"
  val SubjectForm: String = "fxml/form/subject_form.fxml"
  val SubjectEventIncompatibilityForm = "fxml/form/subject_event_incompatibility_manager.fxml"
  val EventForm: String = "fxml/form/event_form.fxml"
  val EventIncompatibilityFrom: String = "fxml/form/event_incompatibility_manager.fxml"
  val ResourceForm: String = "fxml/form/resource_form.fxml"

  val EntityManagerPanel: String = "fxml/manage/entity_manager.fxml"
  val ManageResourcesPanel: String = "fxml/manage/resource_manager.fxml"
  val ResourceAvailabilityManager: String = "fxml/manage/resource_availability_manager.fxml"

  val GenericSchedule: String = "fxml/schedule/schedule_view.fxml"
  val DualWeekGenericSchedule: String = "fxml/schedule/dual_week_schedule_view.fxml"
  val CourseSchedule: String = "fxml/schedule/course_schedule_view.fxml"

  val BasicAlert: String = "fxml/alert/basic_alert.fxml"
  val ChoiceAlert: String = "fxml/alert/choice_alert.fxml"
  val RemoveModePrompt: String = "fxml/alert/remove_mode_prompt.fxml"

  val ModifyImportJob: String = "fxml/import/modify_import_job_main_frame.fxml"
  val ImportEntityManagerView: String = "fxml/import/import_entity_manager.fxml"
  val ImportCourseDetailsView: String = "fxml/import/entity_details/course_details.fxml"
  val ImportSubjectDetailsView: String = "fxml/import/entity_details/subject_details.fxml"
  val ImportEventDetailsView: String = "fxml/import/entity_details/event_details.fxml"
  val ImportResourceDetailsView: String = "fxml/import/entity_details/resource_details.fxml"

  val MCFErrorViewer: String = "fxml/import/mcf/mcf_import_error_viewer.fxml"
  val MCFFinishImportPrompt: String = "fxml/import/mcf/mcf_finish_import_prompt.fxml"
}

object AppSettings {

  final val applicationTitle: String = "ScaDULER v0.2"
  final val TimeSlotDuration: Int = 30
  final val TimeSlotsPerHour: Int = 60/TimeSlotDuration

  var language: Language = DefaultLanguage

  var timeSeparatorSymbol: String = ":"
  var dayStart: Int = 8 //8:00 AM
  var dayEnd: Int = 19 //19:00

  var timeSlotsPerDay:Int = 22 //this should be computed ( (dayEnd - dayStart) * TimeSlotsPerHour )
  var days: Int = 5
  var timeSlots: Int = timeSlotsPerDay*days

  var maxEventDuration: Int = 5
  var eventViewColumnPercentage: Double = 0.9

  var minCapacityPerResource: Int = 1

  var defaultTimeout: Double = 10 //seconds

  var tempPath: String = "temp/"

  var softViabilityCheck: Boolean = true

}
