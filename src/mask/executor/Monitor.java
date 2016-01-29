/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import mask.world.World;
import mask.agent.Agent;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import mask.executor.MasterExecutor.State;

/**
 *
 * @author zj
 */
public abstract class Monitor extends Application implements IExecutorCallBack {

//    private final Image PlayButtonImage = new Image(Monitor.class.getResourceAsStream("playbutton.png"));
    public Label currentValue;
    public Label stateLabel;
    protected MasterExecutor executor;

    public Monitor() {
    }

    @Override
    public void time(int time) {
        Platform.runLater(() -> currentValue.setText(Long.toString(time)));
    }

    @Override
    public void state(State state) {
        Platform.runLater(() -> stateLabel.setText(state.toString()));

    }

    protected abstract MasterExecutor newExecutor();
    public TextField durationValue;
    private TextField pauseAtValue;
    private TextField maxTimeValue;

    private void load() {
        executor = newExecutor();
        executor.setPauseAt(1);
        executor.threadStart(Integer.parseInt(maxTimeValue.getText()));
    }

    @Override
    public void start(Stage stage) throws Exception {

        BorderPane root = new BorderPane();
        HBox hBox = new HBox();

        Label currentLabel = new Label("Current");
        currentValue = new Label("0");
        currentValue.setPrefWidth(35);
        currentValue.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        Label maxTimeLabel = new Label("Total");
        maxTimeValue = new TextField(Long.toString(200));
        maxTimeValue.setPrefColumnCount(5);
//        Button gotoButton = new Button("Stop At");
        Label stopAtLabel = new Label("Stop At");
        pauseAtValue = new TextField(Long.toString(200));
        pauseAtValue.setPrefColumnCount(5);
        durationValue = new TextField(Long.toString(500));
        durationValue.setPrefColumnCount(4);

        stateLabel = new Label("Click Load!");
        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> {
            if (executor != null) {
                if (executor.getState() != MasterExecutor.State.Stopped) {
                    executor.stop();
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                tabPane.getTabs().clear();
                tabPane.getTabs().addAll(createTabs());
            }
            load();
        });

        Button runButton = new Button("Run");
        runButton.setOnAction(e -> {
            if (executor != null) {
                executor.setPauseAt(Integer.parseInt(pauseAtValue.getText()));
                executor.setMaxTime(Integer.parseInt(maxTimeValue.getText()));
                executor.setDuration(Integer.parseInt(durationValue.getText()));
                executor.resume();
            }
        });

        Button stepButton = new Button("Step Run");
        stepButton.setOnAction(e -> {
            if (executor != null) {
                executor.setMaxTime(Integer.parseInt(maxTimeValue.getText()));
                executor.setDuration(Integer.parseInt(durationValue.getText()));
                executor.stepRun();
            }
        });

        Button stopButton = new Button("Stop");
        stopButton.setOnAction(e -> {
            if (executor != null) {
                executor.stop();
            }
        });

        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(e -> {
            if (executor != null) {
                executor.pause();
            }
        });

        Button fastButton = new Button("Fast");
        fastButton.setOnAction(e -> {
            if (executor != null) {
                int duration = Integer.parseInt(durationValue.getText());
                if (duration > 0) {
                    duration = duration / 2;
                }
                durationValue.setText(Integer.toString(duration));
                executor.speed(duration);
            }

        });
        Button slowButton = new Button("Slow");
        slowButton.setOnAction(e -> {
            if (executor != null) {
                int duration = Integer.parseInt(durationValue.getText());
                if (duration > 0) {
                    duration = duration * 2;
                } else {
                    duration = 1000;
                }
                durationValue.setText(Integer.toString(duration));
                executor.speed(duration);
            }
        });
        Button speedButton = new Button("Speed");
        speedButton.setOnAction(e -> {
            if (executor != null) {
                executor.speed(Integer.parseInt(durationValue.getText()));
            }
        });

        hBox.getChildren().addAll(stateLabel, loadButton, currentLabel, currentValue, maxTimeLabel, maxTimeValue, runButton, stepButton, stopAtLabel, pauseAtValue,
                stopButton, pauseButton, fastButton, slowButton, speedButton, durationValue);

        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);

        root.setBottom(hBox);

        tabPane = new TabPane(createTabs());
        root.setCenter(tabPane);
        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setTitle("Distributed Simulator for Multi-Agent System");
        stage.show();
    }

    protected abstract Tab[] createTabs();

    @Override
    public void stop() {
        if (executor != null) {
            executor.stop();
        }
    }

    @Override
    public abstract void agents(Agent[] agents);

    @Override
    public abstract void world(World world);

    private TabPane tabPane;
}
