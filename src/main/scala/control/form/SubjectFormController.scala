package control.form

import model.descriptor.{EventDescriptor, SubjectDescriptor}
import model.{Course, Resource}

class SubjectFormController(
  courses: Iterable[Course],
  resources: Iterable[Resource]) extends AbstractSubjectFormController(courses, resources) {

  def newEventDescriptor: ED = new EventDescriptor

  def newSubjectDescriptor: SD = new SubjectDescriptor
}