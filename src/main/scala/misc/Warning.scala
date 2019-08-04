package misc

import app.AppSettings

class Warning(message: String){
    override def toString = AppSettings.language.getItem("warning") + ": " + message
}