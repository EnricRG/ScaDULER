//package gui

//import data.EventData

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{TextArea, TextField}
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

          val eventName = new TextField{
            layoutX = startX
            layoutY = startY
            promptText = AppSettings.Language.getItem("eventForm_eventName")
            prefWidth = AppSettings.eventFormTextFieldWidth
            prefHeight = AppSettings.eventFormTextFieldHeight
            focusTraversable = false
          }

          val eventShortName = new TextField{
            layoutX = eventName.layoutX.toDouble
            layoutY = eventName.layoutY.toDouble + eventName.prefHeight.toDouble + AppSettings.eventFormFieldStep
            promptText = AppSettings.Language.getItem("eventForm_eventShortName")
            prefHeight = AppSettings.eventFormTextFieldHeight
            focusTraversable = false
          }

          val eventDescription = new TextArea{
            layoutX = eventName.layoutX.toDouble
            layoutY = eventShortName.layoutY.toDouble + eventShortName.prefHeight.toDouble + AppSettings.eventFormFieldStep
            promptText = AppSettings.Language.getItem("eventForm_eventDescription")
            //prefHeight = AppSettings.eventFormTextFieldHeight
            focusTraversable = false
          }

          content = List(eventName, eventShortName, eventDescription)
        }
      }
    }

    prompt.main(Array())

    Some(new EventData(n_events))
  }
}
