package floydwarshall.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

class AlgorithmControlView extends HBox {
    public AlgorithmControlView() {
        Button step = new Button("Step");
        Button toEnd = new Button("To end");
        getChildren().addAll(step, toEnd);
        setSpacing(Gui.SPACING);
        setAlignment(Pos.CENTER_RIGHT);
    }
}
