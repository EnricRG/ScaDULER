package control.misc

import java.net.URL
import java.util.ResourceBundle

import control.{StageController, StageSettings}
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.{Label, ListView}
import javafx.stage.Stage

import scala.collection.JavaConverters

class NameListPrompt(names: Iterable[String], placeholder: String, settings: StageSettings) extends StageController {

  private final def PrefListHeight: Double = 300

  setupStage() //Initializing without ViewFactory

  override def initialize(location: URL, resources: ResourceBundle): Unit = { }

  private def setupStage(): Unit = {
    val stage = prepareStage()
    val listView = prepareListView()

    stage.setScene(new Scene(listView))

    setStage(stage)
  }

  private def prepareStage(): Stage = {
    val stage = new Stage

    stage.setTitle(settings.title)
    if(settings.owner.nonEmpty) stage.initOwner(settings.owner.get)
    stage.initModality(settings.modality)

    stage
  }

  private def prepareListView(): ListView[String] = {
    val listView = new ListView[String]

    listView.setPadding(new Insets(5))
    listView.setPrefHeight(PrefListHeight)

    listView.setItems(FXCollections.observableArrayList(
      JavaConverters.asJavaCollection(names)).sorted())

    listView.setPlaceholder(new Label(placeholder))

    listView
  }
}
