package floydwarshall.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;

class AlgorithmMatrixView extends VBox {
    public AlgorithmMatrixView() {
        TableView tableView = new TableView();
        tableView.setPlaceholder(new Label("No vertices to display"));
        getChildren().addAll(tableView);
        setSpacing(Gui.SPACING);
        setAlignment(Pos.CENTER);
    }
}
