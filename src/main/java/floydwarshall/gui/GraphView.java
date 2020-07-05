package floydwarshall.gui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import floydwarshall.gui.graphshapes.Line;
import floydwarshall.gui.graphshapes.Math;
import floydwarshall.gui.graphshapes.Node;
import java.util.ArrayList;
import java.util.Collections;

public class GraphView extends VBox {

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


    public GraphView() {
        pane = new Pane();
        pane.setPrefWidth(700);
        pane.setPrefHeight(700);

        scrollPane = new ScrollPane(pane);
        scrollPane.prefWidthProperty().bind(this.widthProperty());
        scrollPane.prefHeightProperty().bind(this.heightProperty());


        Button button = new Button("add");
        Button button2 = new Button("drag");
        Button button3 = new Button("delete");
        Button button4 = new Button("add line");
        Button button5 = new Button("delete line");
        Button button6 = new Button("edit");
        updateButton = new Button("update");

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
        button6.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Edit press");
                state = PROGRAM_STATE.EDIT;
            }
        });
        updateButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (state == PROGRAM_STATE.EDIT) {
                    String newWeight = textField.getText();
                    if (isDigitString(newWeight)) {
                        editLine.getWeightText().setText(newWeight);
                        editLine.setWeight(Integer.valueOf(newWeight));
                        setTextOnLabel(editLine.getStartNodeName(), editLine.getEndNodeName(), editLine.getWeightText().getText());
                    }
                }
            }
        });

        Insets insetForButton = new Insets(0, 0, 0, 5);
        Insets insetForButtonBox = new Insets(0, 0, 5, 0);
        Insets insetForInformBox = new Insets(10, 0, 0, 0);
        Insets insetForLabel = new Insets(3, 0, 0, 0);
        Insets insetForTextField = new Insets(0, 0, 0, 30);


        HBox buttonBox = new HBox(11, button, button2, button3, button4, button5, button6);
        HBox.setMargin(button, insetForButton);
        buttonBox.setPadding(insetForButtonBox);
        HBox.setMargin(buttonBox, insetForButtonBox);


        label = new Label("");
        label.setPrefWidth(180);

        textField = new TextField();
        textField.setPrefWidth(50);

        HBox inform = new HBox(label, textField, updateButton);
        inform.setPadding(insetForInformBox);
        HBox.setMargin(label, insetForLabel);
        HBox.setMargin(textField, insetForTextField);
        HBox.setMargin(updateButton, insetForTextField);


        getChildren().addAll(buttonBox, scrollPane, inform);

        pane.setOnMousePressed((MouseEvent event) ->
        {
            if (state == PROGRAM_STATE.ADD) {
                if (listNodes.size() <= 25) {
                    Node node = new Node(event.getX(), event.getY());
                    pane.getChildren().addAll(node.getEllipse(), node.getText()/*,node.getTriangle()*/);
                    node.setName(getNodeName());
                    listNodes.add(node);
                }
            }
            if (state == PROGRAM_STATE.DELETE) {
                if (listNodes.size() > 0) {
                    Node node = findDragEllipse(event.getX(), event.getY());
                    if (node != null) {
                        //node.deleteNode(this);
                        deleteNode(node);
                        listNodes.remove(node);
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
                System.out.println("edit");
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
                    }
                }
            }
        });

        pane.setOnMouseReleased((MouseEvent event) -> {
            if (state == PROGRAM_STATE.ADD_LINES) {
                if (isChouseNodeFirstForAddLines) {
                    if (listNodes.size() > 0) {
                        Node node = findDragEllipse(event.getX(), event.getY());
                        if (node != null && !isDublicateLine(currentLine, new Point2D(node.getEllipse().getCenterX(), node.getEllipse().getCenterY()))
                                && !currentLine.isStartNode(node)) {
                            node.addLineEndPoint(currentLine);
                            updateLineEndPoint(node.getEllipse().getCenterX(), node.getEllipse().getCenterY(), currentLine);
                            currentLine.setTriangle();
                            setConvexOnLines(currentLine);
                            listLines.add(currentLine);
                            currentLine.setEndNode(node);
                            pane.getChildren().add(currentLine.getTriangle());
                            pane.getChildren().add(currentLine.getWeightText());
                            currentLine = null;
                            isChouseNodeFirstForAddLines = false;
                            node.drawFront();
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
                }
            }
            if (state == PROGRAM_STATE.DELETE_LINES) {
                if (isDeleteState) {
                    isDeleteState = false;
                }
            }
        });
    }

    private void deleteNode(Node node) {
        pane.getChildren().remove(node.getEllipse());
        pane.getChildren().remove(node.getText());
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
        char currentChar = 'A';
        ArrayList<String> namesNode = new ArrayList<>();
        for (Node node : listNodes) {
            namesNode.add(node.getName());
        }
        Collections.sort(namesNode);
        for (String name : namesNode) {
            if (name.toCharArray()[0] > currentChar) {
                break;
            } else {
                currentChar++;
            }
        }
        return String.valueOf(currentChar);
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
