package app

object FXMLPaths {
    val MainInterface: String = "src/main/fxml/main_border_pane.fxml"
    val UnassignedEvent: String = "src/main/fxml/unassigned_event_box.fxml"
    val CoursePanel: String = "src/main/fxml/course_grid.fxml"
    val CourseForm: String = "src/main/fxml/course_form.fxml"
}

object AppSettings {

    final val applicationTitle = "ScaDULER v0.1"
    final val TimeSlotDuration = 30

    var language: Language = DefaultLanguage

    var timeSeparatorSymbol = ":"
    var dayStart: Int = 8
    var dayEnd: Int = 19

    object eventFormSettings {
        var width = 640
        var height = 450

        var fieldSpacing = 4

        var nameFieldWidth = width/2
        var shortNameFieldWidth = width/4

    }
}
