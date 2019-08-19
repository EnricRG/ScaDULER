package solver

import model.OldEvent

@Deprecated
case class InstanceData(dayDuration: Int,
                        labRooms: Int, classRooms: Int, pcRooms: Int,
                        nEvents: Int, events: List[OldEvent], preassignedEvents: List[OldEvent],
                        precedences: List[(OldEvent, OldEvent)])
