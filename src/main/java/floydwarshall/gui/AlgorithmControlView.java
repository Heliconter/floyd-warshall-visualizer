package floydwarshall.gui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

class AlgorithmControlView extends Region {
    public AlgorithmControlView() {
        // Temporary filler:
        setMinHeight(100);
        getChildren().add(new BorderPane(new Text("Algorithm-control buttons")));
    }
}
