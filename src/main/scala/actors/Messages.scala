package actors

import solver.{EventAssignment, NewInstanceData}

object Messages{
    abstract class Message
    //abstract class Criterion

    case class SolveRequest(data: NewInstanceData, timeout: Double) extends Message
    //case class Optimize(data: NewInstanceData, criterion: Criterion) extends Message
    case object Stop extends Message
    //case class Stop(reason: String) extends Message
    case class Solution(assignments: Option[Iterable[EventAssignment]]) extends Message
}