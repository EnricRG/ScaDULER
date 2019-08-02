package service

import scala.collection.mutable

abstract class Database[K,E] {

    final val elements: mutable.HashMap[K,E] = mutable.HashMap()

    def addElement(key: K, element: E): Option[E] ={
        elements.contains(key) match {
            case false => elements.put(key,element)
            case _ => None
        }
    }
    def getElement(key: K): Option[E] = elements.get(key)
    def getElementOrElse(key: K, el: => E): E = elements.getOrElse(key,el)
    def removeElement(key: K): Option[E] = elements.remove(key)

}
