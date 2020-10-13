package service

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/** Identifiable element.
 */
trait Identifiable2 {

  /** Returns the `id` of this identifiable element.
   * @return The id of this element.
   */
  def id: ID
}

/** Identifiable element builder.
 */
trait IdentifiableBuilder[E <: Identifiable2] {

  /** Gives the builder an `id` to create an object.
   */
  def withId(id: ID): this.type

  /** Checks if the `id` building property has been set.
   * @return `true` if set, `false` otherwise.
   */
  def hasId: Boolean

  /** Returns the last `id` given to this builder.
   * @note If none was given, subclasses can choose how to handle it.
   * @return Last given `id`.
   */
  def id: ID

  /** Checks if the builder can build an object. This method has to return `true` only if we can ensure that
   * the object will be built and returned without any errors when calling `build` method.
   * @return `true` if an object can be built without errors, `false` otherwise.
   */
  def canBuild: Boolean = hasId

  /** Builds an object with the previously given properties and returns that object.
   * @return The built object.
   */
  def build: E
}

/** TODO
 */
trait Database2[E <: Identifiable2] {

}

/** Default implementation for the `Database` trait. Most database specializations will want to extend this trait
 * with simple naming overriding to get a nice implementation for all the methods they need.
 * @tparam E Any type that extends the `Identifiable` trait
 */
trait DatabaseImpl[E <: Identifiable2] extends Database2[E] with Serializable {

  /** Encapsulates a database entity in an object that allows further treatment.
   * @constructor Creates a new database element with an entity.
   * @param entity The encapsulated entity.
   */
  private class DatabaseElement(val entity: E) extends Serializable {

    /** Visibility of this element in database queries. */
    private var _visible: Boolean = true

    /** Hides this element to database queries and returns itself to allow method call concatenation.
     * @return This database element.
     */
    def hide(): this.type = { _visible = false; this }

    /** Makes visible this element to database queries and returns itself to allow method call concatenation.
     * @return This database element.
     */
    def show(): this.type = { _visible = true; this }

    /** Checks if this element is visible to database queries.
     * @return `true` if the element is visible to database queries, `false` otherwise.
     */
    def isVisible: Boolean = _visible
  }


  /************************************************************************\
   *                                                                      *
   *                              Constants                               *
   *                                                                      *
  \************************************************************************/


  /** First non-reserved id at object creation. */
  private final def StartingId: ID = 0


  /************************************************************************\
   *                                                                      *
   *                              Attributes                              *
   *                                                                      *
  \************************************************************************/


  /** Table of elements mapped by its containing entities id. */
  protected final val elements: mutable.HashMap[ID,DatabaseElement] = new mutable.HashMap

  /** Next non-reserved id. */
  protected var nextId: ID = StartingId


  /************************************************************************\
   *                                                                      *
   *                      Database mutation methods                       *
   *                                                                      *
  \************************************************************************/


  /** Returns the first non-reserved id and skips to the next one.
   * @return The first non-reserved id. */
  protected final def reserveNextId(): ID = {
    val nid = nextId
    nextId += 1
    nid
  }

  /** Skips to the next non-reserved id. */
  protected final def skipNextId(): Unit =
    nextId += 1

  /** Reverts to the last reserved id, deleting the last added entity between the last id reservation and this call,
   * if any.
   * @param id The last reserved id.
   * @note This method removes the database entity mapped with `ìd`, if any. That's why you need to
   *       pass `id` as a parameter, to ensure you know what entity is possibly being deleted.
   * @note This method should never be used (only in very rare cases). You should use a combination of `nextId` value
   *       reading + `skipNextID()` instead.
   * @return `true` if `nextId` has been reverted to `id` and deleting a database entity was needed to do so,
   *         `false` otherwise.
   */
  protected final def freeActualId(id: ID): Boolean = {
    if (id == nextId - 1)
      elements.remove(id).nonEmpty
    else
      false
  }

  /** Adds an entity mapped by a given key (i.e. entity's id) and returns the given parameters as a pair.
   * If the key was already taken, nothing happens and the key and the entity identified with that key
   * get returned as a pair.
   * @param key The id of the entity.
   * @param entity The entity to be added.
   * @return The pair (`key`, `entity`) if the key wasn't used. If the key was used, it returns
   *         the pair (`key`, `dbEntity`) where `dbEntity` is the entity that was identified by that key.
   */
  protected final def addElement(key: ID, entity: E): (ID, E) = {
    val dbElement = elements.getOrElseUpdate(key, new DatabaseElement(entity))
    (key, dbElement.entity)
  }

  protected final def addElement(entity: E): (ID, E) = {
    addElement(entity.id, entity)
  }

