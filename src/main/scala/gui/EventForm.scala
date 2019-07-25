package gui

import app.AppSettings
import control.Warning
import javafx.event.{ActionEvent, EventHandler}
import misc.{Classrooms, Days, Hours, Minutes, Weeks}
import model.EventData
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.geometry.Orientation.Horizontal
import scalafx.geometry.Pos.{BaselineCenter, Center}
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color

class FormWarning(val message: String) extends Warning(message)

object EventForm/*(nEvents: Int)*/ {

    //var event: model.EventData = new model.EventData(nEvents)

    var warning: Option[Warning] = None

    private def manageIncompatibilities = {}

    private def leftSplit = new VBox{

        spacing = AppSettings.eventFormSettings.fieldSpacing
        padding = Insets(2,2,4,2)

        val eventNameTag = new Label { text = AppSettings.Language.getItem("eventForm_eventName") + ":"}
        val eventNameField = new TextField{
            maxWidth = AppSettings.eventFormSettings.nameFieldWidth
            promptText = AppSettings.Language.getItem("eventForm_eventNameHelp")
        }

        val eventShortNameTag = new Label { text = AppSettings.Language.getItem("eventForm_eventShortName") + ":"}
        val eventShortNameField = new TextField{
            maxWidth = AppSettings.eventFormSettings.shortNameFieldWidth
            promptText = AppSettings.Language.getItem("eventForm_eventShortNameHelp")
        }

        val eventDescriptionTag = new Label { text = AppSettings.Language.getItem("eventForm_eventDescription") + ":"}
        val eventDescriptionField = new TextArea{
            promptText = AppSettings.Language.getItem("eventForm_eventDescriptionHelp")
            wrapText = false
        }

        val wrapDescription = new HBox {
            alignment = Center
            children = new CheckBox {
                text = AppSettings.Language.getItem("eventForm_wrapDescription")
                alignment = Center
                fillWidth = true
                onAction = (e: ActionEvent) => eventDescriptionField.setWrapText(selected.apply)
            }
        }

        children = List(eventNameTag, eventNameField, eventShortNameTag, eventShortNameField, eventDescriptionTag, eventDescriptionField, wrapDescription)
    }

    private def rightSplit = new VBox{

        private def timeSelector(daySelectorBehavior: EventHandler[ActionEvent],
                                 hourSelectorBehavior: EventHandler[ActionEvent],
                                 minuteSelectorBehavior: EventHandler[ActionEvent]) = new HBox{

            val daySelector = new ComboBox(Days.dayNumberList){
                promptText = AppSettings.Language.getItem("eventForm_day")
                onAction = daySelectorBehavior
            }

            val dayHourSeparator = new Label{ text = AppSettings.timeSeparatorSymbol }

            val hourSelector = new ComboBox(Hours.hourList){
                this.promptText = AppSettings.Language.getItem("eventForm_hour")
                onAction = hourSelectorBehavior
            }

            val hourMinuteSeparator = new Label{ text = AppSettings.timeSeparatorSymbol }

            val minuteSelector = new ComboBox(Minutes.minuteList){
                this.promptText = AppSettings.Language.getItem("eventForm_minutes")
                onAction = minuteSelectorBehavior
            }

            children = List(daySelector, dayHourSeparator, hourSelector, hourMinuteSeparator, minuteSelector)
        }

        fillWidth = true
        spacing = AppSettings.eventFormSettings.fieldSpacing
        padding = Insets(2,2,4,2)

        val eventPropertiesPane = new VBox {

            spacing = AppSettings.eventFormSettings.fieldSpacing
            padding = Insets(0,0,10,0)

            val roomTypeTag = new Label{ text = AppSettings.Language.getItem("eventForm_roomType") }
            val roomTypeSelector = new HBox {
                children = new ComboBox(Classrooms.roomTypes)
            }

            val startTimeTag = new Label { text = AppSettings.Language.getItem("eventForm_startTime") }
            val startTimeField = timeSelector(
                (e: ActionEvent) => {},
                (e: ActionEvent) => {},
                (e: ActionEvent) => {},
            )

            val endTimeTag = new Label { text = AppSettings.Language.getItem("eventForm_endTime")}
            val endTimeField = timeSelector(
                (e: ActionEvent) => {},
                (e: ActionEvent) => {},
                (e: ActionEvent) => {},
            )

            val weekTag = new Label { text = AppSettings.Language.getItem("eventForm_week")}
            val weekSelector = new HBox{
                children = new ComboBox(Weeks.weekList)
            }

            children = List(roomTypeTag, roomTypeSelector, startTimeTag, startTimeField, endTimeTag, endTimeField, weekTag, weekSelector)
        }

        val separator = new Separator{
            orientation = Horizontal
        }

        val incompatibilitiesButton = new VBox{
            maxWidth = Double.MaxValue
            maxHeight = Double.MaxValue
            alignment = BaselineCenter

            children = new Button{
                text = AppSettings.Language.getItem("eventForm_manageIncompatibilities")
                alignment = BaselineCenter
                onAction = (e: ActionEvent) => manageIncompatibilities
            }
        }

        children = List(eventPropertiesPane, separator, incompatibilitiesButton)
    }

    private def splitView = {
        new SplitPane {
            orientation = Horizontal
            dividerPositions = 0.65
            items.addAll(leftSplit,rightSplit)
        }
    }

    private def confirmationButton = new HBox {
        children = new Button{
            maxWidth = Double.MaxValue
            hgrow = Always
            text = AppSettings.Language.getItem("eventForm_confirmationButton")
            style = "-fx-font-size: 20px;"
        }
    }

    private def warningPanel = new Label{
        visible = true
        style = "-fx-font-style: italic;"
        text = "Warning: this functionality is not finished yet."
    }

    def promptForm(n_events: Int): Option[EventData] = {

        val prompt: JFXApp = new JFXApp {

            stage = new PrimaryStage {
                width = AppSettings.eventFormSettings.width
                height = AppSettings.eventFormSettings.height
                scene = new Scene{
                    title = AppSettings.Language.getItem("eventForm_windowTitle")
                    fill = Color.WhiteSmoke
                    root = new VBox{
                        fillWidth = true
                        spacing = 4
                        padding = Insets(4,4,4,4)
                        children = List(splitView,confirmationButton,warningPanel)
                    }
                }
            }

        }

        prompt.main(Array())

        Some(new EventData(n_events))
    }

}
