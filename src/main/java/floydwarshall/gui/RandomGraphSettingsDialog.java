package floydwarshall.gui;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;

class RandomGraphSettingsDialog extends Dialog {
    private Slider verticesSlider;
    private Slider edgesSlider;

    static private int countMaxEdgesAmount(int verticesAmount) {
        if (verticesAmount < 2)
            return 0;
        return verticesAmount > 2 ? verticesAmount * 2 : 2;
    }

    private void setUpSlider(Slider slider) {
        slider.setBlockIncrement(1);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.setShowTickLabels(true);
        slider.setMinWidth(600);
    }

    private void updateMaxEdgesAmount() {
        edgesSlider.setMax(countMaxEdgesAmount(getVerticesAmount()));
        edgesSlider.setValue(getVerticesAmount());
    }

    public RandomGraphSettingsDialog() {
        verticesSlider = new Slider(2, 15, 8);
        edgesSlider = new Slider();
        edgesSlider.setMin(2);
        updateMaxEdgesAmount();
        verticesSlider.valueProperty().addListener((observable, oldValue, newValue) -> updateMaxEdgesAmount());
        setUpSlider(verticesSlider);
        setUpSlider(edgesSlider);

        Label verticesLabel = new Label("Vertices:");
        Label edgesLabel = new Label("Edges:");

        GridPane grid = new GridPane();
        grid.add(verticesLabel, 0, 0);
        grid.add(edgesLabel, 0, 1);
        grid.add(verticesSlider, 1, 0);
        grid.add(edgesSlider, 1, 1);
        grid.setHgap(Gui.SPACING);
        GridPane.setHalignment(verticesLabel, HPos.RIGHT);
        GridPane.setValignment(verticesLabel, VPos.TOP);
        GridPane.setHalignment(edgesLabel, HPos.RIGHT);
        GridPane.setValignment(edgesLabel, VPos.TOP);
        GridPane.setHgrow(verticesSlider, Priority.ALWAYS);
        GridPane.setHgrow(edgesSlider, Priority.ALWAYS);

        initModality(Modality.NONE);
        setTitle("Set random graph settings");
        getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

        getDialogPane().setContent(grid);
    }

    public int getVerticesAmount() {
        return (int)verticesSlider.getValue();
    }
    public int getEdgesAmount() {
        return (int)edgesSlider.getValue();
    }
}
