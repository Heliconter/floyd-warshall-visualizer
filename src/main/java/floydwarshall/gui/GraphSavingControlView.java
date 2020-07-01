package floydwarshall.gui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

class GraphSavingControlView extends Region {
    public GraphSavingControlView() {
        // Temporary filler:
        setMinHeight(100);
        getChildren().add(new BorderPane(new Text("Save graph + Load graph buttons")));
    }
}
