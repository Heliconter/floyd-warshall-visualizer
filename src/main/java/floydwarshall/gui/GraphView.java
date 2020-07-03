package floydwarshall.gui;

import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

class GraphView  extends Region {
    public GraphView() {
        // Temporary filler:
        Text graphView = new Text("Graph view");
        Text saveOrOpenControls = new Text("Save/Open to/from file buttons");
        Region vPusher = new Region();
        VBox.setVgrow(vPusher, Priority.ALWAYS);
        VBox layout = new VBox(graphView, vPusher, saveOrOpenControls);
        layout.setMinHeight(400);
        layout.setMinWidth(400);
        getChildren().add(layout);
    }
}
