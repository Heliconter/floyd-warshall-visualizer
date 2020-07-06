package floydwarshall.gui;

import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;
import floydwarshall.executor.ExecutorInterface;
import floydwarshall.executor.Edge;
import floydwarshall.gravity.GravitySimulation;
import floydwarshall.gui.graphshapes.Line;
import floydwarshall.gui.graphshapes.Math;
import floydwarshall.gui.graphshapes.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GraphView extends VBox {
    ExecutorInterface executor;

    enum PROGRAM_STATE {
        ADD, DRAG, DELETE, ADD_LINES, DELETE_LINES, EDIT
    }

    private PROGRAM_STATE state = PROGRAM_STATE.ADD;
    private Node dragNode = null;
    private ArrayList<Line> listLines = new ArrayList<>();
    private Line currentLine = null;
    private boolean isChouseNodeFirstForAddLines = false;
    private boolean isDragState = false;
    private boolean isDeleteState = false;

    private Pane pane;
    private ScrollPane scrollPane;

    private ArrayList<Node> listNodes = new ArrayList<>();

    private Label label;
    private TextField textField;
    private Button updateButton;
    private Line editLine = null;

    private GravitySimulation gravitySimulation;
    private GravityCenterPoint gravityCenter;

    class GravityCenterPoint extends Node {
        private DoubleProperty x;
        private DoubleProperty y;

        public GravityCenterPoint() {
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

        public DoubleProperty xProperty() {
            return x;
        }

        public DoubleProperty yProperty() {
            return y;
        }
    }

    public GraphView(ExecutorInterface executor) {
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
        ToggleButton button6 = new ToggleButton("edit");
        ToggleGroup group = new ToggleGroup();
        button.setToggleGroup(group);
        button2.setToggleGroup(group);
        button3.setToggleGroup(group);
        button4.setToggleGroup(group);
        button5.setToggleGroup(group);
        button6.setToggleGroup(group);
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
        button6.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setState(PROGRAM_STATE.EDIT);
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
        Insets insetForInformBox = new Insets(10, 0, 0, 0);
        Insets insetForLabel = new Insets(3, 0, 0, 0);
        Insets insetForTextField = new Insets(0, 0, 0, 30);


        HBox buttonBox = new HBox(11, button, button2, button3, button4, button5, button6, random);
        HBox.setMargin(button, insetForButton);
        buttonBox.setPadding(insetForButtonBox);
        HBox.setMargin(buttonBox, insetForButtonBox);


        label = new Label("");
        label.setPrefWidth(180);
        label.setVisible(false);

        textField = new TextField();
        textField.setPrefWidth(50);
        textField.setVisible(false);
        textField.setStyle("-fx-background-color: transparent , transparent , transparent;");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                if (state == PROGRAM_STATE.EDIT) {
                    String newWeight = textField.getText();
                    editLine.getWeightText().setText(newWeight);
                    editLine.setWeight(Integer.valueOf(newWeight));
                    setTextOnLabel(editLine.getStartNodeName(), editLine.getEndNodeName(), editLine.getWeightText().getText());
                    notifyGraphChanged();
                }
            }
        });
        textField.setOnAction(e -> {
            if (state == PROGRAM_STATE.EDIT) {
                String newWeight = textField.getText();
                editLine.getWeightText().setText(newWeight);
                editLine.setWeight(Integer.valueOf(newWeight));
                setTextOnLabel(editLine.getStartNodeName(), editLine.getEndNodeName(), editLine.getWeightText().getText());
                notifyGraphChanged();
            }
        });


        HBox inform = new HBox(label, textField);
        inform.setPadding(insetForInformBox);
        HBox.setMargin(label, insetForLabel);
        HBox.setMargin(textField, insetForTextField);


        getChildren().addAll(buttonBox, scrollPane, inform);

        pane.setOnMouseClicked((MouseEvent event) -> {
            if (event.isControlDown() && state == PROGRAM_STATE.DRAG) {
                Node node = findDragEllipse(event.getX(), event.getY());
                if (node != null) {
                    node.setAffectedByGravity(!node.getAffectedByGravity());
                }
            }
        });

        pane.setOnMousePressed((MouseEvent event) ->
        {
            if (event.isControlDown()) {
                return;
            }

            if (state == PROGRAM_STATE.ADD) {
                if (listNodes.size() <= 25) {
                    Node node = new Node(event.getX(), event.getY());
                    pane.getChildren().add(node);
                    node.setName(getNodeName());
                    listNodes.add(node);
                    notifyGraphChanged();
                }
            }
            if (state == PROGRAM_STATE.DELETE) {
                if (listNodes.size() > 0) {
                    Node node = findDragEllipse(event.getX(), event.getY());
                    if (node != null) {
                        deleteNode(node);
                        notifyGraphChanged();
                    }
                }
            }
            if (state == PROGRAM_STATE.ADD_LINES) {
                if (!isChouseNodeFirstForAddLines) {
                    Node node = findDragEllipse(event.getX(), event.getY());
                    if (node != null) {
                        Line line = new Line(node.getX(), node.getY(),
                                node.getX(), node.getY(),
                                node.getX(), node.getY());
                        line.setFill(null);
                        line.setStroke(Color.BLACK);
                        line.setStrokeWidth(1);
                        line.setStartNode(node);
                        node.addLineStartPoint(line);
                        pane.getChildren().add(line);
                        currentLine = line;
                        isChouseNodeFirstForAddLines = true;
                        node.drawFront();
                    }
                }
            }
            if (state == PROGRAM_STATE.DRAG) {
                if (!isDragState) {
                    if (listNodes.size() > 0) {
                        Node node = findDragEllipse(event.getX(), event.getY());
                        if (node != null) {
                            dragNode = node;
                            isDragState = true;
                            dragNode.setAffectedByGravity(false);
                        }
                    }
                }
            }
            if (state == PROGRAM_STATE.DELETE_LINES) {
                if (!isDeleteState) {
                    isDeleteState = true;
                }
            }
            if (state == PROGRAM_STATE.EDIT) {
                for (Line line : listLines) {
                    if (Math.isEditWeight(line.getWeightText(), event.getSceneX(), event.getSceneY())) {
                        setTextOnLabel(line.getStartNodeName(), line.getEndNodeName(), line.getWeightText().getText());
                        textField.setText(line.getWeightText().getText());
                        editLine = line;
                        break;
                    }
                }
            }

        });

        pane.setOnMouseDragged((MouseEvent event) -> {
            if (state == PROGRAM_STATE.DRAG) {

                if (isDragState && dragNode != null) {
                    dragNode.updatePosition(event.getX(), event.getY());
                }
            }
            if (state == PROGRAM_STATE.ADD_LINES) {
                if (isChouseNodeFirstForAddLines && currentLine != null) {
                    updateLineEndPoint(event.getX(), event.getY(), currentLine);
                }
            }
            if (state == PROGRAM_STATE.DELETE_LINES) {
                if (listLines.size() > 0) {
                    double x = event.getSceneX();
                    double y = event.getSceneY();
                    ArrayList<Line> deleteLine = new ArrayList<>();
                    for (Line line : listLines) {
                        if (Math.isDeleteLine(line, x, y)) {
                            deleteLine.add(line);
                        }
                    }
                    for (Line line : deleteLine) {
                        pane.getChildren().remove(line);
                        pane.getChildren().remove(line.getTriangle());
                        pane.getChildren().remove(line.getWeightText());
                        listLines.remove(line);
                        Node node1 = findDragEllipse(line.getStartX(), line.getStartY());
                        Node node2 = findDragEllipse(line.getEndX(), line.getEndY());
                        if (node1 != null) {
                            node1.popLineStartPoint(line);
                        }
                        if (node2 != null) {
                            node2.popLineEndPoint(line);
                        }
                        if (line.isConvex()) {
                            Line inverse = isIverseLine(line);
                            if (inverse != null) {
                                inverse.setControlX(inverse.getStartX());
                                inverse.setControlY(inverse.getStartY());
                                inverse.setConvex(false);
                                inverse.updateLineShapes();
                            }
                        }
                        notifyGraphChanged();
                    }
                }
            }
        });

        pane.setOnMouseReleased((MouseEvent event) -> {
            if (state == PROGRAM_STATE.ADD_LINES) {
                if (isChouseNodeFirstForAddLines) {
                    if (listNodes.size() > 0) {
                        Node node = findDragEllipse(event.getX(), event.getY());
                        if (node != null && !isDublicateLine(currentLine, new Point2D(node.getX(), node.getY()))
                                && !currentLine.isStartNode(node)) {
                            node.addLineEndPoint(currentLine);
                            updateLineEndPoint(node.getX(), node.getY(), currentLine);
                            currentLine.setShapes();
                            setConvexOnLines(currentLine);
                            listLines.add(currentLine);
                            currentLine.setEndNode(node);
                            pane.getChildren().add(currentLine.getTriangle());
                            pane.getChildren().add(currentLine.getWeightText());
                            currentLine = null;
                            isChouseNodeFirstForAddLines = false;
                            node.drawFront();
                            notifyGraphChanged();
                        } else {
                            pane.getChildren().remove(currentLine);
                            currentLine = null;
                            isChouseNodeFirstForAddLines = false;
                        }
                    }
                }
            }
            if (state == PROGRAM_STATE.DRAG) {
                if (isDragState) {
                    dragNode.setAffectedByGravity(true);
                    dragNode = null;
                    isDragState = false;
                }
            }
            if (state == PROGRAM_STATE.DELETE_LINES) {
                if (isDeleteState) {
                    isDeleteState = false;
                }
            }
        });

        // set up timer for gravity updates
        Timeline timer = new Timeline(
                new KeyFrame(Duration.millis(1000 / 30), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        gravitySimulation.simulationStep(listNodes);
                    }
                }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void notifyGraphChanged() {
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
    }

    private void deleteNode(Node node) {
        pane.getChildren().remove(node);
        for (Line line : node.getLinesStartPoint()) {
            pane.getChildren().remove(line);
            pane.getChildren().remove(line.getTriangle());
            pane.getChildren().remove(line.getWeightText());
            listLines.remove(line);
        }
        for (Line line : node.getLinesEndPoint()) {
            pane.getChildren().remove(line);
            pane.getChildren().remove(line.getTriangle());
            pane.getChildren().remove(line.getWeightText());
            listLines.remove(line);
        }
        listNodes.remove(node);
        for (int i = 0; i < listNodes.size(); i++) {
            listNodes.get(i).setName(String.valueOf((char) ('A' + i)));
        }
    }

    private void updateLineEndPoint(double end_x,
                                    double end_y,
                                    Line line) {
        line.setEndX(end_x);
        line.setEndY(end_y);
    }

    private Node findDragEllipse(double x, double y) {
        Node node = null;
        double min = 1000000;
        for (Node el : listNodes) {
            double localMin = (java.lang.Math.abs(el.getX() - x) + java.lang.Math.abs(el.getY() - y));
            if (localMin < min) {
                min = localMin;
                node = el;
            }
        }
        if (min < 30) {
            return node;
        } else {
            return null;
        }
    }

    private void setConvexOnLines(Line line) {
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

    private boolean isDublicateLine(Line line, Point2D point) {

        for (Line cur : listLines) {
            if (cur.getStartX() == line.getStartX() && cur.getStartY() == line.getStartY() &&
                    cur.getEndX() == point.getX() && cur.getEndY() == point.getY()) {
                return true;
            }
        }

        return false;
    }

    private Line isIverseLine(Line line) {
        for (Line cur : listLines) {
            if (cur.getStartX() == line.getEndX() && cur.getStartY() == line.getEndY() &&
                    cur.getEndX() == line.getStartX() && cur.getEndY() == line.getStartY()) {
                return cur;
            }
        }
        return null;
    }

    private void setTextOnLabel(String startNodeName, String endNodeName, String weight) {
        String string = "Node {" + startNodeName + "} -> Node {" + endNodeName + "} Weight: " + weight;
        label.setText(string);
    }

    private String getNodeName() {
        return String.valueOf((char) ('A' + listNodes.size()));
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
        hideEditElements();

        for (int i = 0; i < countNodes; i++) {
            Node node = new Node(points.get(i).x, points.get(i).y);
            pane.getChildren().add(node);
            node.setName(getNodeName());
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

    private void hideEditElements() {
        if (label != null) {
            label.setVisible(false);
        }
        if (textField != null) {
            textField.setVisible(false);
        }
    }

    private void showEditElements() {
        if (label != null) {
            label.setVisible(true);
        }
        if (textField != null) {
            textField.setVisible(true);
        }
    }

    ArrayList<Node> getListUnrelatedNodes(Node mainNode) {
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
        if (state != PROGRAM_STATE.EDIT){
            hideEditElements();
        }else {
            showEditElements();
        }
    }

    private boolean isDigitString(String string) {
        for (char sign : string.toCharArray()) {
            if (!Character.isDigit(sign)) {
                return false;
            }
        }
        return true;
    }
}
