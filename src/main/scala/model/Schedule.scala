package model

import scala.collection.mutable

@SerialVersionUID(1L)
class Schedule[T](intervals: Int) extends Serializable {
    private val timeline: mutable.Map[Int,T] = new mutable.HashMap

    def getValueAtInterval(interval: Int): Option[T] = if(interval < intervals) timeline.get(interval) else None
    def getValueAtIntervalOrElse(interval: Int, el: => T): T = if(interval < intervals) timeline.getOrElse(interval, el) else el
    //Danger, if interval exceeds limit caller will not know that this happened. So...
    //pre: interval < intervals
    def getValueAtIntervalOrElseUpdate(interval: Int, el: => T): T = if(interval < intervals) timeline.getOrElseUpdate(interval, el) else el

    def updateInterval(interval: Int, element: T): Unit = if(interval < intervals) timeline.update(interval, element)
    def removeInterval(interval: Int): Unit = timeline.remove(interval)

    //I did this because I could.
    def applyToInterval(interval: Int, function: => (T => Any)): Option[Any] = getValueAtInterval(interval) match{
        case Some(t) => Some(function.apply(t))
        case _ => None
    }

    def getAllElements: Iterable[T] = timeline.toSeq.sortBy(_._1).map(_._2)

    override def toString: String = timeline.toString
}
