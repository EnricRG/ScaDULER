package model.blueprint

import model.ResourceLikeImpl
import model.descriptor.ResourceDescriptor

class ResourceBlueprint extends ResourceLikeImpl { }

object ResourceBlueprint {
  def fromDescriptor(rd: ResourceDescriptor): ResourceBlueprint = {
    val rb = new ResourceBlueprint

    rb.name = rd.name
    rb.capacity = rd.capacity
    rb.availability = rd.availability

    rb
  }
}