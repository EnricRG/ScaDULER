package misc

import app.AppSettings

class Warning(message: String){
    override def toString = AppSettings.Language.getItem("warning") + ": " + message
}