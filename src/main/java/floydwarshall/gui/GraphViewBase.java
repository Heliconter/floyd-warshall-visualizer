package floydwarshall.gui;

import floydwarshall.executor.ExecutorInterface;
import floydwarshall.gravity.GravitySimulation;
import floydwarshall.gui.graphshapes.Line;
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

public class GraphViewBase extends VBox {
    ExecutorInterface executor;

    enum PROGRAM_STATE {
        ADD, DRAG, DELETE, ADD_LINES, DELETE_LINES, EDIT
    }

    protected PROGRAM_STATE state = PROGRAM_STATE.ADD;
    protected Node dragNode = null;
    protected ArrayList<Line> listLines = new ArrayList<>();
    protected Line currentLine = null;
    protected boolean isChouseNodeFirstForAddLines = false;
    protected boolean isDragState = false;
    protected boolean isDeleteState = false;

    protected Pane pane;
    protected ScrollPane scrollPane;

    protected ArrayList<Node> listNodes = new ArrayList<>();

    protected Label label;
    protected TextField textField;
    protected Button updateButton;
    protected Line editLine = null;
    protected Button random;

    protected GravitySimulation gravitySimulation;
    protected GravityCenterPoint gravityCenter;

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

    public GraphViewBase(ExecutorInterface executor) {
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
        random = new Button("random");
        updateButton = new Button("update");

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

        updateButton.setVisible(false);

        HBox inform = new HBox(label, textField, updateButton);
        inform.setPadding(insetForInformBox);
        HBox.setMargin(label, insetForLabel);
        HBox.setMargin(textField, insetForTextField);
        HBox.setMargin(updateButton, insetForTextField);


        getChildren().addAll(buttonBox, scrollPane, inform);
    }

    private void setState(PROGRAM_STATE state) {
        this.state = state;
        if (state != PROGRAM_STATE.EDIT) {
            hideEditElements();
        } else {
            showEditElements();
        }
    }

    protected void hideEditElements() {
        if (label != null) {
            label.setVisible(false);
        }
        if (textField != null) {
            textField.setVisible(false);
        }
        if (updateButton != null) {
            updateButton.setVisible(false);
        }
    }

    private void showEditElements() {
        if (label != null) {
            label.setVisible(true);
        }
        if (textField != null) {
            textField.setVisible(true);
        }
        if (updateButton != null) {
            updateButton.setVisible(true);
        }
    }
}

