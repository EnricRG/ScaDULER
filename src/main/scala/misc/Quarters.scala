package misc

import app.AppSettings

object Quarters{
    sealed abstract class Quarter{
        def toString: String
    }

    object FirstQuarter extends Quarter{
        override def toString = AppSettings.language.getItem("firstQuarter")
    }

    object SecondQuarter extends Quarter{
        override def toString = AppSettings.language.getItem("secondQuarter")
    }
}