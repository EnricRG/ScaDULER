package service

import model.Resource
import model.blueprint.ResourceBlueprint
import model.descriptor.ResourceDescriptor

import scala.collection.mutable

class ResourceDatabase extends Database[Resource]{

    class Initializer{

    }

    def this(initializer: ResourceDatabase#Initializer) = this

    private val indexByName: mutable.Map[String, Resource] = new mutable.HashMap

    def index(r: Resource): Unit = {
        indexByName.put(r.name, r)
    }

    def deindex(r: Resource): Unit ={
        if(r != null) indexByName.remove(r.name)
    }

    def createResource: (ID, Resource) = {
        val id = reserveNextId
        val resource = new Resource(id)
        index(resource)
        addElement(id, resource)
    }

    def createResourceFromDescriptor(rd: ResourceDescriptor): (ID, Resource) = {
        val ret = createResource
        Resource.setResourceFromDescriptor(ret._2, rd)
        ret
    }

    @deprecated
    def createResourceFromBlueprint(rb: ResourceBlueprint): (ID, Resource) = {
        val ret = createResource
        Resource.setResourceFromBlueprint(ret._2, rb)
        ret
    }

    def removeResource(rid: ID): Unit = { removeElement(rid); deindex(getElement(rid).orNull) }
    def removeResource(r: Resource): Unit = { removeElement(r.getID); deindex(r) }

    def deleteResource(rid: ID): Unit = { deleteElement(rid); deindex(getElement(rid).orNull) }
    def deleteResource(r: Resource): Unit = { deleteElement(r.getID); deindex(r) }

    def getResourceByName(n: String): Option[Resource] = indexByName.get(n)
}

class ReadOnlyResourceDatabase(resourceDatabase: ResourceDatabase){

}