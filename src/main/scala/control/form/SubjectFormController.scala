package control.form

import model.{Course, Resource, EventDescriptor2, SubjectDescriptor}

class SubjectFormController2(
  courses: Iterable[Course],
  resources: Iterable[Resource]) extends AbstractSubjectFormController(courses, resources) {

  def newEventDescriptor: ED = new EventDescriptor2

  def newSubjectDescriptor: SD = new SubjectDescriptor
}