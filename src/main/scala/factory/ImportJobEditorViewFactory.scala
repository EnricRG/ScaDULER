package factory

import java.io.IOException

import app.FXMLPaths
import control.imprt.ImportJobEditorController
import javafx.scene.Node

//TODO
class ImportJobEditorViewFactory(importJobEditorController: ImportJobEditorController)
    extends ViewFactory[ImportJobEditorController](FXMLPaths.ModifyImportJob) {

    @throws[IOException]
    override def load(): Node ={
        throw new UnsupportedOperationException
    }

    @throws[IOException]
    @throws[UnsupportedOperationException]
    override def load(controller: ImportJobEditorController): Node = throw new UnsupportedOperationException
}
