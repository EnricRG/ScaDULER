package model

import javafx.scene.paint

@SerialVersionUID(1L)
case class Color(r: Double, g: Double, b: Double, o: Double) extends Serializable {
    def this(color: paint.Color) = this(color.getRed, color.getGreen, color.getBlue, color.getOpacity)
    def toJFXColor: paint.Color = new paint.Color(r,g,b,o)
}
