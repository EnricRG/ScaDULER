package service

import model.Resource

class ResourceDatabase extends Database[String,Resource] {

    class Initializer{

    }

    def this(initializer: ResourceDatabase#Initializer) = this

    def createResource(c: Resource): Resource = addElement(c.name, c)
    def createResource(name: String, quantity: Int): Resource = createResource(new Resource(name, quantity))

    def createResourceOrElseIncrement(name: String, quantity: Int): Resource = {
        val c = new Resource(name, quantity)
        val c2 = createResource(c)
        if(c != c2) c2.incrementQuantity(quantity)
        c2
    }
}
