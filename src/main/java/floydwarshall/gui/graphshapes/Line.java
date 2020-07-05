package floydwarshall.gui.graphshapes;

import floydwarshall.gravity.Edge;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Text;

public class Line extends QuadCurve implements Edge {
    private boolean isConvex = false;
    private Triangle triangle = null;
    private WeightText weightText = null;
    private Node startNode;
    private Node endNode;
    private int weight;

    public Line(double centerX, double centerY, double centerX1, double centerY1, double centerX2, double centerY2) {
        super(centerX, centerY, centerX1, centerY1, centerX2, centerY2);
        weight = 1;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isConvex() {
        return isConvex;
    }

    public void setConvex(boolean convex) {
        isConvex = convex;
    }

    public Triangle getTriangle() {
        return triangle;
    }

    public WeightText getWeightText() {
        return weightText;
    }

    public void setStartNode(Node satrtNode) {
        this.startNode = satrtNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public String getStartNodeName() {
        return startNode.getName();
    }

    public String getEndNodeName() {
        return endNode.getName();
    }

    public void updateLineShapes() {
        if (triangle != null) {
            triangle.update();
        }
        if (weightText != null) {
            weightText.update();
        }
    }

    public void setTriangle() {
        triangle = new Triangle(this);
        weightText = new WeightText(this);
    }

    // implement Edge
    public Node getFromPoint() {
        return fromNode;
    }

    public Node getToPoint() {
        return toNode;

	// class for rendering weight
    public class WeightText extends Text {
        Line line;

        WeightText(Line line) {
            super("1");
            setStyle("-fx-font: 13 arial;");
            this.line = line;
            if (!line.isConvex) {
                setX((line.getStartX() + line.getEndX()) / 2);
                setY((line.getStartY() + line.getEndY()) / 2 - 4);
            } else {
                setX(line.getControlX());
                setY(line.getControlY());
            }
        }

        void update() {
            if (!line.isConvex) {
                setX((line.getStartX() + line.getEndX()) / 2);
                setY((line.getStartY() + line.getEndY()) / 2 - 4);
            } else {
                setX(line.getControlX());
                setY(line.getControlY());
            }
        }
    }

    public boolean isStartNode(Node node) {
        return startNode == node;
    }
}
