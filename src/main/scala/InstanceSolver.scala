trait InstanceSolver {
    def init(instance: InstanceData): Unit
    def solve(): EventSchedule
    def optimize(): EventSchedule
}
