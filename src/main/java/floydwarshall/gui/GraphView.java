package floydwarshall.gui;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import floydwarshall.MyShape.MyLine;
import floydwarshall.MyShape.MyMath;
import floydwarshall.MyShape.MyNode;

import java.util.ArrayList;

public class GraphView  extends Region {

//    private Group group_for_shapes = new Group();
    enum PROGRAM_STATE {ADD, DRAG, DELETE, ADD_LINES, DELETE_LINES}
    private PROGRAM_STATE state = PROGRAM_STATE.ADD;
    private MyNode dragNode = null;
    private ArrayList<MyLine> listLines = new ArrayList<>();
    private MyLine currentLine = null;
    private boolean isChouseNodeFirstForAddLines = false;
    private boolean isDragState = false;
    private boolean isDeleteState = false;

    private ArrayList<MyNode> lisNodes = new ArrayList<>();

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
                System.out.println("add press");
                state = PROGRAM_STATE.ADD;
            }
        });
        button2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("drag press");
                state = PROGRAM_STATE.DRAG;
            }
        });
        button3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("delete press");
                state = PROGRAM_STATE.DELETE;
            }
        });
        button4.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("add lines press");
                state = PROGRAM_STATE.ADD_LINES;
            }
        });
        button5.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("delete lines press");
                state = PROGRAM_STATE.DELETE_LINES;
            }
        });

        getChildren().addAll(button, button2, button3, button4, button5);

        this.setOnMousePressed((MouseEvent event) ->
        {
            System.out.println("press mouse");
            if (state == PROGRAM_STATE.ADD) {
                MyNode node = new MyNode(event.getX(), event.getY());
                getChildren().addAll(node.getEllipse(), node.getText()/*,node.getTriangle()*/);
                lisNodes.add(node);
            }
            if (state == PROGRAM_STATE.DELETE) {
                if (lisNodes.size() > 0) {
                    MyNode node = findDragEllipse(event.getX(), event.getY());
                    if (node != null) {
                        System.out.println("delete");
                        //node.deleteNode(this);
                        deleteNode(node);
                        lisNodes.remove(node);
                    }
                }
            }
            if (state == PROGRAM_STATE.ADD_LINES) {
                if (!isChouseNodeFirstForAddLines) {
                    MyNode node = findDragEllipse(event.getX(), event.getY());
                    if (node != null) {
                        MyLine line = new MyLine(node.getEllipse().getCenterX(), node.getEllipse().getCenterY(),
                                node.getEllipse().getCenterX(), node.getEllipse().getCenterY(),
                                node.getEllipse().getCenterX(), node.getEllipse().getCenterY());
                        line.setFill(null);
                        line.setStroke(Color.BLACK);
                        line.setStrokeWidth(1);
                        node.addLineStartPoint(line);
                        getChildren().add(line);
                        currentLine = line;
                        isChouseNodeFirstForAddLines = true;
                        node.drawFront();
                    }
                }
            }
            if (state == PROGRAM_STATE.DRAG) {
                if (!isDragState) {
                    if (lisNodes.size() > 0) {
                        MyNode node = findDragEllipse(event.getX(), event.getY());
                        if (node != null) {
                            dragNode = node;
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

        this.setOnMouseDragged((MouseEvent event) -> {
            if (state == PROGRAM_STATE.DRAG) {
                System.out.println("drag");

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
                    System.out.println("delete lines");
                    double x = event.getSceneX();
                    double y = event.getSceneY();
                    ArrayList<MyLine> deleteLine = new ArrayList<>();
                    for (MyLine line : listLines) {
                        if (MyMath.isDeleteLine(line, x, y)) {
                            deleteLine.add(line);
                        }
                    }
                    for (MyLine line : deleteLine) {
                        getChildren().remove(line);
                        getChildren().remove(line.getTriangle());
                        listLines.remove(line);
                        MyNode node1 = findDragEllipse(line.getStartX(), line.getStartY());
                        MyNode node2 = findDragEllipse(line.getEndX(), line.getEndY());
                        if (node1 != null) {
                            node1.popLineStartPoint(line);
                        }
                        if (node2 != null) {
                            node2.popLineEndPoint(line);
                        }
                        if (line.isConvex()) {
                            MyLine inverse = isIverseLine(line);
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

        this.setOnMouseReleased((MouseEvent event) -> {
            System.out.println("released");
            if (state == PROGRAM_STATE.ADD_LINES) {
                if (isChouseNodeFirstForAddLines) {
                    if (lisNodes.size() > 0) {
                        MyNode node = findDragEllipse(event.getX(), event.getY());
                        if (node != null && !isDublicateLine(currentLine, new Point2D(node.getEllipse().getCenterX(), node.getEllipse().getCenterY()))) {
                            node.addLineEndPoint(currentLine);
                            updateLineEndPoint(node.getEllipse().getCenterX(), node.getEllipse().getCenterY(), currentLine);
                            currentLine.setTriangle();
                            setConvexOnLines(currentLine);
                            listLines.add(currentLine);
                            getChildren().add(currentLine.getTriangle());
                            currentLine = null;
                            isChouseNodeFirstForAddLines = false;
                            node.drawFront();
                        } else {
                            getChildren().remove(currentLine);
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
                }
            }
            if (state == PROGRAM_STATE.DELETE_LINES) {
                if (isDeleteState) {
                    isDeleteState = false;
                }
            }
        });
    }

    private void deleteNode(MyNode node) {
        getChildren().remove(node.getEllipse());
        getChildren().remove(node.getText());
        for (MyLine line : node.getLinesStartPoint()) {
            getChildren().remove(line);
            getChildren().remove(line.getTriangle());
        }
        for (MyLine line : node.getLinesEndPoint()) {
            getChildren().remove(line);
            getChildren().remove(line.getTriangle());

        }
    }

    private void updateLineEndPoint(double end_x,
                                    double end_y,
                                    MyLine line) {
        line.setEndX(end_x);
        line.setEndY(end_y);
    }

    private MyNode findDragEllipse(double x, double y) {
        MyNode node = null;
        double min = 1000000;
        for (MyNode el : lisNodes) {
            double localMin = (Math.abs(el.getEllipse().getCenterX() - x) + Math.abs(el.getEllipse().getCenterY() - y));
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

    private void setConvexOnLines(MyLine line) {
        for (MyLine cur : listLines) {
            if (line.getStartX() == cur.getEndX() && line.getStartY() == cur.getEndY() &&
                    line.getEndX() == cur.getStartX() && line.getEndY() == cur.getStartY()) {
                line.setConvex(true);
                cur.setConvex(true);
                MyMath.setControlPoint(line, cur);
                line.updateTriangle();
                cur.updateTriangle();
                break;
            }
        }
    }

    private boolean isDublicateLine(MyLine line, Point2D point) {

        for (MyLine cur : listLines) {
            if (cur.getStartX() == line.getStartX() && cur.getStartY() == line.getStartY() &&
                    cur.getEndX() == point.getX() && cur.getEndY() == point.getY()) {
                System.out.println("Dublicate line found");
                return true;
            }
        }
        System.out.println("Dublicate line not found");

        return false;
    }

    private MyLine isIverseLine(MyLine line) {

        for (MyLine cur : listLines) {
            if (cur.getStartX() == line.getEndX() && cur.getStartY() == line.getEndY() &&
                    cur.getEndX() == line.getStartX() && cur.getEndY() == line.getStartY()) {
                System.out.println("Inverse line found");
                return cur;
            }
        }
        System.out.println("Inverse line not found");
        return null;
    }

}
