object MainTest extends App{

    val TimeSlotDuration = 30 //This shouldn't be here

    override def main(args: Array[String]): Unit = {

        //
        // Instance 1: 1er_s1 (2019-2020)
        //

        val labRooms = 1
        val classRooms = 2
        val pcRooms = 4

        val events = List(
            //Breaks
            new EventData(1, time_slots = 2, relativeStart = 9),
            new EventData(2, time_slots = 2, relativeStart = 31),
            new EventData(3, time_slots = 2, relativeStart = 53),
            new EventData(4, time_slots = 2, relativeStart = 75),
            new EventData(5, time_slots = 2, relativeStart = 97),

            //Day 1
            new EventData(6, time_slots = 4, classRoomsNeeded = 1),
            new EventData(7, time_slots = 4, labRoomsNeeded = 1),
            new EventData(8, time_slots = 4, pcRoomsNeeded = 1),
            new EventData(9, time_slots = 4, pcRoomsNeeded = 1),

            //Day 2
            new EventData(10, time_slots = 4, classRoomsNeeded = 1),
            new EventData(11, time_slots = 4, classRoomsNeeded = 1),

            new EventData(12, time_slots = 4, classRoomsNeeded = 1),
            new EventData(13, time_slots = 4, labRoomsNeeded = 1),
            new EventData(14, time_slots = 4, pcRoomsNeeded = 1),
            new EventData(15, time_slots = 4, classRoomsNeeded = 1),

            //Day 3
            new EventData(16, time_slots = 4, classRoomsNeeded = 1),
            new EventData(17, time_slots = 2, classRoomsNeeded = 1),
            new EventData(18, time_slots = 2, classRoomsNeeded = 1),
            new EventData(19, time_slots = 2, classRoomsNeeded = 1),
            new EventData(20, time_slots = 2, classRoomsNeeded = 1),
            new EventData(21, time_slots = 4, labRoomsNeeded = 1),

            new EventData(22, time_slots = 4, classRoomsNeeded = 1),
            new EventData(23, time_slots = 4, classRoomsNeeded = 1),
            new EventData(24, time_slots = 2, classRoomsNeeded = 1),

            //Day 4
            new EventData(25, time_slots = 2, classRoomsNeeded = 1),
            new EventData(26, time_slots = 2, classRoomsNeeded = 1),
            new EventData(27, time_slots = 4, classRoomsNeeded = 1),

            new EventData(28, time_slots = 4, classRoomsNeeded = 1),
            new EventData(29, time_slots = 2, classRoomsNeeded = 1),

            //Day 5
            new EventData(30, time_slots = 4, week = Weeks.BWeek, pcRoomsNeeded = 1),
            new EventData(31, time_slots = 4, week = Weeks.AWeek, pcRoomsNeeded = 1),
            new EventData(32, time_slots = 4, labRoomsNeeded = 1),
            new EventData(33, time_slots = 4, week = Weeks.AWeek, pcRoomsNeeded = 1),
            new EventData(34, time_slots = 4, week = Weeks.BWeek, pcRoomsNeeded = 1),
            new EventData(35, time_slots = 4, week = Weeks.BWeek, pcRoomsNeeded = 1),
            new EventData(36, time_slots = 4, week = Weeks.AWeek, pcRoomsNeeded = 1),
            new EventData(37, time_slots = 4, pcRoomsNeeded = 1),
            new EventData(38, time_slots = 4, week = Weeks.AWeek, pcRoomsNeeded = 1),
            new EventData(39, time_slots = 4, week = Weeks.BWeek, pcRoomsNeeded = 1),
            new EventData(40, time_slots = 4, labRoomsNeeded = 1),
            new EventData(41, time_slots = 4, pcRoomsNeeded = 1),
            new EventData(42, time_slots = 4, labRoomsNeeded = 1),
            new EventData(43, time_slots = 4, pcRoomsNeeded = 1),

            new EventData(44, time_slots = 2, classRoomsNeeded = 1),
            new EventData(45, time_slots = 2, classRoomsNeeded = 1),
            new EventData(46, time_slots = 4, labRoomsNeeded = 1),
            new EventData(47, time_slots = 4, pcRoomsNeeded = 1),
            new EventData(48, time_slots = 4, week = Weeks.BWeek, pcRoomsNeeded = 1),
            new EventData(49, time_slots = 4, week = Weeks.AWeek, pcRoomsNeeded = 1),
            new EventData(50, time_slots = 4, week = Weeks.AWeek, pcRoomsNeeded = 1),
            new EventData(51, time_slots = 4, week = Weeks.BWeek, pcRoomsNeeded = 1),
        )

        val nEvents = 51

        val preassignedEvents = events.filter(_.relativeStart != 0)

        val precedences = List()

        val instance_data = new InstanceData(22, labRooms, classRooms, pcRooms, nEvents, events, preassignedEvents, precedences)

        val minizinc_solver = new MiniZincInstanceSolver(new MiniZincInstanceData(instance_data))

        for(line <- minizinc_solver.provisionalSolve) println(line)

    }
}
