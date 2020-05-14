package control

import app.{AppSettings, FXMLPaths, MainApp}
import control.form.SubjectFormController2
import factory.ViewFactory
import javafx.stage.Modality
import model.Subject
import util.Utils

object ScalaMainController {
  def promptSubjectForm(mc: MainController): Unit = {
    val subjectForm = new SubjectFormController2(
      MainApp.getDatabase.courseDatabase.getCourses,
      MainApp.getDatabase.resourceDatabase.getElements)

    subjectForm.setStage(Utils.promptBoundWindow(
      AppSettings.language.getItem("subjectForm_windowTitle"),
      mc.addButtons_subject.getScene.getWindow,
      Modality.WINDOW_MODAL,
      new ViewFactory[SubjectFormController2](FXMLPaths.SubjectForm),
      subjectForm))

    val osd = subjectForm.waitFormResult

    if (osd.nonEmpty){
      val sd = osd.get

      val (sid,subject) = MainApp.getDatabase.subjectDatabase.createSubject
      val events = List() //TODO generate events

      Subject.setSubjectFromBlueprint(subject,sd,events)

      MainApp.getDatabase.subjectDatabase.setAsFinished(sid)
    }
  }
}
