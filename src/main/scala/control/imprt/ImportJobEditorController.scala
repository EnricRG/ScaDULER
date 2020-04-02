package control.imprt

import java.net.URL
import java.util.ResourceBundle

import control.StageController
import file.imprt.{ImportError, ImportJob, ImportType}
import javafx.fxml.Initializable
import model.blueprint.{CourseBlueprint, EventBlueprint, ResourceBlueprint, SubjectBlueprint}

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



  protected val editableImportJob: MutableImportJob = MutableImportJob(
    new ArrayBuffer ++= importJob.subjects,
    new ArrayBuffer ++= importJob.events,
    new ArrayBuffer ++= importJob.resources,
    new ArrayBuffer ++= importJob.courses,
    new ArrayBuffer ++= importJob.errors,
    importJob.finished,
    importJob.importType)

  override def initialize(location: URL, resources: ResourceBundle): Unit = {

  }

  def getImportJob: ImportJob = editableImportJob.toImportJob
}

object ImportJobEditorController{

  class SubjectEditorController extends Initializable{

    def initialize(location: URL, resources: ResourceBundle): Unit = {

    }
  }
}
