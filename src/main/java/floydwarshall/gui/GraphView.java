package floydwarshall.gui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

class GraphView  extends Region {
    public GraphView() {
        // Temporary filler:
        setMinHeight(400);
        setMinWidth(400);
        getChildren().add(new BorderPane(new Text("Graph view")));
    }
}
