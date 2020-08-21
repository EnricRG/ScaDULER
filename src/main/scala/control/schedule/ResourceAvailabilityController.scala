package control.schedule

import java.io.IOException
import java.net.URL

import app.AppSettings
import control.StageController
import control.schedule.cell.{ScheduleCell, ScheduleCellFactory}
import factory.DualWeekScheduleViewFactory
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.{Button, Label, TextField}
import javafx.scene.input.{KeyCode, MouseButton, MouseDragEvent, MouseEvent}
import javafx.scene.layout.VBox
import model.ResourceSchedule
import util.Utils
import java.util
import java.util.ResourceBundle

import javafx.fxml.FXML
import javafx.stage.Stage


object ResourceAvailabilityController {
  private val SELECTED_STATE_STYLE: String = "-fx-background-color: gold;"
  private val SET_STATE_STYLE: String = "-fx-background-color: skyblue;"
}

class ResourceAvailabilityController(availability: ResourceSchedule) extends StageController {

  private class ResourceScheduleCellFactory extends ScheduleCellFactory {
    override def newCell(row: Int, column: Int) = new ResourceAvailabilityCell(row, column)
  }

  private object ResourceAvailabilityCell {
    private val UNSET_STATE = 0
    private val SET_STATE = 1
    private val SELECTED_STATE = 2 //This state is for coloring purposes. Must never be assigned to any cell.
  }

  private class ResourceAvailabilityCell(quantity: Int, row: Int, column: Int) extends ScheduleCell(row, column) {

    private var _week = 0
    private var _interval = 0

    private var _quantity = 0
    private var _state = ResourceAvailabilityCell.UNSET_STATE
    private val _quantityLabel = new Label

    private var _selected = false

    this.setAlignment(Pos.CENTER)
    this.getChildren.add(_quantityLabel)
    setQuantity(quantity, silent = false)

    def this(row: Int, column: Int) = this(0, row, column)

    def week: Int = _week

    def setWeek(week: Int): Unit = { _week = week }

    def interval: Int = _interval

    def setInterval(interval: Int): Unit = { _interval = interval }

    def setState(state: Int, silent: Boolean): Unit = {
      if (!silent) setStateStyle(state)
      _state = state
    }

    def selected: Boolean = _selected

    private def setStateStyle(state: Int): Unit = {
      //remove old style
      val neutralStyle = removeStateFromStyle(this.getStyle, _state)
      //set new style
      val newStyle = addStateToStyle(neutralStyle, state)

      this.setStyle(newStyle)
    }

    private def removeStateFromStyle(style: String, state: Int): String = {
      var newStyle: String = null

      if (state == ResourceAvailabilityCell.SET_STATE)
        newStyle = style.replace(ResourceAvailabilityController.SET_STATE_STYLE, "")
      else if (state == ResourceAvailabilityCell.SELECTED_STATE)
        newStyle = style.replace(ResourceAvailabilityController.SELECTED_STATE_STYLE, "")
      else newStyle = style

      newStyle
    }

    private def addStateToStyle(style: String, state: Int): String = {
      var newStyle: String = null

      if (state == ResourceAvailabilityCell.SET_STATE)
        newStyle = style + ResourceAvailabilityController.SET_STATE_STYLE
      else if (state == ResourceAvailabilityCell.SELECTED_STATE)
        newStyle = style + ResourceAvailabilityController.SELECTED_STATE_STYLE
      else newStyle = style

      newStyle
    }

    def setQuantity(quantity: Int, silent: Boolean): Unit = {
      if (quantity <= 0) {
        setState(ResourceAvailabilityCell.UNSET_STATE, silent)
        _quantityLabel.setVisible(false)
      }
      else {
        _quantityLabel.setText(Integer.toString(quantity))
        setState(ResourceAvailabilityCell.SET_STATE, silent)
        _quantityLabel.setVisible(true)
      }
      _quantity = quantity
    }

    //pre: cell not selected
    def select(): Unit = {
      _selected = true
      setStateStyle(ResourceAvailabilityCell.SELECTED_STATE)
    }

    //pre: cell selected
    def deselect(): Unit = {
      _selected = false
      setStyle(removeStateFromStyle(this.getStyle, ResourceAvailabilityCell.SELECTED_STATE))
      setStateStyle(_state)
    }
  }

  private class CellEventHandler(cell: ResourceAvailabilityCell) extends EventHandler[MouseEvent] {

    override def handle(mouseEvent: MouseEvent): Unit = {
      if (mouseEvent.getEventType == MouseEvent.DRAG_DETECTED) {
        dragging = true
        cell.startFullDrag()
      }
      else if (mouseEvent.getEventType == MouseDragEvent.MOUSE_DRAG_ENTERED) {
        if (dragging)
          modifyCellState(mouseEvent, cell)
      }
      else if (mouseEvent.getEventType == MouseEvent.MOUSE_RELEASED) {
        if (!dragging) //if it was a click
          modifyCellState(mouseEvent, cell)
        else dragging = false
      }

      mouseEvent.consume()
    }

    private def modifyCellState(mouseEvent: MouseEvent, cell: ResourceAvailabilityCell): Unit = {
      if (mouseEvent.getButton == MouseButton.PRIMARY) {
        if (!cell.selected) {
          cell.select()
          selection.add(cell)
        }
      }
      else if (mouseEvent.getButton == MouseButton.SECONDARY) {
        if (cell.selected) {
          cell.deselect()
          selection.remove(cell)
        }
      }
    }
  }

