package file.in

trait ImportReader {
    def read: ImportReader
    def getImportJob: ImportJob
}
