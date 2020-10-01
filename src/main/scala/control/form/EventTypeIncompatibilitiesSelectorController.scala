package control.form

import java.net.URL
import java.util.ResourceBundle

import control.StageController
import javafx.fxml.FXML
import javafx.scene.control.{CheckBox, Label}
import javafx.scene.layout.VBox
import misc.{EventTypeIncompatibilities, EventTypeIncompatibility}

import scala.collection.mutable

class EventTypeIncompatibilitiesSelectorController(eventTypeIncompatibilities: Iterable[EventTypeIncompatibility])
  extends StageController {

  @FXML var informationTag: Label = _
  @FXML var checkBoxContainer: VBox = _

  private val checkBoxIncompatibilityMap: mutable.Map[CheckBox, EventTypeIncompatibility] =
    new mutable.HashMap

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    setupCheckBoxes()
  }

  private def setupCheckBoxes(): Unit = {
    //using list ensures an ordered layout.
    EventTypeIncompatibilities.list.foreach(eti => {
      val c = new CheckBox()

      bindCheckBoxToIncompatibility(c, eti)
      setupCheckBox(c, eti)

      checkBoxContainer.getChildren.add(c)
    })
  }

  private def setupCheckBox(c: CheckBox, eti: EventTypeIncompatibility): Unit = {
    c.setText(eti.toString)
    if (eventTypeIncompatibilities.toSet.contains(eti)) c.setSelected(true)
  }

  private def bindCheckBoxToIncompatibility(c: CheckBox, eti: EventTypeIncompatibility): Unit = {
    checkBoxIncompatibilityMap.put(c, eti)
  }

  def waitFormResult: Option[(Iterable[EventTypeIncompatibility], Iterable[EventTypeIncompatibility])] = {
    showAndWait()
    Some(computeFinalIncompatibilities)
  }

  private def computeFinalIncompatibilities: (Iterable[EventTypeIncompatibility], Iterable[EventTypeIncompatibility]) = {
    val selectedIncompatibilities = checkBoxIncompatibilityMap.filter(_._1.isSelected).values.toSet

    val eventTypeIncompatibilitiesSet = eventTypeIncompatibilities.toSet

    val newSelectedIncompatibilities = selectedIncompatibilities -- eventTypeIncompatibilitiesSet
    val removedIncompatibilities = eventTypeIncompatibilitiesSet -- selectedIncompatibilities

    (newSelectedIncompatibilities, removedIncompatibilities)
  }

}
