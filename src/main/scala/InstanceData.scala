case class InstanceData(dayDuration: Int,
                        labRooms: Int, classRooms: Int, pcRooms: Int,
                        nEvents: Int, events: List[EventData], preassignedEvents: List[EventData],
                        precedences: List[(EventData, EventData)])
