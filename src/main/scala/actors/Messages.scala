package actors

import solver.{EventAssignment, NewInstanceData, NewMiniZincInstanceData}

object Messages{
    abstract class Message
    //abstract class Criterion

    case class SolveRequest(data: NewInstanceData, timeout: Double) extends Message
    //case class Optimize(data: NewInstanceData, criterion: Criterion) extends Message
    case object Stop extends Message
    //case class Stop(reason: String) extends Message
    case object NoSolution extends Message
    case class Solution(assignments: Iterable[EventAssignment]) extends Message

    object MiniZincMessages{
        case class MiniZincSolveRequest(data: NewMiniZincInstanceData)
    }
}