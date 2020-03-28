package file.imprt

trait ImportType

object ImportType{
    case object UnspecifiedImport extends ImportType
    case object MCFImport extends ImportType

    //for Java interoperability
    def MCFImportType: ImportType = MCFImport
    def UnspecifiedImportType: ImportType = UnspecifiedImport
}
