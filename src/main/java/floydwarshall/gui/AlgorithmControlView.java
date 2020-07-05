package floydwarshall.gui;

import floydwarshall.executor.ExecutorInterface;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

class AlgorithmControlView extends HBox {
    private ExecutorInterface executor;
    public AlgorithmControlView(ExecutorInterface executor) {
        this.executor = executor;

        Button back = new Button("Back");
        Button step = new Button("Step");
        Button toEnd = new Button("To end");
        getChildren().addAll(back, step, toEnd);
        setSpacing(Gui.SPACING);
        setAlignment(Pos.CENTER_RIGHT);

        back.setOnAction(actionEvent -> {
            executor.step(-1);
        });
        step.setOnAction(actionEvent ->  {
            executor.step(1);
        });
        toEnd.setOnAction(actionEvent -> {
            executor.toEnd();
        });
    }
}
