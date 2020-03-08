package solver

import model.Weeks.Week
import service.ID

case class EventAssignment(eventID: ID, week: Week, interval: Int)
