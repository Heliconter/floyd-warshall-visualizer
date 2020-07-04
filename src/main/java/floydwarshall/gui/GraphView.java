package floydwarshall.gui;

import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import floydwarshall.gravity.GravitySimulation;
import floydwarshall.gui.graphshapes.Line;
import floydwarshall.gui.graphshapes.Math;
import floydwarshall.gui.graphshapes.Node;

import java.util.ArrayList;

public class GraphView extends Region {

//    private Group group_for_shapes = new Group();
    enum PROGRAM_STATE {ADD, DRAG, DELETE, ADD_LINES, DELETE_LINES}
    private PROGRAM_STATE state = PROGRAM_STATE.ADD;
    private Node dragNode = null;
    private ArrayList<Line> listLines = new ArrayList<>();
    private Line currentLine = null;
    private boolean isChouseNodeFirstForAddLines = false;
    private boolean isDragState = false;
    private boolean isDeleteState = false;

    private GravitySimulation gravitySimulation;

    private Pane pane;
    private ScrollPane scrollPane;

    private ArrayList<Node> lisNodes = new ArrayList<>();

    public GraphView() {
       /* Label graphPlaceholder = new Label("Graph placeholder"); // TODO
        graphPlaceholder.setPrefSize(500, 500);
        graphPlaceholder.setStyle("-fx-background-color: rgb(255, 255, 255);");
        graphPlaceholder.setAlignment(Pos.CENTER);*/

       /* Button save = new Button("Save");
        Button load = new Button("Load"); // TODO Add click handlers

        HBox saveOrLoadControlsLayout = new HBox(save, load);
        saveOrLoadControlsLayout.setSpacing(Gui.SPACING);*/

        //setAlignment(Pos.CENTER);
//        getChildren().addAll(graphPlaceholder, Gui.createVPusher(), saveOrLoadControlsLayout);

        /*minWidth(500);
        minHeight(500);*/
        gravitySimulation = new GravitySimulation();

        pane = new Pane();
        pane.setPrefWidth(700);
        pane.setPrefHeight(700);

        scrollPane = new ScrollPane(pane);
        scrollPane.prefWidthProperty().bind(this.widthProperty());
        scrollPane.prefHeightProperty().bind(this.heightProperty());

        this.getChildren().add(scrollPane);

        Button button = new Button("add");
        Button button2 = new Button("drag");
        Button button3 = new Button("delete");
        Button button4 = new Button("add line");
        Button button5 = new Button("delete line");


        button.setLayoutX(50); // потом когда-нибудь поставить нормальные значения
        button2.setLayoutX(90);
        button3.setLayoutX(-3);
        button4.setLayoutX(134);
        button5.setLayoutX(198);

        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                state = PROGRAM_STATE.ADD;
            }
        });
        button2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                state = PROGRAM_STATE.DRAG;
            }
        });
        button3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                state = PROGRAM_STATE.DELETE;
            }
        });
        button4.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                state = PROGRAM_STATE.ADD_LINES;
            }
        });
        button5.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                state = PROGRAM_STATE.DELETE_LINES;
            }
        });

        this.getChildren().addAll(button, button2, button3, button4, button5);

        pane.setOnMousePressed((MouseEvent event) ->
        {
            if (state == PROGRAM_STATE.ADD) {
                Node node = new Node(event.getX(), event.getY());
                pane.getChildren().addAll(node.getEllipse(), node.getText()/*,node.getTriangle()*/);
                lisNodes.add(node);
                gravitySimulation.updateAdjacencyMatrix(lisNodes, listLines);
            }
            if (state == PROGRAM_STATE.DELETE) {
                if (lisNodes.size() > 0) {
                    Node node = findDragEllipse(event.getX(), event.getY());
                    if (node != null) {
                        //node.deleteNode(this);
                        deleteNode(node);
                        lisNodes.remove(node);
                        gravitySimulation.updateAdjacencyMatrix(lisNodes, listLines);
                    }
                }
            }
            if (state == PROGRAM_STATE.ADD_LINES) {
                if (!isChouseNodeFirstForAddLines) {
                    Node node = findDragEllipse(event.getX(), event.getY());
                    if (node != null) {
                        Line line = new Line(node.getEllipse().getCenterX(), node.getEllipse().getCenterY(),
                                node.getEllipse().getCenterX(), node.getEllipse().getCenterY(),
                                node.getEllipse().getCenterX(), node.getEllipse().getCenterY());
                        line.setFill(null);
                        line.setStroke(Color.BLACK);
                        line.setStrokeWidth(1);
                        line.setFromNode(node);
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
                    if (lisNodes.size() > 0) {
                        Node node = findDragEllipse(event.getX(), event.getY());
                        if (node != null) {
                            dragNode = node;
                            dragNode.setAffectedByGravity(false);
                            isDragState = true;
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
                        listLines.remove(line);
                        gravitySimulation.updateAdjacencyMatrix(lisNodes, listLines);
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
                                inverse.updateTriangle();
                            }
                        }
                    }
                }
            }
        });

        pane.setOnMouseReleased((MouseEvent event) -> {
            if (state == PROGRAM_STATE.ADD_LINES) {
                if (isChouseNodeFirstForAddLines) {
                    if (lisNodes.size() > 0) {
                        Node node = findDragEllipse(event.getX(), event.getY());
                        if (node != null && !isDublicateLine(currentLine, new Point2D(node.getEllipse().getCenterX(), node.getEllipse().getCenterY()))) {
                            currentLine.setToNode(node);
                            node.addLineEndPoint(currentLine);
                            updateLineEndPoint(node.getEllipse().getCenterX(), node.getEllipse().getCenterY(), currentLine);
                            currentLine.setTriangle();
                            setConvexOnLines(currentLine);
                            listLines.add(currentLine);
                            pane.getChildren().add(currentLine.getTriangle());
                            currentLine = null;
                            isChouseNodeFirstForAddLines = false;
                            node.drawFront();
                            gravitySimulation.updateAdjacencyMatrix(lisNodes, listLines);
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
                    dragNode = null;
                    isDragState = false;
                    dragNode.setAffectedByGravity(true);
                }
            }
            if (state == PROGRAM_STATE.DELETE_LINES) {
                if (isDeleteState) {
                    isDeleteState = false;
                }
            }
        });

        Timeline timer = new Timeline(
                new KeyFrame(Duration.millis(1000 / 30), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        gravitySimulation.simulationStep(lisNodes);
                    }
                }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

    }

    private void deleteNode(Node node) {
        pane.getChildren().remove(node.getEllipse());
        pane.getChildren().remove(node.getText());
        for (Line line : node.getLinesStartPoint()) {
            pane.getChildren().remove(line);
            pane.getChildren().remove(line.getTriangle());
        }
        for (Line line : node.getLinesEndPoint()) {
            pane.getChildren().remove(line);
            pane.getChildren().remove(line.getTriangle());

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
        for (Node el : lisNodes) {
            double localMin = (java.lang.Math.abs(el.getEllipse().getCenterX() - x) + java.lang.Math.abs(el.getEllipse().getCenterY() - y));
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
                line.updateTriangle();
                cur.updateTriangle();
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

}
