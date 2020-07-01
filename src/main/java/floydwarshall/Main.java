package floydwarshall;

import floydwarshall.executor.ExecutorInterface;
import floydwarshall.gui.Gui;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Gui gui = new Gui();
        ExecutorInterface executor = null; // TODO
        gui.setExecutor(executor);
        gui.start(primaryStage);
    }
    public static void main(String[] args) {
        Gui.launch(args);
    }
}
