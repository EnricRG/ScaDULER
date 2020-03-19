package file.in

import java.io.IOException

import exception.FileFormatException
import model.blueprint.ResourceBlueprint

trait ResourceImporter {
    @throws(classOf[IOException])
    @throws(classOf[FileFormatException])
    def readResourceBlueprints: Iterable[ResourceBlueprint]
}
