import scalafx.application.JFXApp
import scalafx.scene.Scene

object RESOLUTION {
  val x = 1280
  val y = 720
}

object GUI extends App {

  val gui = new JFXApp {
    stage = new JFXApp.PrimaryStage{
      title = "Window"
      scene = new Scene(RESOLUTION.x, RESOLUTION.y){

      }
    }
  }

  gui.main(args)
  //override def main(args: Array[String]): Unit = gui.main(args)
}
