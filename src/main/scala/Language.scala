
class Language(val items: Map[String,String]) {

    final val ItemNotFound = "???"

    def this(asset: List[(String, String)]) = this(asset.toMap)
    def this() = this(List())

    def getItem(key: String): String = items.getOrElse(key, ItemNotFound)
}

object DefaultLanguage extends Language {
    override val items = Map(
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
