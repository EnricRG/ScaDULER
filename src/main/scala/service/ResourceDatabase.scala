package service

import model.Resource

class ResourceDatabase extends Database[Resource]{

    class Initializer{

    }

    def this(initializer: ResourceDatabase#Initializer) = this
    
    def createResource(): (ID, Resource) = {
        val id = reserveNextId
        val resource = new Resource(id)
        addElement(id, resource)
    }

    def removeResource(rid: ID): Unit = removeElement(rid)
    def removeResource(r: Resource): Unit = removeElement(r.getID)

    def deleteResource(rid: ID): Unit = deleteElement(rid)
    def deleteResource(r: Resource): Unit = deleteElement(r.getID)
}

class ReadOnlyResourceDatabase(resourceDatabase: ResourceDatabase){

}