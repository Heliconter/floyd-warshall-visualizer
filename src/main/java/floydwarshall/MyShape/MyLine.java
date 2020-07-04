package floydwarshall.MyShape;

import javafx.scene.shape.QuadCurve;

public class MyLine extends QuadCurve {
    private boolean isConvex = false;
    private Triangle triangle = null;

    public Triangle getTriangle() {
        return triangle;
    }

    public MyLine(double centerX, double centerY, double centerX1, double centerY1, double centerX2, double centerY2) {
        super(centerX, centerY, centerX1, centerY1, centerX2, centerY2);
    }

    public boolean isConvex() {
        return isConvex;
    }

    public void setConvex(boolean convex) {
        isConvex = convex;
    }

    public void updateTriangle() {
        if (triangle != null) {
            triangle.update();
        }
    }

    public void setTriangle() {
        triangle = new Triangle(this);
    }

}
