package control.mcf

import java.net.URL
import java.util.ResourceBundle

import app.AppSettings
import control.StageController
import file.imprt.ImportJob
import javafx.fxml.FXML
import javafx.scene.control.{Button, Label}
import misc.Selection

class FinishImportPromptController(importJob: ImportJob)
    extends StageController {

    private var selection: Option[Selection] = None

    @FXML var entryLine: Label = _

    @FXML var subjectsLine: Label = _
    @FXML var coursesLine: Label = _
    @FXML var eventsLine: Label = _
    @FXML var resourcesLine: Label = _

    @FXML var completionExplanation: Label = _
    @FXML var questionToUser: Label = _

    @FXML var modifyButton: Button = _
    @FXML var finishButton: Button = _
    @FXML var cancelButton: Button = _

    def initialize(location: URL, resources: ResourceBundle): Unit = {
        initializeContentLanguage()
        bindActions()
    }

    def initializeContentLanguage(): Unit = {
        entryLine.setText(AppSettings.language.getItemOrElse(
            "finishImport_entryLine",
            "This file contains the definition of:"
        ))

        subjectsLine.setText(
            importJob.subjects.length + " " +
            AppSettings.language.getItemOrElse(
                "finishImport_subjects",
                "subjects"
            )
        )

        coursesLine.setText(
            importJob.courses.length + " " +
            AppSettings.language.getItemOrElse(
                "finishImport_courses",
                "courses"
            )
        )

        eventsLine.setText(
            importJob.events.length + " " +
            AppSettings.language.getItemOrElse(
                "finishImport_events",
                "events"
            )
        )

        resourcesLine.setText(
            importJob.resources.length + " " +
            AppSettings.language.getItemOrElse(
                "finishImport_resources",
                "resources"
            )
        )

        completionExplanation.setText(AppSettings.language.getItemOrElse(
            "finishImport_completionExplanation",
            "But some types of events have no association with any resource, nor incompatibilities between them."
        ))

        questionToUser.setText(AppSettings.language.getItemOrElse(
            "finishImport_question",
            "Would you like to enter this settings manually, leave it as it is or cancel the import job?"
        ))

        modifyButton.setText(AppSettings.language.getItemOrElse(
            "finishImport_modifyButton",
            "Modify manually"
        ))

        finishButton.setText(AppSettings.language.getItemOrElse(
            "finishImport_finishButton",
            "Finish Import"
        ))

        cancelButton.setText(AppSettings.language.getItemOrElse(
            "finishImport_cancelButton",
            "Cancel Import"
        ))
    }

    def bindActions(): Unit = {
        modifyButton.setOnAction(_ => {
            selection = Some(Selection.Modify)
            close()
        })

        finishButton.setOnAction(_ => {
            selection = Some(Selection.Finish)
            close()
        })

        cancelButton.setOnAction(_ => {
            selection = Some(Selection.Cancel)
            close()
        })
    }

    def getSelection: Option[Selection] = selection

}
