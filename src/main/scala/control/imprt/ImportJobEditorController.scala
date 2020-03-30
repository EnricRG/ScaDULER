package control.imprt

import java.net.URL
import java.util.ResourceBundle

import control.StageController
import file.imprt.ImportJob

class ImportJobEditorController(importJob: ImportJob) extends StageController {

    override def initialize(location: URL, resources: ResourceBundle): Unit = {

    }

    def getImportJob: ImportJob = throw new UnsupportedOperationException
}
