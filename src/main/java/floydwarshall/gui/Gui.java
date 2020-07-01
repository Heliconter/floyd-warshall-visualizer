package floydwarshall.gui;

import floydwarshall.executor.ExecutorInterface;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Gui extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        GraphView graphView = new GraphView();
        AlgorithmMatrixView algorithmMatrixView = new AlgorithmMatrixView();
        AlgorithmControlView algorithmControlView = new AlgorithmControlView();
        GraphSavingControlView graphSavingControlView = new GraphSavingControlView();

        Region pusher = new Region();
        HBox.setHgrow(pusher, Priority.ALWAYS);

        HBox viewsLayout = new HBox(graphView, algorithmMatrixView);
        HBox controlsLayout = new HBox(graphSavingControlView, pusher, algorithmControlView);
        VBox mainLayout = new VBox(viewsLayout, controlsLayout);
        Group root = new Group(mainLayout);

        mainLayout.setPadding(new Insets(10));
        mainLayout.setSpacing(10);
        viewsLayout.setSpacing(10);
        controlsLayout.setSpacing(10);

        final Border border = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        graphView.setBorder(border);
        algorithmMatrixView.setBorder(border);
        controlsLayout.setBorder(border);

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
