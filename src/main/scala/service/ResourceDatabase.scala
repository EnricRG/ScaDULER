package service

import model.Resource
import model.blueprint.ResourceBlueprint

class ResourceDatabase extends Database[Resource]{

    class Initializer{

    }

    def this(initializer: ResourceDatabase#Initializer) = this

    //private val indexByName: mutable.Map[String, Resource] = new mutable.HashMap

    /*def index(r: Resource): Unit = {
        if(r != null) indexByName.put(r.getName, r)
    }

    def deindex(r: Resource): Unit ={
        if(r != null) indexByName.remove(r.getName)
    }*/

    def createResource: (ID, Resource) = {
        val id = reserveNextId
        val resource = new Resource(id)
        //index(resource)
        addElement(id, resource)
    }
    def createResourceFromBlueprint(rb: ResourceBlueprint): (ID, Resource) = {
        val ret = createResource
        Resource.setResourceFromBlueprint(ret._2, rb)
        ret
    }

    def removeResource(rid: ID): Unit = { removeElement(rid); /*deindex(getElement(rid).orNull)*/ }
    def removeResource(r: Resource): Unit = { removeElement(r.getID); /*deindex(r)*/ }

    def deleteResource(rid: ID): Unit = { deleteElement(rid); /*deindex(getElement(rid).orNull)*/ }
    def deleteResource(r: Resource): Unit = { deleteElement(r.getID); /*deindex(r)*/ }

    //def getResourceByName(n: String): Option[Resource] = indexByName.get(n)
    //TODO O(n), reduce to sub-linear time.
    def getResourceByName(n: String): Option[Resource] = getElements.find(_.getName == n)
}

class ReadOnlyResourceDatabase(resourceDatabase: ResourceDatabase){

}