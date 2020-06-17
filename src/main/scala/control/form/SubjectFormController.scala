package control.form

import model.{Course, Resource}

class SubjectFormController(
  courses: Iterable[Course],
  resources: Iterable[Resource]) extends SubjectDescriptorFormController(courses, resources) { }