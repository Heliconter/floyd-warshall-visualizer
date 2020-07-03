package floydwarshall.gui;

import floydwarshall.executor.ExecutorInterface;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Gui {
    public final int PADDING = 10;

    public void start(Stage primaryStage) throws Exception {
        GraphView graphView = new GraphView();
        AlgorithmMatrixView algorithmMatrixView = new AlgorithmMatrixView();
        AlgorithmControlView algorithmControlView = new AlgorithmControlView();

        Region vPusher = new Region();
        VBox.setVgrow(vPusher, Priority.ALWAYS);

        VBox algorithmLayout = new VBox(algorithmMatrixView, vPusher, algorithmControlView);
        HBox mainLayout = new HBox(graphView, algorithmLayout);
        Group root = new Group(mainLayout);

        mainLayout.setPadding(new Insets(PADDING));
        mainLayout.setSpacing(PADDING);
        algorithmLayout.setSpacing(PADDING);

        final Border border = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        graphView.setBorder(border);
        algorithmLayout.setBorder(border);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Floyd-Warshall visualizer");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private ExecutorInterface executor;
    public void setExecutor(ExecutorInterface executor) {
        this.executor = executor;
    }
}
