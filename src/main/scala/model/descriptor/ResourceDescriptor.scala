package model.descriptor

import app.AppSettings
import model.ResourceSchedule

class ResourceDescriptor {
  var name: String = ""
  var capacity: Int = 0
  var availability: ResourceSchedule = new ResourceSchedule(AppSettings.timeSlots)
}
