package file.imprt.build

import java.io.File

import file.imprt.ImportReader

trait ImportReaderBuilder {
    def build: ImportReader
    def withFile(file: File): ImportReaderBuilder
}
