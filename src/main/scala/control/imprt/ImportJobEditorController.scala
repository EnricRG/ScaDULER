package control.imprt

import java.net.URL
import java.util.ResourceBundle

import app.FXMLPaths
import control.StageController
import factory.ViewFactory
import file.imprt.{ImportJob, MutableImportJob}
import javafx.fxml.FXML
import javafx.scene.control.Tab
import util.Utils

import scala.collection.mutable.ArrayBuffer

class ImportJobEditorController(importJob: ImportJob) extends StageController {

  private val editableImportJob: MutableImportJob = MutableImportJob(
    new ArrayBuffer ++= importJob.subjects,
    new ArrayBuffer ++= importJob.events,
    new ArrayBuffer ++= importJob.resources,
    new ArrayBuffer ++= importJob.courses,
    new ArrayBuffer ++= importJob.errors,
    importJob.finished,
    importJob.importType)

  @FXML var subjectsTab: Tab = _
  @FXML var coursesTab: Tab = _
  @FXML var eventsTab: Tab = _
  @FXML var resourcesTab: Tab = _

  private val subjectsController: ImportSubjectsManagerController = new ImportSubjectsManagerController
  private val coursesController: ImportCoursesManagerController = new ImportCoursesManagerController
  private val eventsController: ImportEventsManagerController = new ImportEventsManagerController
  private val resourcesController: ImportResourcesManagerController = new ImportResourcesManagerController

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initializeControllers()
  }

  private def initializeControllers(): Unit = {
    initializeSubjectsTab()
    initializeCoursesTab()
    initializeEventsTab()
    initializeResourcesTab()
  }

  private def initializeSubjectsTab(): Unit = {
    Utils.loadScene(
      new ViewFactory(FXMLPaths.ImportEntityManagerView),
      subjectsController)

    subjectsTab.setContent(subjectsController.mainBox)
  }

  private def initializeCoursesTab(): Unit = {
    Utils.loadScene(
      new ViewFactory(FXMLPaths.ImportEntityManagerView),
      coursesController)

    subjectsTab.setContent(coursesController.mainBox)
  }

  private def initializeEventsTab(): Unit = {
    Utils.loadScene(
      new ViewFactory(FXMLPaths.ImportEntityManagerView),
      eventsController)

    eventsTab.setContent(eventsController.mainBox)
  }

  private def initializeResourcesTab(): Unit = {
    Utils.loadScene(
      new ViewFactory[ImportCoursesManagerController](FXMLPaths.ImportEntityManagerView),
      resourcesController)

    resourcesTab.setContent(resourcesController.mainBox)
  }

  def getImportJob: ImportJob = editableImportJob.toImportJob

  def waitForImportJob: ImportJob = {
    showAndWait()
    getImportJob
  }
}