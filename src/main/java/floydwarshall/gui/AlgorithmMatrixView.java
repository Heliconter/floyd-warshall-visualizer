package floydwarshall.gui;

import floydwarshall.executor.ExecutorInterface;
import floydwarshall.executor.PathEnds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

class AlgorithmMatrixView extends VBox {
    private ExecutorInterface executor;
    private GridPane matrix;
    private HBox legendLayout;

    public AlgorithmMatrixView(ExecutorInterface executor) {
        this.executor = executor;

        matrix = new GridPane();

        Label legend = new Label("Legend:");
        Label fromKLabel = new Label("from-k");
        fromKLabel.setBackground(new Background(new BackgroundFill(fromKColor, CornerRadii.EMPTY, Insets.EMPTY)));
        Label kToLabel = new Label("k-to");
        kToLabel.setBackground(new Background(new BackgroundFill(kToColor, CornerRadii.EMPTY, Insets.EMPTY)));
        Label fromToLabel = new Label("from-to");
        fromToLabel.setBackground(new Background(new BackgroundFill(fromToColor, CornerRadii.EMPTY, Insets.EMPTY)));
        legendLayout = new HBox(legend, fromKLabel, kToLabel, fromToLabel);

        getChildren().addAll(matrix, legendLayout);

        matrix.setAlignment(Pos.CENTER);
        legendLayout.setAlignment(Pos.CENTER);
        legendLayout.setSpacing(Gui.SPACING);
        setAlignment(Pos.CENTER);
        setSpacing(Gui.PADDING);

        executor.addObserver(this::updateMatrix);
        updateMatrix();
    }

    private final Color fromKColor = new Color(0, 0, 1, 0.1);
    private final Color kToColor = new Color(0, 0, 1, 0.2);
    private final Color fromToColor = new Color(1, 0.7, 0, 0.3);

    private HBox addColoredBackgroundFragment(HBox layout, Color color) {
        if (layout == null)
            layout = new HBox();
        Region r = new Region();
        r.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        HBox.setHgrow(r, Priority.ALWAYS);
        layout.getChildren().add(r);
        return layout;
    }
    private HBox createBackground(int from, int to) {
        HBox layout = null;
        if (from == executor.getFrom() && to == executor.getTo())
            layout = addColoredBackgroundFragment(layout, fromToColor);
        if (from == executor.getFrom() && to == executor.getK())
            layout = addColoredBackgroundFragment(layout, fromKColor);
        if (from == executor.getK() && to == executor.getTo())
            layout = addColoredBackgroundFragment(layout, kToColor);
        return layout;
    }

    private void updateMatrix() {
        matrix.getChildren().clear();
        int verticesAmount = executor.getVerticesAmount();

        for (int i = 0; i < verticesAmount; i++) {
            Label vHeader = new Label("" + (char)('A' + i));
            Label hHeaderLabel = new Label("" + (char)('A' + i));
            HBox hHeader = new HBox(hHeaderLabel);
            vHeader.setFont(new Font(16));
            hHeaderLabel.setFont(new Font(16));
            hHeader.setAlignment(Pos.CENTER);
            GridPane.setHalignment(vHeader, HPos.RIGHT);
            GridPane.setValignment(hHeader, VPos.BOTTOM);
            GridPane.setFillWidth(vHeader, true);
            GridPane.setFillHeight(hHeader, true);
            vHeader.setPadding(new Insets(Gui.SPACING));
            vHeader.setStyle("-fx-border-width: 0 1 0 0; -fx-border-color: black;");
            hHeader.setPadding(new Insets(Gui.SPACING));
            hHeader.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: black;");
            matrix.add(vHeader, 0, i + 1);
            matrix.add(hHeader, i + 1, 0);
        }

        if (verticesAmount != 0) {
            StackPane firstCellLayout = new StackPane();
            firstCellLayout.setMinSize(32, 32);
            Label fromLabel = new Label("from");
            fromLabel.setFont(new Font(10));
            StackPane.setAlignment(fromLabel, Pos.BOTTOM_CENTER);
            Label toLabel = new Label("to");
            toLabel.setFont(new Font(10));
            StackPane.setAlignment(toLabel, Pos.CENTER_RIGHT);
            firstCellLayout.getChildren().addAll(fromLabel, toLabel);
            matrix.add(firstCellLayout, 0, 0);
            legendLayout.setVisible(true);
        }
        else {
            Label noVerticesLabel = new Label("No vertices to visualize");
            noVerticesLabel.setMinHeight(100);
            matrix.add(noVerticesLabel, 0, 0);
            legendLayout.setVisible(false);
        }

        for (int from = 0; from < verticesAmount; from++) {
            for (int to = 0; to < verticesAmount; to++) {
                Integer pathLength = executor.getPathLength(new PathEnds(from, to));
                Label cell = new Label();
                cell.setPadding(new Insets(Gui.SPACING));
                cell.setFont(new Font(16));
                if (pathLength == null)
                    cell.setText("∞");
                else
                    cell.setText(pathLength.toString());
                GridPane.setHalignment(cell, HPos.CENTER);

                HBox background = createBackground(from, to);
                if (background != null)
                    matrix.add(background, to + 1, from + 1);

                matrix.add(cell, to + 1, from + 1);
            }
        }
    }
}
