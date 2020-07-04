package floydwarshall.gui;

import floydwarshall.executor.ExecutorInterface;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Gui {
    static public final int PADDING = 10;
    static public final int SPACING = PADDING / 2;

    static public Region createVPusher() {
        Region vPusher = new Region();
        VBox.setVgrow(vPusher, Priority.ALWAYS);
        return vPusher;
    }

    static public Region createHPusher() {
        Region hPusher = new Region();
        HBox.setHgrow(hPusher, Priority.ALWAYS);
        return hPusher;
    }

    public void start(Stage primaryStage) {
        Label graphLabel = new Label("Graph");
        Label algorithmLabel = new Label("Algorithm");
        GraphView graphView = new GraphView();
        AlgorithmMatrixView algorithmMatrixView = new AlgorithmMatrixView();
        AlgorithmControlView algorithmControlView = new AlgorithmControlView();

        VBox graphLayout = new VBox(graphLabel, graphView);
        VBox algorithmLayout = new VBox(algorithmLabel, algorithmMatrixView, createVPusher(), algorithmControlView);
        GridPane mainLayout = new GridPane();
        mainLayout.add(graphLayout, 0, 0);
        mainLayout.add(algorithmLayout, 1, 0);
        BorderPane root = new BorderPane(mainLayout);

        VBox.setVgrow(graphView, Priority.ALWAYS);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        column1.setHgrow(Priority.ALWAYS);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        column2.setHgrow(Priority.ALWAYS);
        mainLayout.getColumnConstraints().addAll(column1, column2);
        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);
        mainLayout.getRowConstraints().addAll(row);

        mainLayout.setHgap(SPACING);
        mainLayout.setPadding(new Insets(PADDING));
        algorithmLayout.setPadding(new Insets(PADDING));
        algorithmLayout.setSpacing(SPACING);
        algorithmLayout.setAlignment(Pos.CENTER);
        graphLayout.setPadding(new Insets(PADDING));
        graphLayout.setSpacing(SPACING);
        graphLayout.setAlignment(Pos.CENTER);

        graphLabel.setPadding(new Insets(0, 0, SPACING, 0));
        algorithmLabel.setPadding(new Insets(0, 0, SPACING, 0));

        final Border border = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        graphLayout.setBorder(border);
        algorithmLayout.setBorder(border);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Floyd-Warshall visualizer");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.show();
    }

    private ExecutorInterface executor;
    public void setExecutor(ExecutorInterface executor) {
        this.executor = executor;
    }
}
