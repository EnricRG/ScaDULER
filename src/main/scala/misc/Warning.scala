package misc

import app.AppSettings

class Warning(message: String){
    override def toString: String = AppSettings.language.getItem("warning") + ": " + message
}