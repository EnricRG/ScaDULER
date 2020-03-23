package control.select

import java.net.URL
import java.util.ResourceBundle

import app.AppSettings
import control.BinaryChoiceAlertController

class ResourceImportChoiceController(text: String)
    extends BinaryChoiceAlertController(text){

    override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
        super.initialize(url, resourceBundle)

        acceptButton.setText(AppSettings.language.getItemOrElse("import_updateRepeated", "Update repeated"))
        rejectButton.setText(AppSettings.language.getItemOrElse("import_discardRepeated", "Discard repeated"))
    }
}
