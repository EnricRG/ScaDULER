package service

import scala.collection.mutable

abstract class Database[E] {

    class DatabaseElement(val id: Long, element: E) {
        private var visible: Boolean = true

        def hide: this.type = { visible = false; this }
        def show: this.type = { visible = true; this }
        def isVisible: Boolean = visible
        def apply: E = element
    }

    protected final val elements: mutable.HashMap[Long,DatabaseElement] = mutable.HashMap()
    protected var nextId: Long = 0

    def addElement(element: E): Long = {val key = nextId; nextId+=1; elements.getOrElseUpdate(key, new DatabaseElement(key, element)); key}
    def getElement(key: Long): Option[E] = elements.get(key) match {
        case Some(e) if e.isVisible => Some(e.apply)
        case _ => None
    }
    def getElementOrElse(key: Long, el: => E): E = getElement(key) match{
        case Some(e) => e
        case _ => el
    }
    //soft delete
    def removeElement(key: Long): Option[E] = elements.get(key) match{
        case Some(e) => Some(e.hide.apply)
        case None => None
    }
    //hard delete
    def deleteElement(key: Long): Option[E] = elements.remove(key) match{
        case Some(e: DatabaseElement) => Some(e.apply)
        case None => None
    }
    def getElements: Iterable[E] = elements.values.filter(_.isVisible).map(_.apply)

    def removeElement(e: E): Option[E] = elements.find(_._2 == e) match {
        case Some((id,_)) => removeElement(id)
        case None => None
    }
    //hard delete
    def deleteElement(e: E): Option[E] = elements.find(_._2 == e) match {
        case Some((id,_)) => deleteElement(id)
        case None => None
    }
    def size: Int = elements.size

}
