package app


class Language(val items: Map[String,String]) {

    final val ItemNotFound = "???"

    def this(asset: List[(String, String)]) = this(asset.toMap)
    def this() = this(List())

    def getItem(key: String): String = items.getOrElse(key, ItemNotFound)
}

object DefaultLanguage extends Language {
    override val items = Map(

        "fileMenu" -> "File",
        "fileMenu_save" -> "Save",
        "fileMenu_saveAs" -> "Save As...",
        "fileMenu_close" -> "Close",

        "editMenu" -> "File",

        "settingsMenu" -> "Settings",
        "settingsMenu_appSettings" -> "Application Settings",

        "helpMenu" -> "Help",
        "helpMenu_about" -> "About",

        "addButtons_title" -> "Add...",
        "addButtons_course" -> "Course",
        "addButtons_subject" -> "Subject",
        "addButtons_event" -> "Event",

        "manageButtons_title" -> "Manage...",
        "manageButtons_courses" -> "Courses",
        "manageButtons_subjects" -> "Subjects",
        "manageButtons_events" -> "Events",
        "manageButtons_unfinishedEvents" -> "Unfinished\nEvents",

        "viewButtons_title" -> "View...",
        "viewButtons_eventList" -> "Events\nList",
        "viewButtons_unfinishedEventsList" -> "Unfinished\nEvents List",

        "runButtons_title" -> "Run...",
        "runButtons_solve" -> "Solve",
        "runButtons_optimize" -> "Optimize",
        "runButtons_stop" -> "Stop",

        "rightPane_eventSearch" -> "Enter Event name",

        "labRoom" -> "Lab Room",
        "classRoom" -> "Class Room",
        "pcRoom" -> "PC Room",

        "aWeek" -> "A Week",
        "bWeek" -> "B Week",
        "everyWeek" -> "Every Week",
        "shortAWeek" -> "A",
        "shortBWeek" -> "B",
        "shortEveryWeek" -> "W",

        "eventForm_windowTitle" -> "New Event",
        "eventForm_eventName" -> "Event name",
        "eventForm_eventNameHelp" -> "Full Event name",
        "eventForm_eventShortName" -> "Event short name",
        "eventForm_eventShortNameHelp" -> "Event name abbreviation",
        "eventForm_eventDescription" -> "Event description",
        "eventForm_eventDescriptionHelp" -> "Detailed Event description",
        "eventForm_wrapDescription" -> "Wrap text on corners",
        "eventForm_roomType" -> "Room Type",
        "eventForm_startTime" -> "Start Time",
        "eventForm_endTime" -> "End Time",
        "eventForm_week" -> "Week",
        "eventForm_day" -> "day",
        "eventForm_hour" -> "hour",
        "eventForm_minutes" -> "min",
        "eventForm_confirmationButton" -> "Create Event",
        "eventForm_manageIncompatibilities" -> "Manage Incompatibilities...",

        "warning" -> "Warning",
    )
}
