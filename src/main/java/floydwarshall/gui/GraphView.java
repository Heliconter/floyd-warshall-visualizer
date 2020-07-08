package floydwarshall.gui;

import floydwarshall.executor.PathEnds;
import floydwarshall.gui.graphshapes.LocalPoint;
import floydwarshall.saveload.EdgeInfrom;
import floydwarshall.saveload.GraphInform;
import floydwarshall.saveload.NodeInform;
import floydwarshall.saveload.SaveLoadManager;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import floydwarshall.executor.ExecutorInterface;
import floydwarshall.executor.Edge;
import floydwarshall.gravity.GravitySimulation;
import floydwarshall.gui.graphshapes.Line;
import floydwarshall.gui.graphshapes.Math;
import floydwarshall.gui.graphshapes.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GraphView extends VBox {
    ExecutorInterface executor;
    Stage stage;

    enum PROGRAM_STATE {
        ADD, DRAG, DELETE, ADD_LINES, DELETE_LINES
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

    private GravitySimulation gravitySimulation;
    private GravityCenterPoint gravityCenter;
    private SaveLoadManager manager;


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

    public GraphView(ExecutorInterface executor, Stage stage) {
        this.executor = executor;
        this.stage = stage;
        manager = new SaveLoadManager();

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
        Button saveButton = new Button("save");
        Button loadButton = new Button("load");

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
        loadButton.setOnMouseClicked((MouseEvent event) -> {
            loadGraphFromFile();
        });
        saveButton.setOnMouseClicked((MouseEvent event) -> {
            saveGraphInFile();
        });

        Insets insetForButton = new Insets(0, 0, 0, 5);
        Insets insetForButtonBox = new Insets(0, 0, 5, 0);
        Insets insetForButtonBox2 = new Insets(5, 0, 5, 0);


        HBox buttonBox = new HBox(11, button, button2, button3, button4, button5);
        HBox.setMargin(button, insetForButton);
        buttonBox.setPadding(insetForButtonBox);
        HBox.setMargin(buttonBox, insetForButtonBox);


        HBox buttonBox2 = new HBox(11, loadButton, saveButton, random);
        HBox.setMargin(button, insetForButton);
        buttonBox2.setPadding(insetForButtonBox2);
        HBox.setMargin(buttonBox, insetForButtonBox);

        getChildren().addAll(buttonBox, scrollPane, buttonBox2);

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
                    node.setIndex(listNodes.size());
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
                        line.addObserver(this::edgeChanged);
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

    private void edgeChanged(Line edge) {
        notifyGraphChanged();
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

        for (Node node : listNodes) {
            node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                showPathLabels(((Node) event.getSource()).getIndex());
            });
        }
        for (Node node : listNodes) {
            node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                hidePathLabels();
            });
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
            listNodes.get(i).setIndex(i);
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

    private String getNodeName() {
        return String.valueOf((char) ('A' + listNodes.size()));
    }

    private ArrayList<LocalPoint> getPoints(int countNodes){
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
        return points;
    }

    private void setRandomGraph(int countNodes, int countEdges) {
        if (pane == null) return;

        if (countEdges > countNodes * (countNodes - 1)) {
            countEdges = countNodes * (countNodes - 1);
        }


        ArrayList<LocalPoint> points = getPoints(countNodes);

        deleteGraph();

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

    private ArrayList<Node> getListUnrelatedNodes(Node mainNode) {
        ArrayList<Node> list = new ArrayList<>();
        for (Node node : listNodes) {
            if (node != mainNode) {
                list.add(node);
            }
        }
        return list;
    }

    private void deleteGraph() {
        pane.getChildren().clear();
        listLines.clear();
        listNodes.clear();
        dragNode = null;
        currentLine = null;
        isChouseNodeFirstForAddLines = false;
        isDragState = false;
        isDeleteState = false;
    }

    private Node getNode(String name) {
        for (Node node : listNodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }

    private void loadGraphFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.txt"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            GraphInform graphInform = manager.loadGraphFromFile(file.getPath());
            if (graphInform != null && graphInform.isDataReadCorrect()) {
                deleteGraph();
                ArrayList<NodeInform> nodes = graphInform.getNodes();
                ArrayList<EdgeInfrom> edges = graphInform.getEdges();
                ArrayList<LocalPoint> points = getPoints(nodes.size());

                for (int i = 0; i < nodes.size(); i++) {
                    Node node = new Node(points.get(i).x, points.get(i).y);
                    pane.getChildren().add(node);
                    node.setName(String.valueOf(nodes.get(i).name));
                    node.setIndex(listNodes.size());
                    listNodes.add(node);
                }

                for (EdgeInfrom edge : edges) {
                    Node endNode = getNode(String.valueOf(edge.nameStartNode));
                    Node startNode = getNode(String.valueOf(edge.nameEndNode));
                    if (endNode != null && startNode != null) {
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
                        line.setWeight(edge.weight);
                        line.getWeightText().setText(String.valueOf(edge.weight));
                        pane.getChildren().add(line.getTriangle());
                        pane.getChildren().add(line.getWeightText());
                    }
                }
                for (Node node : listNodes) {
                    node.drawFront();
                }
                notifyGraphChanged();
            } else {
                showAlert("Error message", "File is incorrect.");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getInformationAboutGraph() {
        StringBuilder stringBuilder = new StringBuilder("Nodes:");
        stringBuilder.append(listNodes.size()).append("\n");
        for (Node node : listNodes) {
            stringBuilder.append("(").append(node.getName()).append(")\n");
        }
        stringBuilder.append("Lines:").append(listLines.size()).append("\n");
        for (Line line : listLines) {
            stringBuilder.append("(").append(line.getStartNodeName()).append(",")
                    .append(line.getEndNodeName()).append(",")
                    .append(line.getWeight()).append(")\n");
        }
        stringBuilder.replace(stringBuilder.length(), stringBuilder.length(), "");
        return stringBuilder.toString();
    }

    private void saveGraphInFile() {
        if (listNodes.size() > 0) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.txt"));
            File file = fileChooser.showSaveDialog(stage);
            if (file!=null) {
                if (manager.saveGraphInFile(file.getAbsolutePath(), getInformationAboutGraph())) {
                    showAlert("File save", "The file save successfully.");
                } else {
                    showAlert("File save", "The file was not saved.");
                }
            }
        } else {
            showAlert("File save", "The file was not saved. Graph is empty");
        }
    }

    private void setState(PROGRAM_STATE state) {
        this.state = state;
    }
}
