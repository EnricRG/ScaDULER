package file.imprt

import model.blueprint.{CourseBlueprint, EventBlueprint, ResourceBlueprint, SubjectBlueprint}

import scala.collection.mutable.ArrayBuffer

case class ImportJob(
  subjects: List[SubjectBlueprint],
  events: List[EventBlueprint],
  resources: List[ResourceBlueprint],
  courses: List[CourseBlueprint],
  errors: List[ImportError],
  finished: Boolean,
  importType: ImportType
)

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
