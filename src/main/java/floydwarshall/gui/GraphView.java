package floydwarshall.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

class GraphView  extends VBox {
    public GraphView() {
        Label graphPlaceholder = new Label("Graph placeholder"); // TODO
        graphPlaceholder.setPrefSize(300, 300);
        graphPlaceholder.setStyle("-fx-background-color: rgb(255, 255, 255);");
        graphPlaceholder.setAlignment(Pos.CENTER);

        Button save = new Button("Save");
        Button load = new Button("Load"); // TODO Add click handlers

        HBox saveOrLoadControlsLayout = new HBox(save, load);
        saveOrLoadControlsLayout.setSpacing(Gui.SPACING);

        setAlignment(Pos.CENTER);
        getChildren().addAll(graphPlaceholder, Gui.createVPusher(), saveOrLoadControlsLayout);
    }
}
