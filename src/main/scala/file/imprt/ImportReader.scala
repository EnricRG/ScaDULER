package file.imprt

trait ImportReader {
    def read: ImportReader
    def getImportJob: ImportJob
}
