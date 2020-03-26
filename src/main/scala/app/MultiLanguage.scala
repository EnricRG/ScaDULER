package app

trait MultiLanguage {
    def language: Language
    def previousLanguage: Option[Language]
    def changeLanguage(language: Language): Unit
}
