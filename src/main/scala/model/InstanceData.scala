package model

case class InstanceData(dayDuration: Int,
                        labRooms: Int, classRooms: Int, pcRooms: Int,
                        nEvents: Int, events: List[Event], preassignedEvents: List[Event],
                        precedences: List[(Event, Event)])
