package com.tikeysoft.vmd_sensorsimulator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class HelloApplication extends Application {
    private Condition condition;
    private Production production;

    @Override
    public void start(Stage primaryStage) {
        try {
            MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("user1");
            options.setPassword("1".toCharArray());
            client.connect(options);

            condition = new Condition(client);
            production = new Production(client);

            Button startConditionButton = new Button("Start Condition");
            Button endConditionButton = new Button("End Condition");
            Button productionButton = new Button("Production");

            startConditionButton.setOnAction(e -> {
                condition.start();
                updateButtonStates(startConditionButton, endConditionButton, productionButton);
            });

            endConditionButton.setOnAction(e -> {
                condition.stop();
                updateButtonStates(startConditionButton, endConditionButton, productionButton);
            });

            productionButton.setOnAction(e -> {
                production.call();
                updateButtonStates(startConditionButton, endConditionButton, productionButton);
            });

            condition.setRunningStateListener(isRunning -> Platform.runLater(() ->
                    updateButtonStates(startConditionButton, endConditionButton, productionButton)
            ));

            production.setRunningStateListener(isRunning -> Platform.runLater(() ->
                    updateButtonStates(startConditionButton, endConditionButton, productionButton)
            ));

            VBox vbox = new VBox(10, startConditionButton, endConditionButton, productionButton);
            Scene scene = new Scene(vbox, 300, 200);

            primaryStage.setTitle("MQTT Control Panel");
            primaryStage.setScene(scene);
            primaryStage.show();

            updateButtonStates(startConditionButton, endConditionButton, productionButton);

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateButtonStates(Button startButton, Button stopButton, Button productionButton) {
        boolean isConditionRunning = condition.isRunning();
        boolean isProductionRunning = production.isRunning();
        startButton.setDisable(isConditionRunning);
        stopButton.setDisable(!isConditionRunning);
        productionButton.setDisable(isProductionRunning);
    }

    public static void main(String[] args) {
        launch(args);
    }
}