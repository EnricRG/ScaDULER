package service

import model.Course
import model.blueprint.CourseBlueprint
import model.descriptor.CourseDescriptor

class CourseDatabase extends DatabaseImpl[Course] {

  def createCourse(): (ID, Course) = {
    val id = reserveNextId()
    val course = new Course(id)
    addElement(id, course)
  }

  def createCourseFromDescriptor(cd: CourseDescriptor): (ID, Course) = {
    val entry = createCourse()
    Course.setCourseFromDescriptor(entry._2, cd)
    entry
  }

  def createCourseFromBlueprint(cb: CourseBlueprint): (ID, Course) = {
    val entry = createCourse()
    Course.setCourseFromBlueprint(entry._2, cb)
    entry
  }

  def removeCourse(cid: ID): Unit =
    removeElement(cid)

  def removeCourse(c: Course): Unit =
    removeElement(c)

  //TODO optimize with indexing. Property changes should be listened.
  def getCourseByName(name: String): Option[Course] =
    courses.find(_.name == name)

  def courses: Iterable[Course] =
    getElements
}
