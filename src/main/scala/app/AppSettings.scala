package app

object AppSettings {

    val applicationTitle = "ScaDULER v0.1"

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
