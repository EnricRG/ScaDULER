package file.imprt

import build.{ImportReaderBuilder, MCFImportReaderBuilder}

object ImportReaderFactory {
    private val extensions: Set[String] = Set(MCFImportReader.MCFFileExtension)

    def fromExtension(extension: String): Option[ImportReaderBuilder] = extension match {
        case ext if ext == MCFImportReader.MCFFileExtension => Some(new MCFImportReaderBuilder)
        case _ => None
    }

    def knownExtension(extension: String): Boolean = extensions.contains(extension)
    def unknownExtension(extension: String): Boolean = !knownExtension(extension)
}
