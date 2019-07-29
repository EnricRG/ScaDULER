package app

object FXMLPaths {
    val MainInterface: String = "src/main/fxml/main_border_pane.fxml"
    val UnassignedEvent: String = "src/main/fxml/unassigned_event_box.fxml"
    val Course: String = "src/main/fxml/course_grid.fxml"
}

object AppSettings {

    val applicationTitle = "ScaDULER v0.1"
    val TimeSlotDuration = 30

    var Language: Language = DefaultLanguage

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
