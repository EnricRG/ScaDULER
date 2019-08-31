package solver

import model.{NewEvent, Resource}

case class InstanceData(nDays: Int,
                        dayDuration: Int,
                        nResources: Int,
                        resources: List[Resource],
                        nEvents: Int,
                        events: List[NewEvent])
