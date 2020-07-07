package floydwarshall.gui;

import floydwarshall.executor.Edge;
import floydwarshall.executor.ExecutorInterface;
import floydwarshall.executor.PathEnds;
import floydwarshall.gravity.GravitySimulation;
import floydwarshall.gui.graphshapes.Line;
import floydwarshall.gui.graphshapes.Math;
import floydwarshall.gui.graphshapes.Node;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;

class GraphViewBase extends VBox {
    private ExecutorInterface executor;

    enum PROGRAM_STATE {
        ADD, DRAG, DELETE, ADD_LINES, DELETE_LINES
    }

    PROGRAM_STATE state = PROGRAM_STATE.ADD;
    Node dragNode = null;
    ArrayList<Line> listLines = new ArrayList<>();
    Line currentLine = null;
    boolean isChouseNodeFirstForAddLines = false;
    boolean isDragState = false;
    boolean isDeleteState = false;

    Pane pane;
    private ScrollPane scrollPane;

    ArrayList<Node> listNodes = new ArrayList<>();

    GravitySimulation gravitySimulation;
    private GravityCenterPoint gravityCenter;

    class GravityCenterPoint extends Node {
        private DoubleProperty x;
        private DoubleProperty y;

        GravityCenterPoint() {
            super(0, 0);
            x = new SimpleDoubleProperty();
            y = new SimpleDoubleProperty();
            x.addListener((observable, oldValue, newValue) -> {
                setX(newValue.doubleValue());
            });
            y.addListener((observable, oldValue, newValue) -> {
                setY(newValue.doubleValue());
            });
        }

        DoubleProperty xProperty() {
            return x;
        }

        DoubleProperty yProperty() {
            return y;
        }
    }

