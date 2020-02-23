package app


class Language(val items: Map[String,String]) {

    final val ItemNotFound = "???"

    def this(asset: List[(String, String)]) = this(asset.toMap)
    def this() = this(List())

    def getItem(key: String): String = items.getOrElse(key, ItemNotFound)
}

object DefaultLanguage extends Language {
    override val items = Map(

        "noCourse" -> "No Course",
        "firstQuarter" -> "First Quarter",
        "secondQuarter" -> "Second Quarter",

        "theoryEvent" -> "Theory",
        "labEvent" -> "Laboratory",
        "computerEvent" -> "Computer",
        "problemEvent" -> "Problem",
        "specialEvent" -> "Special",
        "theoryEventShort" -> "T.",
        "labEventShort" -> "Lab.",
        "computerEventShort" -> "PC",
        "problemEventShort" -> "Prob.",
        "specialEventShort" -> "Spec.",

        "monday" -> "Monday",
        "tuesday" -> "Tuesday",
        "wednesday" -> "Wednesday",
        "thursday" -> "Thursday",
        "friday" -> "Friday",

        "manage" -> "Manage",

        //this is obsolete
        "labRoom" -> "Lab Room",
        "classRoom" -> "Class Room",
        "pcRoom" -> "PC Room",
        //////////////////////

        "aWeek" -> "A Week",
        "bWeek" -> "B Week",
        "everyWeek" -> "Every Week",
        "shortAWeek" -> "A",
        "shortBWeek" -> "B",
        "shortEveryWeek" -> "W",

        "fileMenu" -> "File",
        "fileMenu_open" -> "Open",
        "fileMenu_save" -> "Save",
        "fileMenu_saveAs" -> "Save As...",
        "fileMenu_close" -> "Close",

        "editMenu" -> "Edit",

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
        "manageButtons_resources" -> "Resources",
        "manageButtons_subjects" -> "Subjects",
        "manageButtons_events" -> "Events",
        "manageButtons_unfinishedEvents" -> "Unfinished\nEvents",

        "viewButtons_title" -> "View...",
        "viewButtons_eventList" -> "Events\nList",
        "viewButtons_unfinishedEventsList" -> "Unfinished\nEvents List",

        "runButtons_title" -> "Run...",
        "runButtons_solve" -> "Solve",
        "runButtons_optimize" -> "Optimize",
        "runButtons_timeout" -> "Timeout",
        "runButtons_timeoutUnit" -> "seconds",
        "runButtons_timeoutTooltip" -> "Seconds until the solver stops searching valid assignments",
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
        "manageResources_availabilityColumn" -> "Availability",
        "manageResources_availableQuantityColumn" -> "Available\nQuantity",
        "manageResources_addButton" -> "Add Resource",
        "manageResources_modifyButton" -> "Modify Resource",
        "manageResources_deleteButton" -> "Delete Resource",
        "manageResources_quantityField" -> "quantity",
        "manageResources_subButton" -> "-1",
        "manageResources_sumButton" -> "+1",
        "resourceTable_placeholder" -> "No resources",
        "manageResources_availabilityPrompt" -> "Manage Availability",

        "manageCourseResources_windowTitle" -> "Manage Course Resources",
        "manageCourseResources_firstQuarter" -> "First Quarter",
        "manageCourseResources_secondQuarter" -> "Second Quarter",
        "manageCourseResources_manageGlobalResources" -> "Manage Global Resources",

        "subjectForm_windowTitle" -> "Create new Subject",
        "subjectForm_subjectNameTag" -> "Subject Name:",
        "subjectForm_subjectNameField" -> "Enter full Subject name",
        "subjectForm_subjectShortNameTag" -> "Subject Short Name:",
        "subjectForm_subjectShortNameField" -> "Subject name abbreviation",
        "subjectForm_subjectDescriptionTag" -> "Subject Description (optional):",
        "subjectForm_subjectDescriptionField" -> "Write a description about the contents of this subject.",
        "subjectForm_subjectColorTag" -> "Subject Color:",
        "subjectForm_subjectColorExplanation" -> "This color will be used to draw a thin frame around the events of the subject",
        "subjectForm_subjectCourseTag" -> "Course:",
        "subjectForm_generateEventsTag" -> "Generate Subject Events (optional):",
        "subjectForm_eventType" -> "Event type",
        "subjectForm_eventWeek" -> "Event Week",
        "subjectForm_eventDuration" -> "Event Duration",
        "subjectForm_rangeTag" -> "Range:",
        "subjectForm_rangeLowerBound" -> "Start",
        "subjectForm_rangeUpperBound" -> "End",
        "subjectForm_generationExampleTag" -> "Generation example: ",
        "subjectForm_selectResourceTag" -> "Select the resource that these generated events will need:",
        "subjectForm_resourceSearchBar" -> "filter resources by name",
        "subjectForm_resourcePlaceholder" -> "No resources",
        "subjectForm_generateEventsButton" -> "Generate Events",
        "subjectForm_manageEventTypeIncompatibilitiesButton" -> "Manage Event Type Incompatibilities...",
        "subjectForm_evenTablePlaceHolder" -> "No Events created yet",
        "subjectForm_eventTableNameColumn" -> "Name",
        "subjectForm_eventTableResourceColumn" -> "Resource",
        "subjectForm_deleteSelectedEventsButton" -> "Delete Selected Events",
        "subjectForm_deleteAllEventsButton" -> "Delete All Events",
        "subjectForm_createSubjectButton" -> "Create Subject",

        "subjectEventIncompatibilityForm_informationTag" -> "Choose which incompatibilities you want: ",

        "eventForm_windowTitle" -> "New Event",
        "eventForm_eventName" -> "Event name:",
        "eventForm_eventNameHelp" -> "Full Event name",
        "eventForm_eventShortName" -> "Event short name:",
        "eventForm_eventShortNameHelp" -> "Event name abbreviation",
        "eventForm_eventDescription" -> "Event description (Optional):",
        "eventForm_eventDescriptionHelp" -> "Detailed Event description",
        "eventForm_eventSubjectTag" -> "Subject (Optional):",
        "eventForm_eventDurationTag" -> "Duration:",
        "eventForm_eventTypeTag" -> "Event type:",
        "eventForm_eventWeekTag" -> "Event week:",
        "eventForm_eventResourceTag" -> "Needed Resource (Optional):",
        "eventForm_confirmationButton" -> "Create Event",
        "eventForm_manageIncompatibilities" -> "Manage Incompatibilities",

        "manageIncompat_assignedIncompat" -> "Assigned Incompatibilities",
        "manageIncompat_incompatTablePlaceholder" -> "No incompatibilities",
        "manageIncompat_nameColumnHeader" -> "Name",
        "manageIncompat_selectAllIncompatibilities" -> "Select All Incompatibilities",
        "manageIncompat_allEventsHeader" -> "All Events",
        "manageIncompat_searchEvent" -> "Enter event name",
        "manageIncompat_selectAllEvents" -> "Select All Events",
        "eventListPlaceholder" -> "No events",

        "courseManager_windowTitle" -> "Manage Courses",
        "courseManager_nameColumnHeader" -> "Name",
        "courseManager_descriptionColumnHeader" -> "Description",
        "courseManager_q1resourcesColumnHeader" -> "Q1 Resources",
        "courseManager_q2resourcesColumnHeader" -> "Q2 Resources",
        "courseManager_addCourseButton" -> "Add Course",
        "courseManager_editCourseButton" -> "Edit Course",
        "courseManager_removeCourseButton" -> "Remove Course",
        "courseTable_placeholder" -> "No courses",
        "courseTable_totalResourcesTypesWord" -> "Types: ",
        "courseTable_totalResourcesWord" -> "Total: ",

        "subjectManager_windowTitle" -> "Manage Subjects",
        "subjectManager_nameColumnHeader" -> "Name",
        "subjectManager_shortNameColumnHeader" -> "Short Name",
        "subjectManager_descriptionColumnHeader" -> "Description",
        "subjectManager_eventCountColumnHeader" -> "Events",
        "subjectManager_addSubjectButton" -> "Add Subject",
        "subjectManager_editSubjectButton" -> "Edit Subject",
        "subjectManager_removeSubjectButton" -> "Remove Subject",
        "subjectTable_placeholder" -> "No subjects",

        "eventManager_windowTitle" -> "Manage Events",
        "eventManager_nameColumnHeader" -> "Name",
        "eventManager_shortNameColumnHeader" -> "Short Name",
        "subjectManager_subjectColumnHeader" -> "Subject",
        "subjectManager_resourceColumnHeader" -> "Resource",
        "subjectManager_weekColumnHeader" -> "Week",
        "subjectManager_durationColumnHeader" -> "Duration",
        "subjectManager_incompatibilitiesColumnHeader" -> "Incompatibilities",
        "eventManager_addEventButton" -> "Add Event",
        "eventManager_editEventButton" -> "Edit Event",
        "eventManager_removeEventButton" -> "Remove Event",

        "denyButton" -> "Reject",
        "acceptButton" -> "Accept",

        "solverError_WindowTitle" -> "Solver Error",
        "solverError_solverError" -> "Unknown error when running process: Solver",

        "timeout_windowTitle" -> "Timeout expired",
        "timeout_timeoutMessage" -> "The solver did not find any solution for your schedule in the specified amount of time",

        "solver_noSolutionWindowTitle" -> "No solution",
        "solver_noSolutionText" -> "A solution for your particular schedule does not exist. Try checking your resource constrains or moving some assignments.",
        "solver_solutionFoundWindowTitle" -> "Solution Found",
        "solver_solutionFoundText" -> "A solution was found! A valid assignation for all remaining unassigned events exists. Do you want to apply it?",

        "unknownFileFormat_windowTitle" -> "Wrong File Format",
        "unknownFileFormat_explanation" -> "Unknown file format. Maybe this file wasn't generated by this application or it was generated by an old version.",

        "warning" -> "Warning",
        "warning_courseNameCannotBeEmpty" -> "Course name cannot be empty.",
        "warning_courseQuarterCannotBeEmpty" -> "Course quarter cannot be empty.",
        "warning_courseResourcesCannotBeEmpty" -> "Course resources cannot be empty.",
        "warning_firstQuarterResourcesCannotBeEmpty" -> "First quarter has no resources.",
        "warning_secondQuarterResourcesCannotBeEmpty" -> "Second quarter has no resources.",
        "warning_resourceNameCannotBeEmpty" -> "Resource name cannot be empty.",
        "warning_resourceQuantityNaN" -> "The quantity is not a number.",
        "warning_resourceQuantityMin" -> " is lower than the minimum allowed quantity",
        "warning_resourcesNotSelected" -> "No resource has been selected.",
        "warning_eventTypeNotSelected" -> "No event type has been selected.",
        "warning_weekNotSelected" -> "No event week has been selected.",
        "warning_durationNotSelected" -> "No duration has been selected.",
        "warning_courseAlreadyExists" -> "A Course with this name already exists.",
        "warning_subjectNameCannotBeEmpty" -> "Subject Name cannot be empty.",
        "warning_subjectShortNameCannotBeEmpty" -> "Subject Short Name cannot be empty.",
        "warning_descendingRange" -> "Range start has to be lower or equal to range end.",
        "warning_incompatibleEvents" -> "the event that you're trying to assign (%s) is incompatible with this event: %s.",
        "warning_resourceUnavailable" -> "the resource %s is not available for as long as it requires.",
        "warning_resourceNeverUnavailable" -> "the resource %s is not available in any of the intervals.",
        "warning_resourceWillBeUnavailable" -> "the resource %s will be unavailable in %s minutes from the start of the event.",
        "warning_borderLimit" -> "this event cannot be placed here because its ending time would exceed max hour allowed.",
        "warning_weekMatchError" -> "cannot assign event to this week because it's not its selected week.",
        "warning_eventNameCannotBeEmpty" -> "event name cannot be empty.",
        "warning_durationCannotBeEmpty" -> "event duration cannot be empty.",
        "warning_eventTypeCannotBeEmpty" -> "event type cannot be empty.",
        "warning_eventWeekCannotBeEmpty" -> "week cannot be empty.",
    )
}
