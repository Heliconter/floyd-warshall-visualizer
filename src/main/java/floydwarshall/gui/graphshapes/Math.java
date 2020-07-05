package floydwarshall.gui.graphshapes;

import javafx.geometry.Point2D;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;


public class Math {

    public static boolean isDeleteLine(Line line, double x, double y) {

        Ellipse ellipse = new Ellipse();
        ellipse.setRadiusX(1);
        ellipse.setRadiusY(1);
        ellipse.setCenterX(x);
        ellipse.setCenterY(y);

        return Shape.intersect(line, ellipse).getBoundsInLocal().getWidth() != -1;
    }

    public static boolean isEditWeight(Text text, double x, double y) {

        Ellipse ellipse = new Ellipse();
        ellipse.setRadiusX(50);
        ellipse.setRadiusY(50);
        ellipse.setCenterX(x);
        ellipse.setCenterY(y);

        return Shape.intersect(ellipse, text).getBoundsInLocal().getWidth() != -1;
    }

    public static void setControlPoint(Line line1, Line line2) {
        double middlePointX = (line1.getStartX() + line1.getEndX()) / 2; //середина
        double middlePointY = (line1.getStartY() + line1.getEndY()) / 2;

        double quarterPointX = (middlePointX + line1.getEndX()) / 2;//четверть
        double quarterPointY = (middlePointY + line1.getEndY()) / 2;

        double rotatablePointX = (middlePointX + quarterPointX) / 2;//0.125
        double rotatablePointY = (middlePointY + quarterPointY) / 2;


        double newX = (rotatablePointX - middlePointX) * java.lang.Math.cos(90) - (rotatablePointY - middlePointY) * java.lang.Math.sin(90) + middlePointX;
        double newY = (rotatablePointX - middlePointX) * java.lang.Math.sin(90) + (rotatablePointY - middlePointY) * java.lang.Math.cos(90) + middlePointY;

        line1.setControlX(newX);
        line1.setControlY(newY);

        newX = (rotatablePointX - middlePointX) * java.lang.Math.cos(-90) - (rotatablePointY - middlePointY) * java.lang.Math.sin(-90) + middlePointX;
        newY = (rotatablePointX - middlePointX) * java.lang.Math.sin(-90) + (rotatablePointY - middlePointY) * java.lang.Math.cos(-90) + middlePointY;

        line2.setControlX(newX);
        line2.setControlY(newY);
    }

    public static void setControlPointForOnlyLine(Line line) {
        double middlePointX = (line.getStartX() + line.getEndX()) / 2; //середина
        double middlePointY = (line.getStartY() + line.getEndY()) / 2;

        double quarterPointX = (middlePointX + line.getEndX()) / 2;//четверть
        double quarterPointY = (middlePointY + line.getEndY()) / 2;

        double rotatablePointX = (middlePointX + quarterPointX) / 2;//0.125
        double rotatablePointY = (middlePointY + quarterPointY) / 2;

        double radian = 90;

        double newX = (rotatablePointX - middlePointX) * java.lang.Math.cos(radian) - (rotatablePointY - middlePointY) * java.lang.Math.sin(radian) + middlePointX;
        double newY = (rotatablePointX - middlePointX) * java.lang.Math.sin(radian) + (rotatablePointY - middlePointY) * java.lang.Math.cos(radian) + middlePointY;
        line.setControlX(newX);
        line.setControlY(newY);
    }

    public static Point2D rotateAroundPoint(Point2D point, Point2D pointCenterCoordinates, double radian) {
        double X = (point.getX() - pointCenterCoordinates.getX()) * java.lang.Math.cos(radian) - (point.getY() - pointCenterCoordinates.getY()) * java.lang.Math.sin(radian) + pointCenterCoordinates.getX();
        double Y = (point.getX() - pointCenterCoordinates.getX()) * java.lang.Math.sin(radian) + (point.getY() - pointCenterCoordinates.getY()) * java.lang.Math.cos(radian) + pointCenterCoordinates.getY();

        return new Point2D(X,Y);
    }
}
