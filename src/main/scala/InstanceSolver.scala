import akka.actor.Actor

trait InstanceSolver extends Actor{

    def solve(): EventSchedule
    def optimize(): EventSchedule
}
