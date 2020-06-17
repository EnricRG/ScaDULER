package model.build

import model.blueprint.{CourseBlueprint, EventBlueprint, ResourceBlueprint, SubjectBlueprint}
import model.descriptor.EventDescriptor
import model.{CourseLike, EventLike, ResourceLike, SubjectLike}

trait EventLikeBuilder[S <: SubjectLike[S,C,R,E], C <: CourseLike, R <: ResourceLike, E <: EventLike[S,C,R,E]]{
  private val _descriptor: EventDescriptor[S,C,R,E] = new EventDescriptor

  def withName(name: String): this.type = { _descriptor.name = name; this}
}

class EventBlueprintBuilder
  extends EventLikeBuilder [SubjectBlueprint, CourseBlueprint, ResourceBlueprint, EventBlueprint]{ }
