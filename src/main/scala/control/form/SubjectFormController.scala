package control.form

import model.descriptor.EventDescriptor
import model.{Course, Resource, SubjectDescriptor}

class SubjectFormController2(
  courses: Iterable[Course],
  resources: Iterable[Resource]) extends AbstractSubjectFormController(courses, resources) {

  def newEventDescriptor: ED = new EventDescriptor

  def newSubjectDescriptor: SD = new SubjectDescriptor
}