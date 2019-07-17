
class Language(val items: Map[String,String]) {

    final val ItemNotFound = "???"

    def this(asset: List[(String, String)]) = this(asset.toMap)
    def this() = this(List())

    def getItem(key: String): String = items.getOrElse(key, ItemNotFound)
}

object DefaultLanguage extends Language {
    override val items = Map(
        "eventForm_windowTitle" -> "New Event",
        "eventForm_eventName" -> "Event name",
        "eventForm_eventShortName" -> "Event name abbreviation",
        "eventForm_eventDescription" -> "Event description",
        "eventForm_wrapDescription" -> "Wrap text on corners",
    )
}
