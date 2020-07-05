package floydwarshall.gui;

import floydwarshall.executor.ExecutorInterface;
import floydwarshall.executor.PathEnds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

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
            HBox vHeader = new HBox();
            vHeader.setAlignment(Pos.CENTER_RIGHT);
            vHeader.getChildren().add(new Label("" + (char)('A' + i)));
            Label hHeader = new Label("" + (char)('A' + i));
            setFillWidth(vHeader, true);
            setFillHeight(hHeader, true);
            vHeader.setPadding(new Insets(Gui.SPACING));
            vHeader.setStyle("-fx-border-width: 0 1 0 0; -fx-border-color: black;");
            hHeader.setPadding(new Insets(Gui.SPACING));
            hHeader.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: black;");
            add(vHeader, 0, i + 1);
            add(hHeader, i + 1, 0);
        }

        for (int from = 0; from < verticesAmount; from++) {
            for (int to = 0; to < verticesAmount; to++) {
                Integer pathLength = executor.getPathLength(new PathEnds(from, to));
                Label cell = new Label();
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
