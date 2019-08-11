package app

object FXMLPaths {
    val MainInterface: String = "src/main/fxml/main_border_pane.fxml"
    val UnassignedEvent: String = "src/main/fxml/unassigned_event_box.fxml"
    val CoursePanel: String = "src/main/fxml/course_grid.fxml"

    val CourseForm: String = "src/main/fxml/course_form.fxml"
    val CourseResourceManagerForm: String = "src/main/fxml/course_resource_manager.fxml"
    val SubjectForm: String = "src/main/fxml/subject_form.fxml"

    val ManageResourcesPanel: String = "src/main/fxml/resource_manager.fxml"
    val ManageCoursesPanel: String = "src/main/fxml/course_manager.fxml"
    val ManageSubjectsPanel: String = "src/main/fxml/subject_manager.fxml"

    val GenericSchedule: String = "src/main/fxml/schedule_view.fxml"
}

object AppSettings {

    final val applicationTitle: String = "ScaDULER v0.1"
    final val TimeSlotDuration: Int = 30

    var language: Language = DefaultLanguage

    var timeSeparatorSymbol: String = ":"
    var dayStart: Int = 8
    var dayEnd: Int = 19

    var minQuantityPerResource: Int = 1

    object eventFormSettings {
        var width: Int = 640
        var height: Int = 450

        var fieldSpacing: Int = 4

        var nameFieldWidth: Int = width/2
        var shortNameFieldWidth: Int = width/4

    }
}
