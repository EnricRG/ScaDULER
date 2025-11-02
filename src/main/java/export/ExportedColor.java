package export;

import model.Color;

/**
 * @author EnricRG
 */
public class ExportedColor {
    private Double r;
    private Double g;
    private Double b;
    private Double o;

    public ExportedColor(Color color) {
        this.r = color.r();
        this.g = color.g();
        this.b = color.b();
        this.o = color.o();
    }

    public Color toColor() {
        return new Color(this.r, this.g, this.b, this.o);
    }

    public Double getR() {
        return r;
    }

    public void setR(Double r) {
        this.r = r;
    }

    public Double getG() {
        return g;
    }

    public void setG(Double g) {
        this.g = g;
    }

    public Double getB() {
        return b;
    }

    public void setB(Double b) {
        this.b = b;
    }

    public Double getO() {
        return o;
    }

    public void setO(Double o) {
        this.o = o;
    }
}
