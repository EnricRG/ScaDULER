package control

import app.{AppSettings, FXMLPaths, MainApp}
import control.form.{CreateEventFormController, CreateSubjectLikeFormController, SubjectLikeForm}
import control.manage.CourseManagerController2
import factory.ViewFactory
import javafx.stage.Modality
import model.descriptor.EventDescriptor
import model.{Course, Event, Resource, Subject}
import util.Utils

object ScalaMainController {
  def promptSubjectForm(mc: MainController): Unit = {
    val subjectForm = new CreateSubjectLikeFormController(
      MainApp.getDatabase.courseDatabase.courses,
      MainApp.getDatabase.resourceDatabase.resources)

    subjectForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItem("subjectForm_windowTitle"),
      mc.addButtons_subject.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.SubjectForm),
      subjectForm))

    val osd = subjectForm.waitFormResult

    if (osd.nonEmpty){
      val sd = osd.get

      val (sid,subject) = MainApp.getDatabase.subjectDatabase.createSubject
      val events = {
        val descriptorMap = sd.events.map((_,MainApp.getDatabase.eventDatabase.createEvent._2)).toMap

        descriptorMap.foreach { case (ed, e) =>
          Event.setEventFromDescriptor(e, fromSubjectFormEventDescriptor(Some(subject), ed, descriptorMap))
        }

        descriptorMap.values
      }

      val eventsByType = events.groupBy(_.eventType)

      sd.eventTypeIncompatibilities.foreach(eti =>
        eventsByType.getOrElse(eti.getFirstType, Nil).foreach(e1 =>
          eventsByType.getOrElse(eti.getSecondType, Nil).foreach(e2 =>
            e1.addIncompatibility(e2))))

      Subject.setSubjectFromDescriptor(subject,sd,events)
      MainApp.getDatabase.subjectDatabase.setAsFinished(sid)
      events.foreach(mc.addUnassignedEvent)
    }
  }

  private def fromSubjectFormEventDescriptor(s: Option[Subject],
    ed: SubjectLikeForm[Course, Resource]#ED,
    descriptorMapping: Map[SubjectLikeForm[Course, Resource]#ED, Event]
    ): EventDescriptor[Subject,Course,Resource,Event] = {

    val descriptor = new EventDescriptor[Subject,Course,Resource,Event]

    descriptor.name = ed.name
    descriptor.shortName = ed.shortName
    descriptor.description = ed.description
    descriptor.eventType = ed.eventType
    descriptor.duration = ed.duration
    descriptor.periodicity = ed.periodicity

    descriptor.subject = s
    descriptor.course = ed.course
    descriptor.quarter = ed.quarter
    descriptor.neededResource = ed.neededResource
    descriptor.incompatibilities ++= ed.incompatibilities.map(descriptorMapping.toMap)
    /*ed.incompatibilities
      .collect(descriptorMapping.toMap)
      .foreach(descriptor.incompatibilities.add)*/

    descriptor
  }

  def promptEventForm(mainController: MainController): Unit = {
    val eventForm = new CreateEventFormController(
      MainApp.getDatabase.subjectDatabase.getFinishedSubjects,
      MainApp.getDatabase.courseDatabase.courses,
      MainApp.getDatabase.resourceDatabase.resources,
      MainApp.getDatabase.eventDatabase.events)

    eventForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse(
        "eventForm_windowTitle",
        "New Event"),
      mainController.addButtons_event.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[StageController](FXMLPaths.EventForm),
      eventForm
    ))

    val oed = eventForm.waitFormResult

    if(oed.nonEmpty){
      val ed = oed.get

      val event = MainApp.getDatabase.eventDatabase.createEventFromDescriptor(ed)._2

      if(ed.subject.nonEmpty){
        ed.subject.get.events_$plus$eq(event)
      }

      mainController.addUnassignedEvent(event)
    }
  }

  def promptCourseManager(mainController: MainController): Unit = {
    Utils.promptBoundWindow(
      AppSettings.language.getItemOrElse("courseManager_windowTitle", "Manage Courses"),
      mainController.manageButtons_courses.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory(FXMLPaths.EntityManagerPanel),
      new CourseManagerController2(
        MainApp.getDatabase.courseDatabase.courses,
        mainController,
        MainApp.getDatabase)
    ).showAndWait()
  }

}
