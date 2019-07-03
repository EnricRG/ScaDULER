object MainTest extends App{

    val TimeSlotDuration = 30 //This shouldn't be here

    override def main(args: Array[String]): Unit = {

        //
        // Instance 1: 1er_s1 (2018-2019)
        //

        val nEvents = 51

        val labRooms = 1
        val classRooms = 3
        val pcRooms = 5

        val events = List(
          new EventData(1, time_slots = 2, relativeStart = 13, week = Weeks.EveryWeek),
          new EventData(2, time_slots = 2, relativeStart = 26, week = Weeks.EveryWeek),
          new EventData(3, time_slots = 2, relativeStart = 39, week = Weeks.EveryWeek),
          new EventData(4, time_slots = 2, relativeStart = 52, week = Weeks.EveryWeek),
          new EventData(5, time_slots = 2, relativeStart = 65, week = Weeks.EveryWeek),
          new EventData(6, time_slots = 4, week = Weeks.EveryWeek)
        )
    }
}
