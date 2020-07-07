package floydwarshall.gui;

import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import floydwarshall.executor.ExecutorInterface;
import floydwarshall.gui.graphshapes.Line;
import floydwarshall.gui.graphshapes.Math;
import floydwarshall.gui.graphshapes.Node;

import java.util.ArrayList;

class GraphView extends GraphViewBase {

    GraphView(ExecutorInterface executor) {
        super(executor);

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


}
