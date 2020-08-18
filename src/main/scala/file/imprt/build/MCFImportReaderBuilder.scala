package file.imprt.build

import java.io.File

import file.imprt.MCFImportReader

class MCFImportReaderBuilder extends ImportReaderBuilder {
    private var file: File = _

    def build: MCFImportReader = new MCFImportReader(file)

    def withFile(file: File): MCFImportReaderBuilder = {
        this.file = file
        this
    }
}
