object MainTest extends App{

    val TimeSlotDuration = 30 //This shouldn't be here

    override def main(args: Array[String]): Unit = {
        val nEvents = 51

        val labRooms = 1
        val classRooms = 3
        val pcRooms = 5

        val events = List(
          new EventData(1, time_slots = 2)
        )
    }
}
