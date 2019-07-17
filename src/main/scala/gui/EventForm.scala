//package gui

//import data.EventData

import javafx.event.ActionEvent
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Pos.{Center, TopCenter}
import scalafx.scene.layout.{ColumnConstraints, GridPane}
//import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.{CheckBox, TextArea, TextField}
import scalafx.scene.paint.Color


object EventForm {
    def promptForm(n_events: Int): Option[EventData] = {

    val prompt: JFXApp = new JFXApp {

        val startX = 20
        val startY = 10

        stage = new PrimaryStage {
            width = AppSettings.eventFormSettings.width
            height = AppSettings.eventFormSettings.height
            scene = new Scene {
                title = AppSettings.Language.getItem("eventForm_windowTitle")
                fill = Color.WhiteSmoke

                val eventNameField = new TextField{
                    promptText = AppSettings.Language.getItem("eventForm_eventName")
                    prefColumnCount = AppSettings.eventFormSettings.nameColumnWidth
                    focusTraversable = false
                }

                val eventShortNameField = new TextField{
                    promptText = AppSettings.Language.getItem("eventForm_eventShortName")
                    prefColumnCount = AppSettings.eventFormSettings.shortNameColumnWidth
                    focusTraversable = false
                }

                val eventDescriptionField = new TextArea{
                    promptText = AppSettings.Language.getItem("eventForm_eventDescription")
                    wrapText = false
                    focusTraversable = false
                }

                val wrapDescription = new CheckBox {
                    text = AppSettings.Language.getItem("eventForm_wrapDescription")
                    alignment = Center
                    focusTraversable = false
                    onAction = (e: ActionEvent) => eventDescriptionField.setWrapText(selected.apply)
                }

                val column1 = new ColumnConstraints{ percentWidth = 29; fillWidth = false}
                val column2 = new ColumnConstraints{ percentWidth = 69; fillWidth = false}

                val grid = new GridPane{
                    hgap = 10
                    vgap = 10
                }

                grid.columnConstraints = List(column1, column2)

                grid.add(wrapDescription, 0, 3)

                grid.add(eventNameField, 1, 1)
                grid.add(eventShortNameField, 1, 2)
                grid.add(eventDescriptionField, 1, 3)

                root = grid
                //content = List(eventName, eventShortName, eventDescription, wrapDescription)
            }
        }
    }

    prompt.main(Array())

    Some(new EventData(n_events))
    }
}