    GraphViewBase(ExecutorInterface executor) {
        this.executor = executor;

        gravitySimulation = new GravitySimulation();
        gravityCenter = new GravityCenterPoint();
        gravityCenter.updatePosition(getWidth() / 2, getHeight() / 2);
        gravityCenter.xProperty().bind(this.widthProperty().divide(2));
        gravityCenter.yProperty().bind(this.heightProperty().divide(2));
        gravitySimulation.setGravityCenter(gravityCenter);

        pane = new Pane();
        pane.setPrefWidth(700);
        pane.setPrefHeight(700);

        scrollPane = new ScrollPane(pane);
        scrollPane.prefWidthProperty().bind(this.widthProperty());
        scrollPane.prefHeightProperty().bind(this.heightProperty());

        ToggleButton button = new ToggleButton("add");
        button.setSelected(true);
        ToggleButton button2 = new ToggleButton("drag");
        ToggleButton button3 = new ToggleButton("delete");
        ToggleButton button4 = new ToggleButton("add line");
        ToggleButton button5 = new ToggleButton("delete line");
        ToggleGroup group = new ToggleGroup();
        button.setToggleGroup(group);
        button2.setToggleGroup(group);
        button3.setToggleGroup(group);
        button4.setToggleGroup(group);
        button5.setToggleGroup(group);
        Button random = new Button("random");

        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setState(PROGRAM_STATE.ADD);
            }
        });
        button2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setState(PROGRAM_STATE.DRAG);
            }
        });
        button3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setState(PROGRAM_STATE.DELETE);
            }
        });
        button4.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setState(PROGRAM_STATE.ADD_LINES);
            }
        });
        button5.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setState(PROGRAM_STATE.DELETE_LINES);
            }
        });
        random.setOnMouseClicked(event -> {
            RandomGraphSettingsDialog dialog = new RandomGraphSettingsDialog();
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.APPLY) {
                    setRandomGraph(dialog.getVerticesAmount(), dialog.getEdgesAmount());
                }
            });
        });

        Insets insetForButton = new Insets(0, 0, 0, 5);
        Insets insetForButtonBox = new Insets(0, 0, 5, 0);

        HBox buttonBox = new HBox(11, button, button2, button3, button4, button5, random);
        HBox.setMargin(button, insetForButton);
        buttonBox.setPadding(insetForButtonBox);
        HBox.setMargin(buttonBox, insetForButtonBox);

        getChildren().addAll(buttonBox, scrollPane);
    }

    private void setRandomGraph(int countNodes, int countEdges) {
        if (pane == null) return;

        class LocalPoint {
            private int x, y;

            private LocalPoint(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }

        if (countEdges > countNodes * (countNodes - 1)) {
            countEdges = countNodes * (countNodes - 1);
        }

        ArrayList<LocalPoint> points = new ArrayList<>();

        int spacing = 140;
        int cols = (int) java.lang.Math.ceil(java.lang.Math.sqrt(countNodes));
        int rows = (int) java.lang.Math.ceil(countNodes / (double) cols);
        int startX = (int) (this.getWidth() / 2 - spacing * cols / 2);
        int startY = (int) (this.getHeight() / 2 - spacing * rows / 2);
        int x = startX;
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                points.add(new LocalPoint(x, startY));
                x += spacing;
            }
            x = startX;
            startY += spacing;
        }


        pane.getChildren().clear();
        listLines.clear();
        listNodes.clear();
        dragNode = null;
        currentLine = null;
        isChouseNodeFirstForAddLines = false;
        isDragState = false;
        isDeleteState = false;

        for (int i = 0; i < countNodes; i++) {
            Node node = new Node(points.get(i).x, points.get(i).y);
            pane.getChildren().add(node);
            node.setIndex(listNodes.size());
            listNodes.add(node);
        }


        HashMap<Node, ArrayList<Node>> map = new HashMap<>();


        for (int i = 0; i < countNodes; i++) {
            map.put(listNodes.get(i), getListUnrelatedNodes(listNodes.get(i)));
        }


        int count = 0;
        boolean isNotOver = true;
        while (isNotOver) {
            for (Node startNode : listNodes) {
                ArrayList<Node> nodes = map.get(startNode);
                if (nodes != null && startNode != null) {
                    if (nodes.size() != 0) {
                        int randomNumber = Math.rnd(0, nodes.size() - 1);
                        Node endNode = nodes.get(randomNumber);
                        Line line = new Line(startNode.getX(), startNode.getY(),
                                startNode.getX(), startNode.getY(),
                                endNode.getX(), endNode.getY());
                        startNode.addLineStartPoint(line);
                        endNode.addLineEndPoint(line);
                        line.addObserver(this::edgeChanged);
                        setConvexOnLines(line);
                        listLines.add(line);
                        pane.getChildren().add(line);
                        line.setStartNode(startNode);
                        line.setEndNode(endNode);
                        line.setShapes();
                        int weight = Math.rnd(1, 10);
                        line.setWeight(weight);
                        line.getWeightText().setText(String.valueOf(weight));
                        pane.getChildren().add(line.getTriangle());
                        pane.getChildren().add(line.getWeightText());
                        count++;
                        nodes.remove(endNode);
                        if (count == countEdges) {
                            isNotOver = false;
                            break;
                        }
                    }
                }

            }
        }
        notifyGraphChanged();
        for (Node node : listNodes) {
            node.drawFront();
        }
    }

    void edgeChanged(Line edge) {
        notifyGraphChanged();
    }

    void notifyGraphChanged() {
        ArrayList<Edge> edges = new ArrayList<>();
        for (Line line : listLines) {
            int indexFrom = listNodes.indexOf(line.getFromPoint());
            int indexTo = listNodes.indexOf(line.getToPoint());
            if (indexFrom == -1 || indexTo == -1) {
                throw new RuntimeException("Could not find Point in ArrayList");
            }
            edges.add(new Edge(indexFrom, indexTo, line.getWeight()));
        }
        executor.setGraph(listNodes.size(), edges);
        gravitySimulation.updateAdjacencyMatrix(listNodes, listLines);

        for (Node node : listNodes) {
            node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                showPathLabels(((Node)event.getSource()).getIndex());
            });
        }
        for (Node node : listNodes) {
            node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                hidePathLabels();
            });
        }
    }

    void setConvexOnLines(Line line) {
        for (Line cur : listLines) {
            if (line.getStartX() == cur.getEndX() && line.getStartY() == cur.getEndY() &&
                    line.getEndX() == cur.getStartX() && line.getEndY() == cur.getStartY()) {
                line.setConvex(true);
                cur.setConvex(true);
                Math.setControlPoint(line, cur);
                line.updateLineShapes();
                cur.updateLineShapes();
                break;
            }
        }
    }

    private void showPathLabels(int from) {
        for (Node node : listNodes) {
            node.addPathLengthLabel(executor.getPathLength(new PathEnds(from, node.getIndex())));
        }
    }
    private void hidePathLabels() {
        for (Node node : listNodes) {
            node.removePathLengthLabel();
        }
    }

    private ArrayList<Node> getListUnrelatedNodes(Node mainNode) {
        ArrayList<Node> list = new ArrayList<>();
        for (Node node : listNodes) {
            if (node != mainNode) {
                list.add(node);
            }
        }
        return list;
    }

    private void setState (PROGRAM_STATE state){
        this.state = state;
    }

}
