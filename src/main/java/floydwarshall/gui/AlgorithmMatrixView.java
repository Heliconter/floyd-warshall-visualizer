package floydwarshall.gui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

class AlgorithmMatrixView extends Region {
    public AlgorithmMatrixView() {
        // Temporary filler:
        setMinHeight(250);
        setMinWidth(400);
        getChildren().add(new BorderPane(new Text("Graph matrix")));
    }
}
