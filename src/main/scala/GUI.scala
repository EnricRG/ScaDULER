import scalafx.application.JFXApp
import scalafx.scene.Scene

case class Resolution(x: Int = 1280, y: Int = 720)

object GUI extends App {

  def gui = {
    val resolution = Resolution()

    val gui = new JFXApp {

      stage = new JFXApp.PrimaryStage {
        title = "Window"
        scene = new Scene(resolution.x, resolution.y) {

        }
      }
    }

    gui.main(args)
    //override def main(args: Array[String]): Unit = gui.main(args)
  }
}
