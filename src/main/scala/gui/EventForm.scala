//package gui

//import data.EventData

import javafx.event.ActionEvent
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
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
            width = AppSettings.eventFormWidth
            height = AppSettings.eventFormHeight
            scene = new Scene {
                fill = Color.WhiteSmoke

                val eventNameField = new TextField{
                    layoutX = startX
                    layoutY = startY
                    promptText = AppSettings.Language.getItem("eventForm_eventName")
                    prefWidth = AppSettings.eventFormTextFieldWidth
                    prefHeight = AppSettings.eventFormTextFieldHeight
                    focusTraversable = false
                }

                val eventShortNameField = new TextField{
                    layoutX = eventNameField.layoutX.apply
                    layoutY = eventNameField.layoutY.apply + eventNameField.prefHeight.apply + AppSettings.eventFormFieldStep
                    promptText = AppSettings.Language.getItem("eventForm_eventShortName")
                    prefHeight = AppSettings.eventFormTextFieldHeight
                    focusTraversable = false
                }

                val eventDescriptionField = new TextArea{
                    layoutX = eventNameField.layoutX.apply
                    layoutY = eventShortNameField.layoutY.apply + eventShortNameField.prefHeight.apply + AppSettings.eventFormFieldStep
                    promptText = AppSettings.Language.getItem("eventForm_eventDescription")
                    //prefHeight = AppSettings.eventFormTextFieldHeight
                    wrapText = true
                    focusTraversable = false
                }

                val wrapDescription = new CheckBox {
                    layoutX = eventDescriptionField.layoutX.apply + eventDescriptionField.getWidth/2
                    layoutY = eventDescriptionField.layoutY.apply + 10 + eventDescriptionField.getHeight
                    focusTraversable = false

                    onAction = (e: ActionEvent) => eventDescriptionField.setWrapText(selected.apply)
                }

                val column1 = new ColumnConstraints{
                    percentWidth = 29
                }

                val column2 = new ColumnConstraints{
                    percentWidth = 69
                }

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
