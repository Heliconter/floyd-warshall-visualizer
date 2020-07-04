package floydwarshall.MyShape;

import javafx.geometry.Point2D;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;


public class MyMath {

    public static boolean isDeleteLine(MyLine myLine, double x, double y) {

        Ellipse ellipse = new Ellipse();
        ellipse.setRadiusX(1);
        ellipse.setRadiusY(1);
        ellipse.setCenterX(x);
        ellipse.setCenterY(y);

        return Shape.intersect(myLine, ellipse).getBoundsInLocal().getWidth() != -1;
    }

    public static void setControlPoint(MyLine myLine1 , MyLine myLine2) {
        double middlePointX = (myLine1.getStartX() + myLine1.getEndX()) / 2; //середина
        double middlePointY = (myLine1.getStartY() + myLine1.getEndY()) / 2;

        double quarterPointX = (middlePointX + myLine1.getEndX()) / 2;//четверть
        double quarterPointY = (middlePointY + myLine1.getEndY()) / 2;

        double rotatablePointX = (middlePointX + quarterPointX) / 2;//0.125
        double rotatablePointY = (middlePointY + quarterPointY) / 2;


        double newX = (rotatablePointX - middlePointX) * Math.cos(90) - (rotatablePointY - middlePointY) * Math.sin(90) + middlePointX;
        double newY = (rotatablePointX - middlePointX) * Math.sin(90) + (rotatablePointY - middlePointY) * Math.cos(90) + middlePointY;

        myLine1.setControlX(newX);
        myLine1.setControlY(newY);

        newX = (rotatablePointX - middlePointX) * Math.cos(-90) - (rotatablePointY - middlePointY) * Math.sin(-90) + middlePointX;
        newY = (rotatablePointX - middlePointX) * Math.sin(-90) + (rotatablePointY - middlePointY) * Math.cos(-90) + middlePointY;

        myLine2.setControlX(newX);
        myLine2.setControlY(newY);
    }

    public static void setControlPointForOnlyLine(MyLine line) {
        double middlePointX = (line.getStartX() + line.getEndX()) / 2; //середина
        double middlePointY = (line.getStartY() + line.getEndY()) / 2;

        double quarterPointX = (middlePointX + line.getEndX()) / 2;//четверть
        double quarterPointY = (middlePointY + line.getEndY()) / 2;

        double rotatablePointX = (middlePointX + quarterPointX) / 2;//0.125
        double rotatablePointY = (middlePointY + quarterPointY) / 2;

        double radian = 90;

        double newX = (rotatablePointX - middlePointX) * Math.cos(radian) - (rotatablePointY - middlePointY) * Math.sin(radian) + middlePointX;
        double newY = (rotatablePointX - middlePointX) * Math.sin(radian) + (rotatablePointY - middlePointY) * Math.cos(radian) + middlePointY;
        line.setControlX(newX);
        line.setControlY(newY);
    }

    public static Point2D rotateAroundPoint(Point2D point, Point2D pointCenterCoordinates, double radian) {
        double X = (point.getX() - pointCenterCoordinates.getX()) * Math.cos(radian) - (point.getY() - pointCenterCoordinates.getY()) * Math.sin(radian) + pointCenterCoordinates.getX();
        double Y = (point.getX() - pointCenterCoordinates.getX()) * Math.sin(radian) + (point.getY() - pointCenterCoordinates.getY()) * Math.cos(radian) + pointCenterCoordinates.getY();

        return new Point2D(X,Y);
    }
}
