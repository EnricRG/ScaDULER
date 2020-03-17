package file.imprt

import model.blueprint.ResourceBlueprint

import scala.util.Try

trait ResourceImporter {
    def getResourceBlueprints: Try[Iterable[ResourceBlueprint]]
}
