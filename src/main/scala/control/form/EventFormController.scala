package control.form

import model.{Course, Event, Resource, Subject}

@deprecated
class EventFormController(
  subjects: Iterable[Subject],
  courses: Iterable[Course],
  resources: Iterable[Resource],
  events: Iterable[Event])
  extends CreateEventFormController(None, subjects, courses, resources, events)
