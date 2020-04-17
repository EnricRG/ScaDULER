package control.form

import model.{Course, Event, Resource, Subject}

class EventFormController(
  subjects: Iterable[Subject],
  courses: Iterable[Course],
  resources: Iterable[Resource],
  events: Iterable[Event]) extends EventDescriptorFormController(subjects, courses, resources, events)
