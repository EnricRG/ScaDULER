package actors

import akka.actor.Actor
import solver.EventSchedule

trait InstanceSolver extends Actor{

    def solve(): EventSchedule
    def optimize(): EventSchedule
}
