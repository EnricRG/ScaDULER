package file.imprt

import model.blueprint.{CourseBlueprint, EventBlueprint, ResourceBlueprint, SubjectBlueprint}

case class ImportJob(subjects: List[SubjectBlueprint],
                     events: List[EventBlueprint],
                     resources: List[ResourceBlueprint],
                     courses: List[CourseBlueprint],
                     errors: List[ImportError],
                     finished: Boolean,
                     importType: ImportType)
