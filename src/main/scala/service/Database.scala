package service

import scala.collection.mutable

/** Identifiable element
 *
 *  Abstract class that all entities must extend to be able to be stored in a Database.
 *  Provides an identifier to the element.
 *
 *  @param id unique identifier for the element. If not unique, Database will override previous element with that id
 */
@SerialVersionUID(1L)
abstract class Identifiable(private val id: ID) extends Serializable{
    def getID: ID = id
}

/** Abstract database of Identifiable objects
 *
 *  Represents and implements an abstract database of Identifiable objects. This class will generally be used to store
 *  application entities such as an Event or a Course. It can:
 *  - Add a previously created element.
 *  - Softly remove an element (remove). This means that the element is not erased but only hidden, and can be restored.
 *  - Hardly remove an element (delete). This will definitively remove the element from the database.
 *  - Search for elements in 'constant' time.
 *
 *  @tparam E Identifiable type for the database elements.
 */
@SerialVersionUID(1L)
abstract class Database[E<:Identifiable] extends Serializable {

    class DatabaseElement(element: E) extends Serializable {
        private var visible: Boolean = true

        def hide: this.type = { visible = false; this }
        def show: this.type = { visible = true; this }
        def isVisible: Boolean = visible
        def apply: E = element
    }

    protected final val elements: mutable.HashMap[ID,DatabaseElement] = mutable.HashMap()
    protected var nextId: ID = 0

    protected final def reserveNextId: ID = {
        val nid = nextId
        nextId += 1
        nid
    }

    protected final def addElement(key: ID, element: E): (ID, E) = {
        elements.getOrElseUpdate(key, new DatabaseElement(element))
        (key, element)
    }

    final def getElement(key: ID): Option[E] = elements.get(key) match {
        case Some(e) if e.isVisible => Some(e.apply)
        case _ => None
    }
    final def getElementOrElse(key: ID, el: => E): E = getElement(key) match{
        case Some(e) => e
        case _ => el
    }

    //soft delete
    protected final def removeElement(key: ID): Option[E] = elements.get(key) match{
        case Some(e) => Some(e.hide.apply)
        case None => None
    }
    //hard delete
    protected final def deleteElement(key: ID): Option[E] = elements.remove(key) match{
        case Some(e: DatabaseElement) => Some(e.apply)
        case None => None
    }

    final def getIDs: Iterable[ID] = elements.filter(_._2.isVisible).keys
    final def getElements: Iterable[E] = elements.values.filter(_.isVisible).map(_.apply)

    //soft delete
    protected final def removeElement(e: E): Option[E] = removeElement(e.getID)
    //hard delete
    protected final def deleteElement(e: E): Option[E] = deleteElement(e.getID)

    final def size: Int = elements.size
}