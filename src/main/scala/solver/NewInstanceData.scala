package solver

import model.{NewEvent, Resource}

case class NewInstanceData(nDays: Int,
                           dayDuration: Int,
                           nResources: Int,
                           resources: List[Resource],
                           nEvents: Int,
                           events: List[NewEvent])
