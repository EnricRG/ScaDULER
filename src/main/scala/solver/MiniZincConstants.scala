package solver

import app.AppSettings

object MiniZincConstants {
    val MiniZincPath = "./bin/minizinc/minizinc"
    val MiniZincModel = "minizinc/finalModel.mzn"
    val PredefinedDZNFilePath: String = AppSettings.tempPath +  "data.dzn"
    val CommandLineStatisticsOption = "-s --compiler-statistics"
    val CommandLineDataOption = "-D"
    val ChuffedSolver = "--solver Chuffed"
    val EqualsSign = "="
    val SemiColon = ";"
}
