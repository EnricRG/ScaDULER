package app


class Language(val items: Map[String,String]) {

    final val ItemNotFound = "???"

    def this(asset: List[(String, String)]) = this(asset.toMap)
    def this() = this(List())

    def getItem(key: String): String = items.getOrElse(key, ItemNotFound)
}

object DefaultLanguage extends Language {
    override val items = Map(

        "firstQuarter" -> "First Quarter",
        "secondQuarter" -> "Second Quarter",

        "labRoom" -> "Lab Room",
        "classRoom" -> "Class Room",
        "pcRoom" -> "PC Room",

        "aWeek" -> "A Week",
        "bWeek" -> "B Week",
        "everyWeek" -> "Every Week",
        "shortAWeek" -> "A",
        "shortBWeek" -> "B",
        "shortEveryWeek" -> "W",

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
        "manageButtons_courseResources" -> "Course Resources",
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

        "optional_tag" -> "(Optional)",

        "rightPane_eventSearch" -> "Enter Event name",

        "form_wrapDescription" -> "Wrap text on corners",

        "course" -> "Course",
        "course_emptyDescription" -> "This course has no description",

        "courseForm_windowTitle" -> "Create new Course",
        "courseForm_courseNameTagText" -> "Course name:",
        "courseForm_courseNameFieldText" -> "Full Course name",
        "courseForm_courseQuarterTagText" -> "Quarter",
        "courseForm_courseDescriptionTagText" -> "Course description",
        "courseForm_courseDescriptionFieldText" -> "Enter a course description",
        "courseForm_manageCourseResourcesButtonText" -> "Manage Course Resources...",
        "courseForm_manageCourseResourcesInfo" -> "Create the resources available for this course",
        "courseForm_createCourseButtonText" -> "Create Course",
        "courseForm_modifyCourseButtonText" -> "Modify Course",
        "courseForm_manageCourseResources" -> "Manage Course Resources...",

        "manageResources_windowTitle" -> "Manage Resources",
        "manageResources_searchResourceField" -> "search resources",
        "manageResources_nameColumn" -> "Name",
        "manageResources_quantityColumn" -> "Quantity",
        "manageResources_availableQuantityColumn" -> "Available\nQuantity",
        "manageResources_addButton" -> "Add Resource",
        "manageResources_modifyButton" -> "Modify Resource",
        "manageResources_deleteButton" -> "Delete Resource",
        "manageResources_quantityField" -> "quantity",
        "manageResources_subButton" -> "-1",
        "manageResources_sumButton" -> "+1",
        "resourceTable_placeholder" -> "No resources",

        "manageCourseResources_windowTitle" -> "Manage Course Resources",
        "manageCourseResources_firstQuarter" -> "First Quarter",
        "manageCourseResources_secondQuarter" -> "Second Quarter",
        "manageCourseResources_manageGlobalResources" -> "Manage Global Resources",

        "eventForm_windowTitle" -> "New Event",
        "eventForm_eventName" -> "Event name",
        "eventForm_eventNameHelp" -> "Full Event name",
        "eventForm_eventShortName" -> "Event short name",
        "eventForm_eventShortNameHelp" -> "Event name abbreviation",
        "eventForm_eventDescription" -> "Event description",
        "eventForm_eventDescriptionHelp" -> "Detailed Event description",
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
        "warning_courseNameCannotBeEmpty" -> "Course name cannot be empty.",
        "warning_courseQuarterCannotBeEmpty" -> "Course quarter cannot be empty.",
        "warning_firstQuarterResourcesCannotBeEmpty" -> "First quarter has no resources.",
        "warning_secondQuarterResourcesCannotBeEmpty" -> "Second quarter has no resources.",
        "warning_resourceNameCannotBeEmpty" -> "Resource name cannot be empty.",
        "warning_resourceQuantityNaN" -> "The quantity is not a number.",
        "warning_resourceQuantityMin" -> " is lower than the minimum allowed quantity",
        "warning_resourcesNotSelected" -> "No resource has been selected.",
        "warning_courseAlreadyExists" -> "A Course with this name already exists.",
    )
}
