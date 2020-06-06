package control

import app.{AppSettings, FXMLPaths, MainApp}
import control.form.{AbstractSubjectFormControllerResult, SubjectFormController}
import factory.ViewFactory
import javafx.stage.Modality
import model.descriptor.EventDescriptor
import model.{Course, Event, Resource, Subject}
import util.Utils

object ScalaMainController {
  def promptSubjectForm(mc: MainController): Unit = {
    val subjectForm = new SubjectFormController(
      MainApp.getDatabase.courseDatabase.getCourses,
      MainApp.getDatabase.resourceDatabase.getElements)

    subjectForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItem("subjectForm_windowTitle"),
      mc.addButtons_subject.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[SubjectFormController](FXMLPaths.SubjectForm),
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

      Subject.setSubjectFromDescriptor(subject,sd,events)
      MainApp.getDatabase.subjectDatabase.setAsFinished(sid)
      events.foreach(mc.addUnassignedEvent)
    }
  }

  private def fromSubjectFormEventDescriptor(s: Option[Subject],
    ed: AbstractSubjectFormControllerResult[Course, Resource]#ED,
    descriptorMapping: Map[AbstractSubjectFormControllerResult[Course, Resource]#ED, Event]
    ): EventDescriptor[Subject,Course,Resource,Event] =
  {
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
}
