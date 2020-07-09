package floydwarshall.gui;

import floydwarshall.executor.ExecutorInterface;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.Timer;
import java.util.TimerTask;

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

        setRepeatingOnHoldingAction(step, () -> executor.step(1));
        setRepeatingOnHoldingAction(back, () -> executor.step(-1));
        toEnd.setOnAction(actionEvent -> {
            executor.toEnd();
        });
    }

    static private void setRepeatingOnHoldingAction(Button button, Runnable action) {
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, pressEvent -> {
            action.run();
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {action.run();});
                }
            }, 500, 150);
            button.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>(){
                public void handle(MouseEvent event){
                    timer.cancel();
                    button.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
                }
            });
        });
    }
}
