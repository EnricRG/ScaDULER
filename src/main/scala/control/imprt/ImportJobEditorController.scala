package control.imprt

import java.net.URL
import java.util.ResourceBundle

import app.{AppSettings, FXMLPaths}
import control.StageController
import factory.ViewFactory
import file.imprt.{ImportError, ImportJob, ImportType}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.control.Tab
import javafx.stage.{Modality, Stage}
import model.blueprint.{CourseBlueprint, EventBlueprint, ResourceBlueprint, SubjectBlueprint}
import util.Utils

import scala.collection.mutable.ArrayBuffer

case class MutableImportJob(
  var subjects: ArrayBuffer[SubjectBlueprint] = new ArrayBuffer,
  var events: ArrayBuffer[EventBlueprint] = new ArrayBuffer,
  var resources: ArrayBuffer[ResourceBlueprint] = new ArrayBuffer,
  var courses: ArrayBuffer[CourseBlueprint] = new ArrayBuffer,
  var errors: ArrayBuffer[ImportError] = new ArrayBuffer,
  var finished: Boolean,
  var importType: ImportType){

  def toImportJob: ImportJob =
    ImportJob(
      subjects.toList,
      events.toList,
      resources.toList,
      courses.toList,
      errors.toList,
      finished,
      importType)
}

class ImportJobEditorController(importJob: ImportJob) extends StageController {

  private val editableImportJob: MutableImportJob = MutableImportJob(
    new ArrayBuffer ++= importJob.subjects,
    new ArrayBuffer ++= importJob.events,
    new ArrayBuffer ++= importJob.resources,
    new ArrayBuffer ++= importJob.courses,
    new ArrayBuffer ++= importJob.errors,
    importJob.finished,
    importJob.importType)

  @FXML var overviewTab: Tab = _
  @FXML var subjectsTab: Tab = _
  @FXML var coursesTab: Tab = _
  @FXML var eventsTab: Tab = _
  @FXML var resourcesTab: Tab = _

  private val overviewController: ImportOverviewController = new ImportOverviewController
  private val subjectsController: ImportSubjectsManagerController = new ImportSubjectsManagerController
  private val coursesController: ImportCoursesManagerController = new ImportCoursesManagerController
  private val eventsController: ImportEventsManagerController = new ImportEventsManagerController
  private val resourcesController: ImportResourcesManagerController = new ImportResourcesManagerController

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initializeControllers()
  }

  private def initializeControllers(): Unit = {
    initializeOverviewTab()
    initializeSubjectsTab()
    initializeCoursesTab()
    initializeEventsTab()
    initializeResourcesTab()
  }

  private def initializeOverviewTab(): Unit = {
    Utils.loadScene(
      new ViewFactory(FXMLPaths.ImportEntityManagerView),
      overviewController)

    subjectsTab.setContent(subjectsController.mainBox)
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