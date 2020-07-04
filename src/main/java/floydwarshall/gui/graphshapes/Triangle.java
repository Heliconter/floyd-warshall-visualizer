package floydwarshall.gui.graphshapes;


import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;


public class Triangle extends Polygon {
    private Line line;

    public Triangle(Line line) {
        this.line = line;
        calculationСoordinatesTriangle();

   /*     double distanceX = line.getStartX() - line.getEndX();
        double distanceY = line.getStartY() - line.getEndY();
        double distance = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        double deltaX = 10 * distanceX / distance;
        double deltaY = 10 * distanceY / distance;*/

       /* start_triangle_point_1 = new Point2D(line.getEndX(), line.getEndY());
        start_triangle_point_2 = new Point2D(line.getEndX() - 20, line.getEndY() + 5);
        start_triangle_point_3 = new Point2D(line.getEndX() - 20, line.getEndY() - 5);
        double angle = Math.toDegrees(Math.atan(distanceY / distanceX));*/

       /* System.out.println("start X: " + line.getStartX() + " end X: " + line.getEndX());
        System.out.println("start Y: " + line.getStartY() + " end Y: " + line.getEndY());
        System.out.println("distanceX "+distanceX);
        System.out.println("distanceY "+distanceY);
        System.out.println("deltaX "+deltaX);
        System.out.println("deltaY "+deltaY);*/


        //angle = 0; //вниз(-90) вверх (90) влево (0)
//        System.out.println("Angle " + angle);

       /* if (distanceX > 0) {
            angle += 180;
        }



        angle = Math.toRadians(angle);
        start_triangle_point_2 = MyMath.rotateAroundPoint(start_triangle_point_2, new Point2D(line.getEndX(), line.getEndY()), angle);
        start_triangle_point_3 = MyMath.rotateAroundPoint(start_triangle_point_3, new Point2D(line.getEndX(), line.getEndY()), angle);
        this.getPoints().addAll(new Double[]{start_triangle_point_1.getX() + deltaX, start_triangle_point_1.getY() + deltaY,
                start_triangle_point_2.getX() + deltaX, start_triangle_point_2.getY() + deltaY,
                start_triangle_point_3.getX() + deltaX, start_triangle_point_3.getY() + deltaY});*/
    }

    public void update() {
        calculationСoordinatesTriangle();
    }

    private void calculationСoordinatesTriangle() {
        double distanceX = line.getControlX() - line.getEndX();
        double distanceY = line.getControlY() - line.getEndY();
        double distance = java.lang.Math.sqrt(java.lang.Math.pow(distanceX, 2) + java.lang.Math.pow(distanceY, 2));
        double deltaX = Node.radius * distanceX / distance;
        double deltaY = Node.radius * distanceY / distance;
        double angle = java.lang.Math.toDegrees(java.lang.Math.atan(distanceY / distanceX));

        Point2D start_triangle_point_1 = new Point2D(line.getEndX(), line.getEndY());
        Point2D start_triangle_point_2 = new Point2D(line.getEndX() - 20, line.getEndY() + 5); // 20 и 5 задают размерность стрелки
        Point2D start_triangle_point_3 = new Point2D(line.getEndX() - 20, line.getEndY() - 5);
        if (distanceX > 0) {
            angle += 180;
        }
        angle = java.lang.Math.toRadians(angle);
        start_triangle_point_2 = Math.rotateAroundPoint(start_triangle_point_2, new Point2D(line.getEndX(), line.getEndY()), angle);
        start_triangle_point_3 = Math.rotateAroundPoint(start_triangle_point_3, new Point2D(line.getEndX(), line.getEndY()), angle);
        getPoints().clear();
        getPoints().addAll(start_triangle_point_1.getX() + deltaX, start_triangle_point_1.getY() + deltaY,
                start_triangle_point_2.getX() + deltaX, start_triangle_point_2.getY() + deltaY,
                start_triangle_point_3.getX() + deltaX, start_triangle_point_3.getY() + deltaY);
    }

}
