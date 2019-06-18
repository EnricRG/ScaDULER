import scala.sys.process._

object MinizincRun extends App{
  override def main(args: Array[String]): Unit = {
    val minizinc = Process("bin/minizinc/minizinc.exe --solver Chuffed minizinc/firstModel.mzn minizinc/1er_s1.dzn")
    val output = (minizinc lineStream) toList

    output foreach(println)
  }
}
