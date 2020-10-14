package service

import model.Resource
import model.blueprint.ResourceBlueprint
import model.descriptor.ResourceDescriptor

class ResourceDatabase extends DatabaseImpl[Resource] {

  def createResource: (ID, Resource) = {
    val id = reserveNextId()
    val resource = new Resource(id)
    addElement(id, resource)
  }

  def createResourceFromDescriptor(rd: ResourceDescriptor): (ID, Resource) = {
    val entry = createResource
    Resource.setResourceFromDescriptor(entry._2, rd)
    entry
  }

  def createResourceFromBlueprint(rb: ResourceBlueprint): (ID, Resource) = {
    val entry = createResource
    Resource.setResourceFromBlueprint(entry._2, rb)
    entry
  }

  def removeResource(id: ID): Unit =
    removeElement(id)

  def removeResource(r: Resource): Unit =
    removeElement(r)

  //TODO optimize with indexing. Property changes should be listened.
  def getResourceByName(name: String): Option[Resource] =
    getElements.find(_.name == name)

  def resources: Iterable[Resource] =
    getElements
}