  private val schedule: DualWeekScheduleViewController[WeekScheduleController, WeekScheduleController] = {
    val factory = new ResourceScheduleCellFactory
    new DualWeekScheduleViewController(new WeekScheduleController(factory), new WeekScheduleController(factory))
  }

  @FXML var mainContainer: VBox = _

  @FXML var selectedIntervalsTag: Label = _
  @FXML var selectedIntervalsNumberTag: Label = _

  @FXML var quantityField: TextField = _
  @FXML var setButton: Button = _
  @FXML var plusOneButton: Button = _
  @FXML var zeroButton: Button = _
  @FXML var minusOneButton: Button = _

  @FXML var deselectExplanationTag: Label = _

  private var selection = new util.HashSet[ResourceAvailabilityCell]
  private var dragging = false
  private var typing = false

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    try
      mainContainer.getChildren.add(
        0,
        new DualWeekScheduleViewFactory[
          DualWeekScheduleViewController[WeekScheduleController, WeekScheduleController],
          WeekScheduleController,
          WeekScheduleController](schedule).load())
    catch {
      case ioe: IOException =>
        ioe.printStackTrace()
    }

    initializeContentLanguage()
    initializeCustomBehavior()
  }

  override def setStage(stage: Stage): Unit = {
    super.setStage(stage)

    stage.getScene.setOnKeyReleased(keyEvent => {
      val keyCode: KeyCode = keyEvent.getCode

      if (keyCode == KeyCode.ESCAPE)
        clearSelection()
      else if (!typing && (keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE)) {
        //FIXME if user presses any of this keys when typing on text fields, this code will also be triggered.
        //To prevent this from happening, this feature is disabled until fixed.
        //unsetSelection()
      }
    })
  }

  private def initializeContentLanguage(): Unit = {
    selectedIntervalsTag.setText(AppSettings.language.getItemOrElse(
      "resourceAvailability_selectedIntervalsTag",
      "Selected intervals:"))
    selectedIntervalsNumberTag.setText("0")

    setButton.setText(AppSettings.language.getItemOrElse(
      "resourceAvailability_setButton",
      "Set"))

    plusOneButton.setText("+1")
    zeroButton.setText("0")
    minusOneButton.setText("-1")

    deselectExplanationTag.setText(AppSettings.language.getItemOrElse(
      "resourceAvailability_deselectExplanation",
      "To deselect intervals, you can press right click over them, or press Esc key to deselect them all."))
  }

  private def initializeCustomBehavior(): Unit = {
    plusOneButton.setOnAction(actionEvent => {
      incrementSelectionIn(1)
      actionEvent.consume()
    })

    minusOneButton.setOnAction(actionEvent=> {
      decrementSelectionIn(1)
      actionEvent.consume()
    })

    zeroButton.setOnAction(actionEvent => {
      setSelectionTo(0)
      actionEvent.consume()
    })

    setButton.setOnAction(actionEvent => {
      setSelectionFromQuantityField()
      actionEvent.consume()
    })

    quantityField.setOnKeyReleased(keyEvent => {
      if (keyEvent.getCode == KeyCode.ENTER) setSelectionFromQuantityField()
      else if (keyEvent.getCode == KeyCode.ESCAPE) setButton.requestFocus()
      //keyEvent.consume();
    })

    for (week <- 0 to 1) {
      val c = if (week < 1) schedule.firstWeekController else schedule.secondWeekController
      c.getInnerCells.forEach(cell => {
        val interval: Int = computeInterval(cell)
        configureCell(cell.asInstanceOf[ResourceAvailabilityCell], week, interval)
      })
    }
  }

  //pre: cell not null
  private def configureCell(cell: ResourceAvailabilityCell, week: Int, interval: Int): Unit = {
    cell.addEventHandler(MouseEvent.ANY, new CellEventHandler(cell))
    cell.setWeek(week)
    cell.setInterval(interval)
    cell.setQuantity(availability.get(week, interval), silent = false)
  }

  private def computeInterval(cell: ScheduleCell): Int = {
    val day = cell.getColumn - 1 //subtract first column (hours)
    val dayInterval = cell.getRow - 1 //subtract first row (headers)

    Utils.computeInterval(day, dayInterval)
  }

  private def incrementSelectionIn(amount: Int): Unit = {
    selection.forEach(cell => {
      availability.increment(cell.week, cell.interval, amount)
      cell.setQuantity(availability.get(cell.week, cell.interval), silent = true)
    })
  }

  private def decrementSelectionIn(amount: Int): Unit = {
    incrementSelectionIn(-amount)
  }

  private def setSelectionTo(quantity: Int): Unit = {
    selection.forEach(cell => {
      if (quantity <= 0) availability.unset(cell.week, cell.interval)
      else availability.set(cell.week, cell.interval, quantity)
      cell.setQuantity(quantity, silent = true)
    })
  }

  def unsetSelection(): Unit = { setSelectionTo(0) }

  private def setSelectionFromQuantityField(): Unit = {
    val quantity = getNumberFromField
    if (quantity > 0) setSelectionTo(quantity)
  }

  private def getNumberFromField: Int = {
    try
      quantityField.getText.toInt
    catch {
      case nfe: NumberFormatException =>
        -1
    }
  }

  def clearSelection(): Unit = {
    if (!selection.isEmpty) {
      selection.forEach(_.deselect())
      selection = new util.HashSet[ResourceAvailabilityCell]
    }
  }
}

