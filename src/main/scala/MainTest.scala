object MainTest extends App{
  override def main(args: Array[String]): Unit = {
    var x = new EventData(3)
    val y = new EventData(5)

    println(x.event_number, " ", y.event_number)

    x.event_number = 2;
    y.event_number = 4;

    println(x.event_number, " ", y.event_number)
  }
}
