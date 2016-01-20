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
public abstract class Monitor extends Application implements IMonitor {

//    private final Image PlayButtonImage = new Image(Monitor.class.getResourceAsStream("playbutton.png"));
    public Label currentValue;
    public Label stateLabel;
    protected MasterExecutor executor;
    protected int duration;
    protected int maxTime;
    protected int stopAt;

    public Monitor() {
        duration = 1000;
        maxTime = 100;
        stopAt = 100;
    }

    @Override
    public void durationCallBack(int duration) {
        Platform.runLater(() -> durationValue.setText(Long.toString(duration)));
    }

    @Override
    public void timeCallBack(int time) {
        Platform.runLater(() -> currentValue.setText(Long.toString(time)));
    }

    @Override
    public void stateCallBack(State state) {
        Platform.runLater(() -> stateLabel.setText(state.toString()));

    }

    protected abstract MasterExecutor newExecutor();
    public TextField durationValue;
    private TextField stopActValue;
    private TextField maxTimeValue;

    private void load() {
        executor = newExecutor();
        executor.start(maxTime, duration, stopAt);
    }

    private void issueCommand(MasterExecutor.Command command) {
        duration = Integer.parseInt(durationValue.getText());
        maxTime = Integer.parseInt(maxTimeValue.getText());
        stopAt = Integer.parseInt(stopActValue.getText());
        executor.setRunParams(maxTime, duration, stopAt);
        executor.command(command);
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
        maxTimeValue = new TextField(Long.toString(maxTime));
        maxTimeValue.setPrefColumnCount(5);
//        Button gotoButton = new Button("Stop At");
        Label stopAtLabel = new Label("Stop At");
        stopActValue = new TextField(Long.toString(maxTime));
        stopActValue.setPrefColumnCount(5);
        durationValue = new TextField(Long.toString(duration));
        durationValue.setPrefColumnCount(4);

        stateLabel = new Label("Click Load!");
        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> {
            if (executor != null) {
                if (executor.getState() != MasterExecutor.State.Stopped) {
                    executor.command(LocalExecutor.Command.Stop);
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
                issueCommand(MasterExecutor.Command.Run);
            }
        });

        Button stepButton = new Button("Step Run");
        stepButton.setOnAction(e -> {
            if (executor != null) {
                issueCommand(MasterExecutor.Command.StepRun);
            }
        });

        Button stopButton = new Button("Stop");
        stopButton.setOnAction(e -> {
            if (executor != null) {
                issueCommand(MasterExecutor.Command.Stop);
            }
        });

        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(e -> {
            if (executor != null) {
                issueCommand(MasterExecutor.Command.Pause);
            }
        });

        Button fastButton = new Button("Fast");
        fastButton.setOnAction(e -> {
            if (executor != null) {
                issueCommand(MasterExecutor.Command.FastForward);
            }

        });
        Button slowButton = new Button("Slow");
        slowButton.setOnAction(e -> {
            if (executor != null) {
                issueCommand(MasterExecutor.Command.SlowForward);
            }
        });
        Button speedButton = new Button("Speed");
        speedButton.setOnAction(e -> {
            if (executor != null) {
                issueCommand(MasterExecutor.Command.Speed);
            }
        });

        hBox.getChildren().addAll(stateLabel, loadButton, currentLabel, currentValue, maxTimeLabel, maxTimeValue, runButton, stepButton, stopAtLabel, stopActValue,
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
            executor.command(LocalExecutor.Command.Stop);
        }
    }

    @Override
    public abstract void process(Agent[] agents);

    @Override
    public abstract void process(World world);

    private TabPane tabPane;
}
