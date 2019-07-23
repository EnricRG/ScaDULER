package app

object AppSettings {

    var Language: Language = DefaultLanguage

    var timeSeparatorSymbol = ":"

    object eventFormSettings {
        var width = 640
        var height = 450

        var fieldSpacing = 4

        var nameFieldWidth = width/2
        var shortNameFieldWidth = width/4

    }
}