  /** Adds a new created entity with the first non-reserved id as entity id given an IdentifiableBuilder
   * that only needs the id to be able to build an Identifiable object. If the builder can't build an entity
   * given the id, this method does nothing.
   * @param entityBuilder The builder to create the entity.
   * @return An optional pair of id and entity if the entity was successfully created and added to the database.
   */
  protected final def addElement(entityBuilder: IdentifiableBuilder[E]): Option[(ID, E)] = {
    val id = nextId

    if(entityBuilder.withId(id).canBuild)
      Try(entityBuilder.build) match {
        case Success(entity) =>
          skipNextId() //no need to get previous value, we already have it
          Some(addElement(id, entity))
        case Failure(exception) =>
          exception.printStackTrace()
          None
      }
    else
      None
  }

  /** Hides the entity identified by the given id, if any.
   * @param key Entity id.
   * @return Optional value, the hidden entity if any.
   */
  protected final def hideElement(key: ID): Option[E] =
    elements.get(key).map(_.hide().entity)

  protected final def hideElement(e: E): Option[E] =
    hideElement(e.id)

  /** Makes visible the entity identified by the given id, if any.
   * @param key Entity id.
   * @return Optional value, the visible entity if any.
   */
  protected final def showElement(key: ID): Option[E] =
    elements.get(key).map(_.show().entity)

  protected final def showElement(e: E): Option[E] =
    showElement(e.id)

  /** Removes the entity identified by the given id, if any.
   * @param key Entity id.
   * @return Optional value, the removed entity if any.
   */
  protected final def removeElement(key: ID): Option[E] =
    elements.remove(key).map(_.entity)

  protected final def removeElement(e: E): Option[E] =
    removeElement(e.id)


  /************************************************************************\
   *                                                                      *
   *                      Database accessing methods                      *
   *                                                                      *
  \************************************************************************/

  /** Checks if there's a visible entity identified by the given `id`.
   * @param key Entity id.
   * @return `true` if there's a visible entity identified by the given id, `false` otherwise.
   */
  protected final def isVisible(key: ID): Boolean =
    elements.get(key).exists(_.isVisible)

  /** Finds the visible entity identified by the given id, if any.
   * @param key Entity id.
   * @return Optional value, the visible entity identified by that id, if any.
   */
  protected final def getElement(key: ID): Option[E] =
    elements.get(key).filter(_.isVisible).map(_.entity)

  /** Finds the entity identified by the given id, visible or not, if any.
   * @param key Entity id.
   * @return Optional value, the entity identified by that id, if any.
   */
  protected final def getAnyElement(key: ID): Option[E] =
    elements.get(key).map(_.entity)

  /** Finds the visible entity identified by the given id, if any.
   * If none, returns `alternative` instead.
   * @param key Entity id.
   * @param alternative Alternative value if no visible entity is identified by the given id.
   * @return The visible entity identified by that id, or `alternative` if none.
   */
  protected final def getElementOrElse(key: ID, alternative: => E): E =
    getElement(key).getOrElse(alternative)

  /** Finds the entity identified by the given id, visible or not, if any.
   * If none, returns `alternative` instead.
   * @param key Entity id.
   * @param alternative Alternative value if no entity is identified by the given id.
   * @return The entity identified by that id, or `alternative` if none.
   */
  protected final def getAnyElementOrElse(key: ID, alternative: => E): E =
    getAnyElement(key).getOrElse(alternative)

  /** Retrieves all visible entities.
   * @return All visible database entities.
   */
  protected final def getElements: Iterable[E] =
    elements.values.filter(_.isVisible).map(_.entity)

  /** Retrieves all entities.
   * @return All database entities.
   */
  protected final def getAllElements: Iterable[E] =
    elements.values.map(_.entity)
}



/** Identifiable element.
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

    //true when user has finished creating/editing this element, false otherwise
    private var finished: Boolean = false

    def hide: this.type = { visible = false; this }
    def show: this.type = { visible = true; this }
    def isVisible: Boolean = visible

    def isFinished: Boolean = finished
    def setAsUnfinished(): Unit = finished = false
    def setAsFinished(): Unit = finished = true

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

  final def isFinished(key: ID): Boolean = elements.get(key) match {
    case Some(de) => de.isFinished
    case _ => false
  }
  final def setAsFinished(key: ID): Unit = elements.get(key) match {
    case Some(de) => de.setAsFinished()
    case _ =>
  }
  final def setAsUnfinished(key: ID): Unit = elements.get(key) match {
    case Some(de) => de.setAsUnfinished()
    case _ =>
  }

  final def size: Int = elements.size
}