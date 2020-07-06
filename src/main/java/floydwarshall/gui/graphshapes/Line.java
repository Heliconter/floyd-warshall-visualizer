package floydwarshall.gui.graphshapes;

import floydwarshall.gravity.Edge;
import javafx.scene.paint.Color;
import javafx.scene.shape.QuadCurve;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class Line extends QuadCurve implements Edge {
    private boolean isConvex = false;
    private Triangle triangle = null;
    private WeightText weightText = null;
    private Node startNode;
    private Node endNode;
    private int weight;

    private ArrayList<EdgeObserver> observers;

    public Line(double centerX, double centerY, double centerX1, double centerY1, double centerX2, double centerY2) {
        super(centerX, centerY, centerX1, centerY1, centerX2, centerY2);
        setWeight(1);
        setFill(null);
        setStroke(Color.BLACK);
        setStrokeWidth(1);
        observers = new ArrayList<>();
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
        notifyObservers();
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

    public void setShapes() {
        triangle = new Triangle(this);
        weightText = new WeightText(this);
    }

    // implement Edge
    public Node getFromPoint() {
        return startNode;
    }

    public Node getToPoint() {
        return endNode;
    }

    // class for rendering weight
    public class WeightText extends TextField {
        Line line;

        WeightText(Line line) {
            this.line = line;
            setText(String.valueOf(line.getWeight()));

            setPrefWidth(50);
            setStyle("-fx-background-color: transparent , transparent , transparent;"
                    + "-fx-foreground-color: transparent; -fx-font: 13 arial;");

            textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    setWeight(Integer.valueOf(getText()));
                    // TODO: remove border/color here
                } else {
                    // TODO: add border/color here
                }
            });

            setOnAction(e -> {
                if (getText().length() == 0) {
                    return;
                }
                setWeight(Integer.valueOf(getText()));
                setFocused(false);
            });

            update();
        }

        void update() {
            if (!line.isConvex) {
                setLayoutX((line.getStartX() + line.getEndX()) / 2);
                setLayoutY((line.getStartY() + line.getEndY()) / 2 - 4);
            } else {
                setLayoutX(line.getControlX());
                setLayoutY(line.getControlY());
            }
        }
    }

    public boolean isStartNode(Node node) {
        return startNode == node;
    }

    public void addObserver(EdgeObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        if (observers == null) {
            return;
        }

        for (EdgeObserver observer : observers) {
            observer.edgeChanged(this);
        }
    }
}
