package floydwarshall.gui.graphshapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;

import floydwarshall.gravity.Point;

public class Node extends StackPane implements Point {

    private Ellipse ellipse;
    private ArrayList<Line> linesStartPoint;
    private ArrayList<Line> linesEndPoint;
    private Text text;
    public static final int radius = 15;

    public Node(double centerX, double centerY) {
        ellipse = new Ellipse();
        ellipse.setRadiusX(radius);
        ellipse.setRadiusY(radius);
        ellipse.setFill(Color.SNOW);
        ellipse.setStroke(Color.BLACK);
        text = new Text("A");
        text.setFont(new Font(20));
        this.getChildren().addAll(ellipse, text);
        linesStartPoint = new ArrayList<>();
        linesEndPoint = new ArrayList<>();
        updatePosition(centerX, centerY);
    }

    public void updatePosition(double centerX, double centerY){
        setLayoutX(centerX - radius);
        setLayoutY(centerY - radius);
        for (Line line : linesStartPoint) {
            line.setStartX(centerX);
            line.setStartY(centerY);
            line.updateLineShapes();
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
            line.updateLineShapes();
            if (line.isConvex()){
                Math.setControlPointForOnlyLine(line);
            }
        }
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

    public Ellipse getEllipse() {
        return ellipse;
    }

    public void setName(String name){
        text.setText(name);
    }
    public String getName(){
        return text.getText();
    }

    public void drawFront(){
        toFront();
    }

    // implement Point
    private double dx;
    private double dy;
    private boolean affectedByGravity = true;

    @Override
    public double getX() {
        return getLayoutX() + radius;
    }

    @Override
    public double getY() {
        return getLayoutY() + radius;
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
        updatePosition(value, getLayoutY() + radius);
    }

    @Override
    public void setY(double value) {
        updatePosition(getLayoutX() + radius, value);
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
