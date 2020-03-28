package misc

trait Selection

object Selection{
    case object Modify extends Selection
    case object Finish extends Selection
    case object Cancel extends Selection
    case object Accept extends Selection
    case object Reject extends Selection

    //For Java interoperability
    def ModifyOption: Selection = Modify
    def FinishOption: Selection = Finish
    def CancelOption: Selection = Cancel
}

