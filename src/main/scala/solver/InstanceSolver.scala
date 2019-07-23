package solver

import akka.actor.Actor
import model.EventSchedule

trait InstanceSolver extends Actor{

    def solve(): EventSchedule
    def optimize(): EventSchedule
}
