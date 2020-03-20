package file.out

import java.io.IOException

import exception.FileFormatException
import model.blueprint.ResourceBlueprint

trait ResourceExporter {
    @throws(classOf[IOException])
    def writeResourceBlueprints(blueprints: Iterable[ResourceBlueprint]): Unit
}
