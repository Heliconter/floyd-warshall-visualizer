package floydwarshall.gui;

import floydwarshall.executor.ExecutorInterface;
import floydwarshall.executor.PathEnds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

class AlgorithmMatrixView extends GridPane {
    private ExecutorInterface executor;

    public AlgorithmMatrixView(ExecutorInterface executor) {
        this.executor = executor;

        executor.addObserver(this::updateMatrix);
        updateMatrix();

        setAlignment(Pos.CENTER);
    }

    private void updateMatrix() {
        getChildren().clear();
        int verticesAmount = executor.getVerticesAmount();

        for (int i = 0; i < verticesAmount; i++) {
            Label vHeader = new Label("" + (char)('A' + i));
            Label hHeader = new Label("" + (char)('A' + i));
            vHeader.setFont(new Font(16));
            hHeader.setFont(new Font(16));
            GridPane.setHalignment(vHeader, HPos.RIGHT);
            GridPane.setValignment(hHeader, VPos.BOTTOM);
            GridPane.setFillWidth(vHeader, true);
            GridPane.setFillHeight(hHeader, true);
            vHeader.setPadding(new Insets(Gui.SPACING));
            vHeader.setStyle("-fx-border-width: 0 1 0 0; -fx-border-color: black;");
            hHeader.setPadding(new Insets(Gui.SPACING));
            hHeader.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: black;");
            add(vHeader, 0, i + 1);
            add(hHeader, i + 1, 0);
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
            add(firstCellLayout, 0, 0);
        }
        else {
            Label noVerticesLabel = new Label("No vertices to visualize");
            noVerticesLabel.setMinHeight(100);
            add(noVerticesLabel, 0, 0);
        }

        for (int from = 0; from < verticesAmount; from++) {
            for (int to = 0; to < verticesAmount; to++) {
                Integer pathLength = executor.getPathLength(new PathEnds(from, to));
                Label cell = new Label();
                cell.setFont(new Font(16));
                if (pathLength == null)
                    cell.setText("∞");
                else
                    cell.setText(pathLength.toString());
                HBox cellLayout = new HBox(cell);
                cellLayout.setAlignment(Pos.CENTER);
                add(cellLayout, to + 1, from + 1);
            }
        }
    }
}
