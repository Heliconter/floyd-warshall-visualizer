package floydwarshall.gui.graphshapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;

import java.util.ArrayList;

import floydwarshall.gravity.Point;

public class Node implements Point {

    private Ellipse ellipse;
    private ArrayList<Line> linesStartPoint;
    private ArrayList<Line> linesEndPoint;
    private Text text;
    public static final int radius = 10;

    public Node(double centerX, double centerY) {
        ellipse = new Ellipse();
        ellipse.setCenterX(centerX);
        ellipse.setCenterY(centerY);
        ellipse.setRadiusX(radius);
        ellipse.setRadiusY(radius);
        ellipse.setFill(Color.SNOW);
        ellipse.setStroke(Color.BLACK);
        text = new Text(centerX,centerY,"A");
        text.setX(centerX-4);   // для выравнивания
        text.setY(centerY+3);   // для выравнивания
        linesStartPoint = new ArrayList<>();
        linesEndPoint = new ArrayList<>();
    }

    public void updatePosition(double centerX, double centerY){
        ellipse.setCenterX(centerX);
        ellipse.setCenterY(centerY);
        text.setX(centerX-4);   // для выравнивания
        text.setY(centerY+3);   // для выравнивания
        for (Line line : linesStartPoint) {
            line.setStartX(centerX);
            line.setStartY(centerY);
            line.updateTriangle();
            if (!line.isConvex()) {
                line.setControlX(centerX);
                line.setControlY(centerY);
            }else {
                Math.setControlPointForOnlyLine(line);
            }
        }
        for (Line line : linesEndPoint) {
            line.setEndX(centerX);
            line.setEndY(centerY);
            line.updateTriangle();
            if (line.isConvex()){
                Math.setControlPointForOnlyLine(line);
            }
        }
    }

    public Ellipse getEllipse() {
        return ellipse;
    }

    public Text getText() {
        return text;
    }

    public ArrayList<Line> getLinesStartPoint() {
        return linesStartPoint;
    }

    public ArrayList<Line> getLinesEndPoint() {
        return linesEndPoint;
    }

    public void addLineStartPoint(Line line){
        linesStartPoint.add(line);
    }

    public void addLineEndPoint(Line line){
        linesEndPoint.add(line);
    }

    public void popLineStartPoint(Line line) {
        if (line!=null) {
            linesStartPoint.remove(line);
        }
    }

    public void popLineEndPoint(Line line){
        if (line!=null) {
            linesEndPoint.remove(line);
        }
    }

   /* public void deleteNode(GraphView group){
        group.getChildren().remove(ellipse);
        group.getChildren().remove(text);
        for (MyLine line : linesStartPoint) {
            group.getChildren().remove(line);
            group.getChildren().remove(line.getTriangle());
        }
        for (MyLine line : linesEndPoint) {
            group.getChildren().remove(line);
            group.getChildren().remove(line.getTriangle());

        }
    }*/

    public void setName(String name){
        text.setText(name);
    }

    public void drawFront(){
        ellipse.toFront();
        text.toFront();
    }

    // implement Point
    private double dx;
    private double dy;
    private boolean affectedByGravity = true;

    @Override
    public double getX() {
        return ellipse.getCenterX();
    }

    @Override
    public double getY() {
        return ellipse.getCenterY();
    }

    @Override
    public double getDX() {
        return dx;
    }

    @Override
    public double getDY() {
        return dy;
    }

    @Override
    public boolean getAffectedByGravity() {
        return affectedByGravity;
    }

    @Override
    public void setX(double value) {
        updatePosition(value, ellipse.getCenterY());
    }

    @Override
    public void setY(double value) {
        updatePosition(ellipse.getCenterX(), value);
    }

    @Override
    public void setDX(double value) {
        dx = value;
    }

    @Override
    public void setDY(double value) {
        dy = value;
    }

    public void setAffectedByGravity(boolean value) {
        affectedByGravity = value;
    }
}
