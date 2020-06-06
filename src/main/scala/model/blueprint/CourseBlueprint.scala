package model.blueprint

import model.CourseLikeImpl
import model.descriptor.CourseDescriptor

class CourseBlueprint extends CourseLikeImpl{ }

object CourseBlueprint{
  def setBlueprintFromDescriptor(cb: CourseBlueprint, cd: CourseDescriptor): Unit = {
    cb.name = cd.name
    cb.description = cd.description
  }
}
