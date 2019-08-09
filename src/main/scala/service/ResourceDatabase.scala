package service

import model.Resource

class ResourceDatabase extends Database[Resource] {

    class Initializer{

    }

    def this(initializer: ResourceDatabase#Initializer) = this

    //TODO: update to pure DBID model
    def createResource(r: Resource): Resource = {
        val id = addElement(r)
        getElement(id).get //this should be secure because we just added the resource
    }
    def createResource(name: String, quantity: Int): Resource = createResource(new Resource(name, quantity))

    def createResourceOrElseIncrement(name: String, quantity: Int): Resource = {
        val r = new Resource(name, quantity)
        val r2 = createResource(r)
        if(r != r2) r2.incrementQuantity(quantity)
        r2
    }
}
